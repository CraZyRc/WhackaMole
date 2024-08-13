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
import whackamole.whackamole.Main;
import whackamole.whackamole.RS.Types.IRewardInteractType;
import whackamole.whackamole.RS.Types.IRewardType;
import whackamole.whackamole.RS.Types.IRewardWaitableType;
import whackamole.whackamole.Utils.Econ;

import whackamole.whackamole.Utils.Logger;
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
    private List<IRewardType> rewardTypes;
    private Queue<IRewardType> rewards = new LinkedList<>();
    private Optional<IRewardType> current;
    private Player player;
    private Entity entity;
    private Game game;
    private Object _lock = new Object();
    private Location loc;
    private int timer = 0;
    private int i = 0;
    private int rewardSize = 0;

    protected RewardExecutor(List<IRewardType> rewards) {
        this.rewardTypes = new ArrayList<>(rewards);
        this.state = State.Ready;
    }

    /**
     * Set the game which this Executor is running for
     * @param game
     * @return {@link RewardExecutor}
     */
    protected RewardExecutor setGame(Game game) {
        this.game = game;
        int random;

        for (var reward : this.rewardTypes) {
            random = new Random().nextInt(100);
            if (this.game.getRunning().get().score >= reward.getThreshold() && random <= reward.getRewardChance()) {
                this.rewards.add(reward);
            }
        }
        for (var reward : this.rewards) {
            if (reward instanceof IRewardInteractType) {
                this.rewardSize++;
            }
        }
        return this;
    }

    /**
     * Set the player this executor is running for
     * @param player
     * @return {@link RewardExecutor}
     */
    protected RewardExecutor setPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Get the current state this executor is in
     * @return {@link State}
     */
    protected State getState() {
        return this.state;
    }

    /**
     * Sets the State to Running and runs the first Tick
     */
    public void Start() {
        Econ econ = new Econ();
        if (this.Has()) {
            this.state = State.Running;
            this.loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(2).setY(0));
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
                break;
            case Completed:
                this.game.setState(gameState.READY);
                this.state = State.ForRemoval;
                break;
            default:
                break;
        }
    }

    boolean Has() {
        if (this.rewards.isEmpty()) {
            this.state = State.Completed;
            return false;
        } else return true;
    }

    void setTimer(int seconds) {
        // * Set the timer to 5 seconds of ticks (20 ticks in a second)
        this.timer = seconds * 20;
    }
    boolean stepTimer() {
        this.timer -= 1;
        if (this.timer < 1) {
            this.timer = 0;
            return true;
        }
        return false;
    }

    void Next() {
        var next = this.rewards.poll();
        if (next == null) {
            this.i = 0;
            this.entity.remove();
            this.state = State.Completed;
        } else if (next instanceof IRewardInteractType) {
            this.i++;
        }
        this.current = Optional.ofNullable(next);
    }

    void startExecution() {
        this.current.ifPresent((reward) -> {
            if (Config.Game.PLAYERLOCK) {
                this.loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(2).setY(0));
            }
            if (!(reward instanceof IRewardWaitableType)) {
                reward.Execute(this.player);
            } else {
                this.setTimer(((IRewardWaitableType) reward).getTimer());
                this.state = State.Waiting;
                Logger.info("Reward display " + reward);
                ((IRewardWaitableType) reward).displayType(this.main, this.loc.clone());
                if (reward instanceof IRewardInteractType) {
                    this.entity = this.displayCount(this.i, this.rewardSize);
                }
            }
        });
    }

    void stopExecution() {
        synchronized (_lock) {
            if (this.state != State.Waiting) return;
            this.current.ifPresent((reward) -> {
                reward.Execute(this.player);

                this.state = State.Running;

                if (reward instanceof IRewardInteractType interact) {
                    this.entity.remove();
                    interact.Remove(this.player);
                }
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
        Logger.info("locChange");
        Logger.info(this.loc + "");
        this.loc = loc.clone().add(0, 2, 0).add(player.getEyeLocation().getDirection().multiply(2).setY(0)); // TODO: fix direction (this code gets executed before the teleport occurs, so the player its looking direction is the one from before the tp. Maybe add Looking direction to the Tp type?
        Logger.info(this.loc + "");
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
            if (reward instanceof IRewardInteractType interact) {
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
