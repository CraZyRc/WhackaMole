package whackamole.whackamole.RS.Types;


import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import whackamole.whackamole.Config;
import whackamole.whackamole.Main;
import whackamole.whackamole.RS.AnimationCommand;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AnimationType implements IRewardWaitableType {
    private Particle Particle = org.bukkit.Particle.REDSTONE;
    private List<AnimationCommand> Commands = new ArrayList<>();
    private YMLFile animationFile;
    private String Animation;
    private Location Loc;
    private int Counter = 0;
    private int Duration;
    private int rewardChance;
    private int Threshold;


    private AnimationType(int threshold, int rewardChance, String animation, int duration) {
        this.Threshold = threshold;
        this.rewardChance = rewardChance;
        this.Animation = animation;
        this.Duration = duration;
        this.animationFile = new YMLFile(Config.AppConfig.storageFolder + "/animations/" + animation + ".yml");
    }

    public static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) {
        var rewardChance = (int) Settings.get("RewardChance");
        var animation = (String) Settings.get("Animation");
        var duration = (int) Settings.get("Duration");

        return new AnimationType(threshold, rewardChance, animation, duration);
    }

    @Override
    public boolean Check() {
        if (this.Animation == null || this.Animation.isEmpty()) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_STRING.Format("Animation"));
            return false;
        } else if ( this.Duration <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Duration", "Duration"));
            return false;
        }  else if (this.rewardChance > 100 || this.rewardChance <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_REWARDCHANCE);
            return false;
        } else if (this.animationFile == null || this.animationFile.getList("Animation").isEmpty()) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_ANIMATIONFILE.Format(this.Animation));
            return false;
        } else {
            for (Object commandString : this.animationFile.getList("Animation")) {
                if (!AnimationCommand.Check((String) commandString, this.Animation)) {
                    return false;
                }
                this.Commands.add(new AnimationCommand((String) commandString));
            }
            return true;
        }
    }

    @Override
    public int getRewardChance() { return this.rewardChance; }

    @Override
    public int getThreshold() { return this.Threshold; }

    @Override
    public int getTimer() { return this.Duration; }

    @Override
    public void Execute(Player player) { // TODO: fix rotation
        var locationV = this.Loc.toVector();
        for (var CMD : this.Commands) {
            var particleV = new Vector(CMD.Delta1, CMD.Delta2, CMD.Delta3);
            var angle = particleV.angle(locationV);
            Logger.info("Partical: %f %f %f %f".formatted(CMD.Delta1, CMD.Delta2, CMD.Delta3, angle));
            particleV.rotateAroundY(angle);
            Location loc = this.Loc.clone().add(particleV);
            this.Loc.getWorld().spawnParticle(this.Particle, loc, CMD.Count, 0, 0, 0, CMD.Speed, new Particle.DustOptions(Color.fromRGB(Math.round(CMD.colorRed * 255.0F), Math.round(CMD.colorGreen * 255.0F), Math.round(CMD.colorBlue * 255.0F)), CMD.Scale));
        }
    }

    @Override
    public void displayType(Main main, Location loc) {
        int duration = this.Duration * 20;
        this.Loc = loc;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Counter <= duration) {
                    Counter++;
                    Execute(null);
                } else {
                    Counter = 0;
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(main, 0L, 1L);

    }
}
