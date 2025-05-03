package whackamole.whackamole.RS.Types;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface IRewardInteractType extends IRewardWaitableType { // TODO: Rework system? IRewardType and IRewardWaitable are redundant...

    void Remove(Player player);
    Entity getInteractable();
    boolean getEnabled();
}
