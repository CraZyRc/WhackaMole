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
    public List<LinkedHashMap> Types;
    public static List<RewardType> rewardTypes = new ArrayList<>();

    public Reward(YMLFile rewardsFile, String key) {
        Name = key;
        Threshold = rewardsFile.getDouble("Rewards." + key + ".Threshold");
        Animation = rewardsFile.getString("Rewards." + key + ".Animation");
        Games = (List<String>) rewardsFile.getList("Rewards." + key + ".Games");
        Types = (List<LinkedHashMap>) rewardsFile.getList("Rewards." + key + ".RewardTypes");

        for (LinkedHashMap type : Types) {
            if (type.get("Type").equals("Item")) {
                rewardTypes.add(new ItemType().Load((LinkedHashMap) type.get("Settings")));
            } else if (type.get("Type").equals("Currency")) {
                rewardTypes.add(new CurrencyType().Load((LinkedHashMap) type.get("Settings")));
            } else if (type.get("Type").equals("Effect")) {
                rewardTypes.add(new EffectType().Load((LinkedHashMap) type.get("Settings")));
            } else if (type.get("Type").equals("Message")) {
                rewardTypes.add(new MessageType().Load((LinkedHashMap) type.get("Settings")));
            } else if (type.get("Type").equals("Sound")) {
                rewardTypes.add(new SoundType().Load((LinkedHashMap) type.get("Settings")));
            } else if (type.get("Type").equals("Teleport")) {
                rewardTypes.add(new TeleportType().Load((LinkedHashMap) type.get("Settings")));
            }
        }

    }

    public void Payout(Player player) {
        for (var reward : rewardTypes) {
            reward.Execute(player);
        }
    }

}
