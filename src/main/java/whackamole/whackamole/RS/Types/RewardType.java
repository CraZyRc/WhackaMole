package whackamole.whackamole.RS.Types;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import whackamole.whackamole.Main;
import whackamole.whackamole.Utils.YMLFile;

import java.util.LinkedHashMap;

public interface RewardType {

    RewardType Load(LinkedHashMap Settings);
    boolean Check();
    void Execute(Player player);
    void displayType(Main main, Location loc);
    void Remove(Player player);
}
