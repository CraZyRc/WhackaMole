package whackamole.whackamole.Rewards;

import whackamole.whackamole.Rewards.Types.*;
import whackamole.whackamole.Rewards.Types.CurrencyType;
import whackamole.whackamole.Utils.YMLFile;

import java.util.*;

public class Reward {
    public int Threshold;
    public String Name;
    public String Animation;
    public List<String> Games;
    public static List<RewardType> rewards = new ArrayList<>();
    public List<String> rewardTypes = Arrays.asList(
            "Item"
            ,   "Effect"
            ,   "Teleport"
            ,   "Currency"
            ,   "Message"
            ,   "Sound"
            ,   "Advancement"
    );

    public Reward(YMLFile rewardsFile) {
        for (String key : rewardsFile.FileConfig.getConfigurationSection("Rewards").getValues(false).keySet()) {
            Name = key;
            Threshold = rewardsFile.getInt("Rewards." + key + "Threshold");
            Animation = rewardsFile.getString("Rewards." + key + "Animation");
            Games = (List<String>) rewardsFile.getList("Rewards." + key + "Games");
            rewards.add(new CurrencyType());
            rewards.add(new EffectType());
            rewards.add(new ItemType());
            rewards.add(new MessageType());
            rewards.add(new SoundType());
            rewards.add(new TeleportType());

        }
    }

}
