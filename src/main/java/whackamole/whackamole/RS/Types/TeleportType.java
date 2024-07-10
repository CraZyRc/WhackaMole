package whackamole.whackamole.RS.Types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import whackamole.whackamole.Utils.Logger;

import java.util.LinkedHashMap;

public class TeleportType implements RewardType {
    private Location loc;
    private World world;
    private double X;
    private double Y;
    private double Z;

    @Override
    public RewardType Load(LinkedHashMap Settings) {
        this.world = Bukkit.getWorld((String) Settings.get("World"));
        this.X = (int) Settings.get("X");
        this.Y = (int) Settings.get("Y");
        this.Z = (int) Settings.get("Z");
        return this;
    }

    @Override
    public boolean Check() {
        this.loc = new Location(world, X, Y, Z);

        if (world == null) {
            Logger.error("Invalid World set in the RewardsFile");
            return false;
        }

        try {
            Location feet = this.loc.clone();
            if (!feet.getBlock().getType().isTransparent() && !feet.add(0, 1, 0).getBlock().getType().isTransparent()) {
                Logger.error("Cannot send tp from rewards file, tp location isn't safe"); // TODO: add Translator message
                return false; // block not transparent (will suffocate)
            }

            Location head = feet.add(0, 1, 0);

            if (!head.getBlock().getType().isTransparent()) {
                Logger.error("Cannot send tp from rewards file, tp location isn't safe"); // TODO: add Translator message
                return false; // block not transparent (will suffocate)
            }
            Location ground = feet.subtract(0, 2, 0);
            return ground.getBlock().getType().isTransparent(); // returns if the ground is solid or not.
        } catch (Exception er) {
            Logger.error(er.getMessage());
        }
        return false;
    }

    @Override
    public void Execute(Player player) {
        if (this.Check()) {
            player.teleport(this.loc);
        }
    }
}
