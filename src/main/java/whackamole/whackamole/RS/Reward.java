package whackamole.whackamole.RS;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Main;
import whackamole.whackamole.RS.Types.*;
import whackamole.whackamole.RS.Types.CurrencyType;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

import java.util.*;

public class Reward {
    private final Main main = Main.getPlugin(Main.class);
    BukkitTask task = null;

    private int counter = 0;
    private int i;
    private Player p;
    private Location loc;


    public double Threshold;
    public String Name;
    public String Animation;
    private Animation anim;
    public List<String> Games;
    private List<LinkedHashMap<String, ?>> Types;
    private List<Entity> entities = new ArrayList<>();
    private final List<IRewardType> staticTypes = new ArrayList<>();
    private final List<IRewardType> interactiveTypes = new ArrayList<>();
    private List<IRewardType> interactiveRewards = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Reward(YMLFile rewardsFile, String key) {
        this.Name = key;
        this.Threshold = rewardsFile.getDouble("Rewards." + key + ".Threshold");
        this.Animation = rewardsFile.getString("Rewards." + key + ".Animation");
        this.Games = rewardsFile.getList("Rewards." + key + ".Games");
        this.Types = rewardsFile.getList("Rewards." + key + ".RewardTypes");

        for (var typeMap : Types) {
            IRewardType rewardType;
            String type = typeMap.get("Type").toString();
            LinkedHashMap<String, ?> settings = (LinkedHashMap<String, ?>) typeMap.get("Settings");
            switch (type) {
                case "Item"     ->  rewardType = new ItemType().Load(settings);
                case "Currency" ->  rewardType = new CurrencyType().Load(settings);
                case "Effect"   ->  rewardType = new EffectType().Load(settings);
                case "Message"  ->  rewardType = new MessageType().Load(settings);
                case "Sound"    ->  rewardType = new SoundType().Load(settings);
                case "Teleport" ->  rewardType = new TeleportType().Load(settings);
                default -> {
                    Logger.error(Translator.REWARDS_INVALIDREWARDTYPE.Format(typeMap.get("Type")));
                    continue;
                }
            }

            if (rewardType != null && rewardType.Check()) {
                if (type.equals("Item") || type.equals("Currency") || type.equals("Teleport")) {
                    this.interactiveTypes.add(rewardType);
                } else this.staticTypes.add(rewardType);
            }
        }

        if (!this.Animation.isEmpty()) {
            this.anim = new Animation(this.Animation);
        }
    }

    public void Payout(Player player, Game game) {
        int random;
        this.i = 0;
        this.p = player;
        this.loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().setY(0).normalize());

        if (this.anim != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (counter <= 40) {
                        anim.spawnAnimation(loc);
                        counter++;
                    } else {
                        counter = 0;
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(main, 0L, 1L);
        }

        for (var reward : this.staticTypes) {
            random = new Random().nextInt(100);
            if (reward.getRewardChance() >= random) reward.Execute(player);
        }

        if (!this.interactiveTypes.isEmpty()) {
            for (var v :this.interactiveTypes) {
                random = new Random().nextInt(100);
                if (v.getRewardChance() >= random) this.interactiveRewards.add(v);
            }
            if (!this.interactiveRewards.isEmpty()) this.interactivePayout(game);
            else game.setState(Game.gameState.READY);
        } else game.setState(Game.gameState.READY);

    }

    private void interactivePayout(Game game) {
        IRewardType reward = this.interactiveRewards.get(i);

        entities.add(this.displayCount( this.i+1, this.interactiveRewards.size()));
        reward.displayType(this.main, this.loc.clone());

        this.task =  new BukkitRunnable() {
            @Override
            public void run() {
                interactiveStop(game);
            }
        }.runTaskLater(main, 20L * 5L /*<-- 5 sec delay */);
    }

    public void interactiveStop(Game game) {
        if (!this.task.isCancelled()) {
            this.task.cancel();
        }
        var reward = this.interactiveRewards.get(i);
        reward.Execute(p);
        reward.Remove(p);
        for (Entity e : entities) {
            e.remove();
        }
        this.i++;
        if (this.i < this.interactiveRewards.size()) {
            this.interactivePayout(game);
        } else {
            this.interactiveRewards = new ArrayList<>();
            game.setState(Game.gameState.READY);
        }
    }

    public void checkEntity(Player player, Game game) {
        if (player.equals(p)) {
            this.interactiveStop(game);
        }
    }


    public TextDisplay displayCount(int count, int size) {
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
