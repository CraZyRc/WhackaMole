package whackamole.whackamole.RS;

import org.bukkit.entity.Player;
import whackamole.whackamole.RS.Types.*;
import whackamole.whackamole.RS.Types.CurrencyType;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.YMLFile;

import java.util.*;

public class Reward {
    public double Threshold;
    public String Name;
    public String Animation;
    public List<String> Games;
    private List<LinkedHashMap> Types;
    private final List<RewardType> rewardTypes;

    public Reward(YMLFile rewardsFile, String key) {
        this.Name = key;
        this.Threshold = rewardsFile.getDouble("Rewards." + key + ".Threshold");
        this.Animation = rewardsFile.getString("Rewards." + key + ".Animation");
        this.Games = (List<String>) rewardsFile.getList("Rewards." + key + ".Games");
        this.Types = (List<LinkedHashMap>) rewardsFile.getList("Rewards." + key + ".RewardTypes");
        this.rewardTypes = new ArrayList<>();

        for (LinkedHashMap type : Types) {
            RewardType rewardType = null;
            switch (type.get("Type").toString()) {
                case "Default" -> {
                    Logger.error("Unknown RewardType in: " + type.get("Type")); // TODO: add Translator message
                    continue;
                }
                case "Item"     ->  rewardType = new ItemType().Load((LinkedHashMap) type.get("Settings"));
                case "Currency" ->  rewardType = new CurrencyType().Load((LinkedHashMap) type.get("Settings"));
                case "Effect"   ->  rewardType = new EffectType().Load((LinkedHashMap) type.get("Settings"));
                case "Message"  ->  rewardType = new MessageType().Load((LinkedHashMap) type.get("Settings"));
                case "Sound"    ->  rewardType = new SoundType().Load((LinkedHashMap) type.get("Settings"));
                case "Teleport" ->  rewardType = new TeleportType().Load((LinkedHashMap) type.get("Settings"));
            }
            if (rewardType.Check()) {
                rewardTypes.add(rewardType);
            }
        }
    }

    public void Payout(Player player) {
        for (var reward : rewardTypes) {
            reward.Execute(player);
        }
    }

}
