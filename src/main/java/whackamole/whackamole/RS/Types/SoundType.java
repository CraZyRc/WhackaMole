package whackamole.whackamole.RS.Types;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import whackamole.whackamole.Main;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

import java.util.LinkedHashMap;

public class SoundType implements IRewardType {
    private Sound sound;
    private float volume;
    private float pitch;
    public int rewardChance = 0;
    public int threshold;

    private SoundType(int threshold, String soundName, float volume, float pitch, int rewardChance)
    {
        this.threshold = threshold;
        this.sound = Sound.valueOf(soundName);
        this.volume = volume;
        this.pitch = pitch;
    }

    public static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) {
        var soundName = (String) Settings.get("Sound");
        var volume = Float.valueOf((String) Settings.get("Volume"));
        var pitch = Float.valueOf((String) Settings.get("Pitch"));
        var rewardChance = (int) Settings.get("RewardChance");
        
        return new SoundType(threshold, soundName, volume, pitch, rewardChance);
    }

    @Override
    public boolean Check() {
        if (this.sound == null) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_STRING.Format("Sound"));
            return false;
        } else if (this.volume <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Volume", "Volume"));
            return false;
        } else if (this.pitch <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Pitch", "Pitch"));
            return false;
        } else if (this.rewardChance > 100 || this.rewardChance <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_REWARDCHANCE);
            return false;
        } else return true;
    }

    @Override
    public int getRewardChance() {
        return this.rewardChance;
    }

    @Override
    public void Execute(Player player) {
        player.playSound(player,this.sound , this.volume, this.pitch);
    }

    @Override
    public void displayType(Main main, Location loc) {}

    @Override
    public void Remove(Player player) {}
}
