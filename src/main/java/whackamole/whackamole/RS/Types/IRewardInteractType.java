package whackamole.whackamole.RS.Types;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface IRewardInteractType extends IRewardWaitableType {

    void Remove(Player player);
    Entity getInteractable();
}
