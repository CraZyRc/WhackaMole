package whackamole.whackamole.Rewards.Types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import whackamole.whackamole.Utils.Logger;

public class Teleport {

    public void setTeleport(Player player, String world, double X, double Y, double Z) {
        boolean safeTP = true;
        Location loc = new Location(Bukkit.getWorld(world), X, Y, Z);

        try {
            Location feet = loc.clone();
            if (!feet.getBlock().getType().isTransparent() && !feet.add(0, 1, 0).getBlock().getType().isTransparent()) {
                safeTP = false; // not transparent (will suffocate)
                Logger.error("Cannot send tp from rewards file, tp location isn't safe"); // TODO: add Translator message
                return;
            }
            Location head = feet.add(0, 1, 0);
            if (!head.getBlock().getType().isTransparent()) {
                safeTP = false; // not transparent (will suffocate)
                Logger.error("Cannot send tp from rewards file, tp location isn't safe"); // TODO: add Translator message
                return;
            }
            Location ground = feet.subtract(0, 2, 0);
            safeTP = ground.getBlock().getType().isSolid(); // returns if the ground is solid or not.
        } catch (Exception er) {
            Logger.error(er.getMessage());
        }
        if (safeTP) {
            player.teleport(loc);
        }
    }
}
