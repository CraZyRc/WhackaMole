package whackamole.whackamole.RS;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
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
    BukkitScheduler schedular = Bukkit.getScheduler();
    private int task;

    private Location loc;


    public double Threshold;
    public String Name;
    public String Animation;
    public List<String> Games;
    private List<LinkedHashMap> Types;
    private List<Entity> entities = new ArrayList<>();
    private final List<RewardType> staticTypes;
    private final List<RewardType> interactiveTypes;

    public Reward(YMLFile rewardsFile, String key) {
        this.Name = key;
        this.Threshold = rewardsFile.getDouble("Rewards." + key + ".Threshold");
        this.Animation = rewardsFile.getString("Rewards." + key + ".Animation");
        this.Games = (List<String>) rewardsFile.getList("Rewards." + key + ".Games");
        this.Types = (List<LinkedHashMap>) rewardsFile.getList("Rewards." + key + ".RewardTypes");
        this.staticTypes = new ArrayList<>();
        this.interactiveTypes = new ArrayList<>();

        for (LinkedHashMap typeMap : Types) {
            RewardType rewardType;
            String type = typeMap.get("Type").toString();
            switch (type) {
                default -> {
                    Logger.error(Translator.REWARDS_INVALIDREWARDTYPE.Format(typeMap.get("Type")));
                    continue;
                }
                case "Item"     ->  rewardType = new ItemType().Load((LinkedHashMap) typeMap.get("Settings"));
                case "Currency" ->  rewardType = new CurrencyType().Load((LinkedHashMap) typeMap.get("Settings"));
                case "Effect"   ->  rewardType = new EffectType().Load((LinkedHashMap) typeMap.get("Settings"));
                case "Message"  ->  rewardType = new MessageType().Load((LinkedHashMap) typeMap.get("Settings"));
                case "Sound"    ->  rewardType = new SoundType().Load((LinkedHashMap) typeMap.get("Settings"));
                case "Teleport" ->  rewardType = new TeleportType().Load((LinkedHashMap) typeMap.get("Settings"));
            }

            if (rewardType != null && rewardType.Check()) {
                if (type.equals("Item") || type.equals("Currency") || type.equals("Teleport")) {
                    this.interactiveTypes.add(rewardType);
                } else this.staticTypes.add(rewardType);
            }
        }
    }

    public void Payout(Player player) {
        this.loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().setY(0).normalize());
        for (var reward : this.staticTypes) {
            reward.Execute(player);
        }
        this.interactivePayout(player, 0);

    }

    private void interactivePayout(Player player, int i) {

        entities.add(this.displayCount( i+1, this.interactiveTypes.size()));
        var reward = this.interactiveTypes.get(i);
        reward.displayType(this.main, this.loc.clone());

        task = this.schedular.runTaskLater(main, () -> {
            this.interactiveStop(player, i);
        }, 20L * 5L).getTaskId();
    }

    public void interactiveStop(Player player, int i) {
        if (this.schedular.isCurrentlyRunning(task)) {
            this.schedular.cancelTask(task);
        }
        var reward = this.interactiveTypes.get(i);
        reward.Execute(player);
        reward.Remove(player);
        for (Entity e : entities) {
            e.remove();
        }
        int I = i + 1;
        if (I < this.interactiveTypes.size()) {
            this.interactivePayout(player, I);
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
