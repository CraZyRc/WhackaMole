package whackamole.whackamole.Rewards.Types;

import org.bukkit.entity.Player;
import whackamole.whackamole.Utils.YMLFile;

public interface RewardType {

    void Load(YMLFile file, String Reward);
    boolean Check();
    void Execute(Player player);
}
