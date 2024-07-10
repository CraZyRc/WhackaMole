package whackamole.whackamole.RS.Types;

import org.bukkit.entity.Player;
import whackamole.whackamole.Utils.YMLFile;

import java.util.LinkedHashMap;

public interface RewardType {

    RewardType Load(LinkedHashMap Settings);
    boolean Check();
    void Execute(Player player);
}
