package whackamole.whackamole.RS_new.Reward.Steps;

import java.util.Map;

import org.bukkit.Sound;

import whackamole.whackamole.RS_new.RewardExecutorContext;
import whackamole.whackamole.RS_new.RewardsManager;
import whackamole.whackamole.RS_new.Reward.ValidationException;
import whackamole.whackamole.Utils.Translator;

public class SoundReward extends RewardStep {
    private String soundName;
    private float volume;
    private float pitch;
    private Sound sound;

    public SoundReward(Map<String, ?> settings) {
        this.soundName = RewardsManager.getOrDefault(settings, "Sound", "");
        this.volume = RewardsManager.getOrDefault(settings, "Volume", 1F);
        this.pitch = RewardsManager.getOrDefault(settings, "Pitch", 1F);
    }
    
    @Override
    public void Validate() throws ValidationException {
        try {
            this.sound = Sound.valueOf(soundName);
        } catch (Exception ex) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_STRING.Format("Sound"));
        }

        if (this.volume <= 0f) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_INT.Format("Volume", "Volume"));
        }
        
        if (this.pitch <= 0f) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_INT.Format("Pitch", "Pitch"));
        }
    }

    @Override
    public void Execute(RewardExecutorContext context) {
        context.player.playSound(context.player, this.sound, this.volume, this.pitch);
    }
}
