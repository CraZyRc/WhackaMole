package whackamole.whackamole.RS.Types;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import whackamole.whackamole.Main;

public interface IRewardInteractType extends IRewardWaitableType {

    void displayType(Main main, Location loc);
    void Remove(Player player);
    Entity getInteractable();
}
