package whackamole.whackamole.RS;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.YMLFile;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    private Particle particle = Particle.DUST;
    private static List<AnimationCommand> Commands = new ArrayList<>();

    public Animation(String Animation) {
        YMLFile animationFile = new YMLFile(Config.AppConfig.storageFolder + "/animations/" + Animation + ".yml");
        for (Object commandString : animationFile.getList("Animation")) {
            Commands.add(new AnimationCommand((String) commandString, Animation));
        }
    }

    public void spawnAnimation(Location location) {
        for (var CMD : Commands) {
            Location loc = location.clone().add(new Vector(CMD.Delta1, CMD.Delta2, CMD.Delta3).rotateAroundY(location.getYaw())); //TODO: fix rotation of Animation
            location.getWorld().spawnParticle(this.particle, loc, CMD.Count, CMD.offSetX, CMD.offSetY, CMD.offSetZ, CMD.Speed, new Particle.DustOptions(Color.fromRGB(Math.round(CMD.colorRed * 255.0F), Math.round(CMD.colorGreen * 255.0F), Math.round(CMD.colorBlue * 255.0F)), CMD.Scale));
        }
    }

    public static void onReload() {
        if (!Commands.isEmpty()) Commands.clear();
    }
}

