package whackamole.whackamole.RS;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.GS.Game.gameState;
import whackamole.whackamole.RS.Reward.Reward;
import whackamole.whackamole.RS.Reward.Steps.IRewardStepInteractable;
import whackamole.whackamole.RS.Reward.Steps.IRewardStepTickable;
import whackamole.whackamole.RS.Reward.Steps.RewardStep;
import whackamole.whackamole.Main;
import whackamole.whackamole.Utils.Econ;

import whackamole.whackamole.Utils.Misc;

public class RewardExecutor {
    private final Main main = Main.getPlugin(Main.class);

    public enum State {
        /** Ready to Start */
        Ready,
        /** Running through the rewards */
        Running,
        /** On interactive reward. Waiting for interact event or till the timer runs out */
        Waiting,
        /** All rewards have been executed */
        Completed,
        /** All post checks are done. Object can be deleted */
        ForRemoval
    }

    private State state;
    private List<Reward> rewards;
    private Queue<RewardStep> steps;
    private Optional<RewardStep> current;
    private Player player;
    private Entity entity;
    private Game game;
    private Object _lock = new Object();
    private Location loc;
    private int timerTicksPassed = -1;
    private int timerTicksLimit = 0;
    private int interactableRewardCounter = 0;
    private int interactableRewardsCount = 0;
    private RewardExecutorContext context = new RewardExecutorContext();

    protected RewardExecutor(List<Reward> rewards) {
        this.rewards = new LinkedList<>(rewards);
        this.state = State.Ready;
        this.context.plugin = main;
    }

    /**
     * Set the game which this Executor is running for
     * @param game
     * @return {@link RewardExecutor}
     */
    protected RewardExecutor setGame(Game game) {
        this.game = game;
        this.FilterGameRewards();
        return this;
    }

    /**
     * Set the player this executor is running for
     * @param player
     * @return {@link RewardExecutor}
     */
    protected RewardExecutor setPlayer(Player player) {
        this.player = player;
        this.context.player = player;
        return this;
    }

    /**
     * Get the current state this executor is in
     * @return {@link State}
     */
    protected State getState() {
        return this.state;
    }

    void FilterGameRewards() {
        this.steps = new LinkedList<RewardStep>();
        for(var reward : this.rewards) {
            var rewardSteps = reward.getSteps(this.game.getRunning().get().score);
            if (rewardSteps != null && !rewardSteps.isEmpty()) {
                this.steps.addAll(rewardSteps);
            }

            if (reward.UseInteract) {
                this.interactableRewardsCount ++;
            }
        }
    }

    /**
     * Sets the State to Running and runs the first Tick
     */
    public void Start() {
        Econ econ = new Econ();
        if (this.Has()) {
            this.state = State.Running;
            this.loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(2).setY(0));
            this.context.location = this.loc;
            this.Tick();
        } else {
            this.game.getRunning().ifPresent((gameRunner) -> {
                econ.depositPlayer(this.player, gameRunner.score);
                RewardsManager.sendScoreToPlayer(player, gameRunner.score);
            });
        }
    }

    /**
     * Runs all the rewards in order.
     *
     * This method must be run every game tick to ensure
     * correct behaviour around the interactive reward types
     *
     */
    public void Tick() {
        switch (this.state) {
            case Running:
                if (Has()) {
                    this.Next();
                    this.startExecution();
                }
                break;
            case Waiting:
                if (this.stepTimer()) this.stopExecution();
                else this.TickExecute();
                break;
            case Completed:
                if (this.entity != null)
                    this.entity.remove();
                
                this.game.setState(gameState.READY);
                this.state = State.ForRemoval;
                break;
            default:
                break;
        }
    }

    boolean Has() {
        if (this.steps.isEmpty()) {
            this.state = State.Completed;
            return false;
        } else return true;
    }

    void setTimer(int seconds) {
        this.timerTicksPassed = 0;
        this.timerTicksLimit = seconds * 20;
    }

    boolean stepTimer() {
        this.timerTicksPassed += 1;
        if (this.timerTicksPassed > this.timerTicksLimit) {
            return true;
        }
        return false;
    }

    void Next() {
        var next = this.steps.poll();
        if (next == null) 
            this.state = State.Completed;
        
        this.current = Optional.ofNullable(next);
    }

    void startExecution() {
        this.current.ifPresent((step) -> {
            if (Config.Game.PLAYERLOCK) {
                this.loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(2).setY(0));
            }
            this.context.location = this.loc;
            step.Execute(this.context);

            if (step instanceof IRewardStepTickable tickable) {
                this.setTimer(tickable.getTimer());
                this.state = State.Waiting;
            }
            if (step instanceof IRewardStepInteractable) {
                this.interactableRewardCounter += 1;
                this.entity = this.displayCount(this.interactableRewardCounter, this.interactableRewardsCount);
            }
        });
    }

    void TickExecute() {
        this.current.ifPresent((reward) -> {
            if (reward instanceof IRewardStepTickable tickable) {
                tickable.ExecuteTick(this.context, this.timerTicksPassed);
            }
        });
    }

    void stopExecution() {
        synchronized (_lock) {
            if (this.state != State.Waiting) return;
            this.current.ifPresent((reward) -> {
                if (reward instanceof IRewardStepTickable tickable) {
                    tickable.ExecuteAfter(this.context);
                }
                if (reward instanceof IRewardStepInteractable) {
                    this.entity.remove();
                }
                this.state = State.Running;
            });
        }
    }

    /**
     * Checks if the player has teleported
     * due to the Teleport Type
     *
     * If so,
     * Sets the new location for the WaitableTypes
     */

    public void onTeleportEvent(Location loc) {
        this.loc = loc.clone().add(loc.getDirection().multiply(2).setY(1.65));
        this.context.location = this.loc;
    }

    /**
     * Checks whether the player interacted entity
     * is the same entity we are waiting on.
     *
     * If the same enity is encounterd then stop
     * the execution of the current reward we are
     * waiting on.
     *
     * @param entity
     * The interacted entity from {@link PlayerInteractEntityEvent}
     */
    public void onInteractEvent(Entity entity) {
        if (this.state != State.Waiting) return;

        this.current.ifPresent((reward) -> {
            if (reward instanceof IRewardStepInteractable interact) {
                if (interact.getInteractable().equals(entity)) {
                    this.stopExecution();
                }
            }
        });
    }

    private TextDisplay displayCount(int count, int size) {
        NamespacedKey namespacedKey = new NamespacedKey(this.main, "CountDisplay");

        Location spawnLoc = loc.clone().add(0, 0.25, 0);
        World World = loc.getWorld();

        final TextDisplay display = (TextDisplay) World.spawnEntity(spawnLoc, EntityType.TEXT_DISPLAY);
        display.setRotation(spawnLoc.getYaw(), 0);
        display.setTransformation(new Transformation(new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f), new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f))); // Translation - leftrot - scale - rightrot
        display.setBillboard(Display.Billboard.FIXED);
        display.setCustomName(Misc.Color(count + "/" + size));
        display.setCustomNameVisible(true);
        display.setPersistent(true);
        display.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 1);
        return display;
    }
}
