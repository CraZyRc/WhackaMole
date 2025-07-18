package whackamole.whackamole.RS.Reward.Steps;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.joml.Vector3d;

import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.RS.Reward.ValidationException;
import whackamole.whackamole.Utils.SafeBlocks;
import whackamole.whackamole.Utils.Translator;

public class TeleportReward extends RewardStep {
    private String worldName;
    private Vector3d locationVector;
    private Location location;
    private RotationDirection direction;

    private enum RotationDirection {
        Invalid     ("",      0f),
        North       ("North",       -180f),
        NorthEast   ("NorthEast",   -135f),
        East        ("East",        -90f),
        SouthEast   ("SouthEast",   -45f),
        South       ("South",       -0f),
        SouthWest   ("SouthWest",   45f),
        West        ("West",        90f),
        NorthWest   ("NorthWest",   135f);

        private String name;
        private float value;

        RotationDirection(String name, float value) {
            this.name = name;
            this.value = value;
        }

        static RotationDirection parse(String name) {
            for (var direction : values()) {
                if (direction.name.equalsIgnoreCase(name)) {
                    return direction;
                }
            }
            return Invalid;
        }

        void applyRotation(Location location) {
            location.setYaw(value);
        }
    }

    public TeleportReward(Map<String, ?> settings) {
        this.worldName = RewardsManager.<String, String>getOrDefault(settings, "World", null);
        var x = Double.parseDouble(String.valueOf(RewardsManager.getOrDefault(settings, "X", 0d)));
        var y = Double.parseDouble(String.valueOf(RewardsManager.getOrDefault(settings, "Y", 0d)));
        var z = Double.parseDouble(String.valueOf(RewardsManager.getOrDefault(settings, "Z", 0d)));
        this.direction = RotationDirection.parse(RewardsManager.<String, String>getOrDefault(settings, "Rotation", null));
        this.locationVector = new Vector3d(x, y, z);
    }
    
    @Override
    public void Validate() throws ValidationException {
        if (this.worldName == null) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_STRING.Format("World"));
        }

        if (this.direction == RotationDirection.Invalid) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_STRING.Format("Direction"));
        }   

        var world = Bukkit.getWorld(this.worldName);
        if (world == null) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_STRING.Format("World"));
        }

        this.location = new Location(world, this.locationVector.x, this.locationVector.y, this.locationVector.z);
        this.direction.applyRotation(this.location);

        var feet = this.location.clone();
        if (SafeBlocks.getUnsafe(feet.getBlock().getType()) || SafeBlocks.getUnsafe(feet.add(0, 1, 0).getBlock().getType())) {
            throw new ValidationException(Translator.REWARDS_TYPE_UNSAFETPLOCATION);
        }
    }

    @Override
    public void Execute(RewardExecutorContext context) {
        context.player.teleport(this.location);
    }
}
