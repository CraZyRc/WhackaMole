package whackamole.whackamole.RS.Types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import whackamole.whackamole.Main;

import java.util.LinkedHashMap;

public interface IRewardType {

    IRewardType Load(LinkedHashMap<String, ?> Settings);
    boolean Check();
    int getRewardChance();

    void Execute(Player player);
    void displayType(Main main, Location loc);
    void Remove(Player player);
}
