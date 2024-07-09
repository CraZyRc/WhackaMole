package whackamole.whackamole.Rewards.Types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.YMLFile;

public class TeleportType implements RewardType {
    private Location loc;
    private World world;
    private double X;
    private double Y;
    private double Z;

    @Override
    public void Load(YMLFile file, String Reward) {
        this.world = file.getWorld(Reward + ".Settings.World");
        this.X = file.getDouble(Reward + ".Settings.X");
        this.Y = file.getDouble(Reward + ".Settings.Y");
        this.Z = file.getDouble(Reward + ".Settings.Z");
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
