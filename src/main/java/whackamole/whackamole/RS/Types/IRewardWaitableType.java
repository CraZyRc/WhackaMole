package whackamole.whackamole.RS.Types;

import org.bukkit.Location;
import whackamole.whackamole.Main;

public interface IRewardWaitableType extends IRewardType {

    void displayType(Main main, Location loc);
    int getTimer();
}
