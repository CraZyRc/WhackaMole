package whackamole.whackamole.Rewards;

import com.google.protobuf.Value;
import whackamole.whackamole.Utils.YMLFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reward {
    private int Threshold;
    private String Name;
    private String rewardType;
    private String Animation;
    private List<String> Games;
    private Map<String, Value> rewards = new HashMap<>();
    private static List<String> rewardTypes = Arrays.asList(
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

        }
    }

}
