package whackamole.whackamole.RS.Types;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

import java.util.LinkedHashMap;

public class SoundType implements IRewardType {
    private Sound Sound;
    private float Volume;
    private float Pitch;
    private int rewardChance;
    private int Threshold;

    private SoundType(int threshold, String soundName, float volume, float pitch, int rewardChance)
    {
        this.Threshold = threshold;
        this.Sound = org.bukkit.Sound.valueOf(soundName);
        this.Volume = volume;
        this.Pitch = pitch;
        this.rewardChance = rewardChance;
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
        if (this.Sound == null) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_STRING.Format("Sound"));
            return false;
        } else if (this.Volume <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Volume", "Volume"));
            return false;
        } else if (this.Pitch <= 0) {
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
    public int getThreshold() { return this.Threshold; }

    @Override
    public void Execute(Player player) {
        player.playSound(player,this.Sound, this.Volume, this.Pitch);
    }

}
