package whackamole.whackamole.RS_new.Reward.Steps;

import java.util.Map;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import whackamole.whackamole.RS_new.RewardExecutorContext;
import whackamole.whackamole.RS_new.RewardsManager;
import whackamole.whackamole.RS_new.Reward.ValidationException;
import whackamole.whackamole.Utils.Translator;

public class EffectReward extends RewardStep {
    private PotionEffectType effectType;
    private int duration;
    private int amplifier;

    @SuppressWarnings("deprecation")
    public EffectReward(Map<String, ?> settings) {
        var effectName = RewardsManager.getOrDefault(settings, "Effect", "");
        this.duration = RewardsManager.getOrDefault(settings, "Duration", 0);
        this.amplifier = RewardsManager.getOrDefault(settings, "Amplifier", 0);

        if (effectName != null) {
            this.effectType = PotionEffectType.getByName(effectName);
        }
    }
    
    @Override
    public void Validate() throws ValidationException {
        if (this.effectType == null) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_STRING.Format("Effect"));
        }

        if (this.duration <= 0) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_INT.Format("Duration", "Duration"));
        }
        
        if (this.amplifier <= 0) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_INT.Format("Amplifier", "Amplifier"));
        }
    }

    @Override
    public void Execute(RewardExecutorContext context) {
        var effect = new PotionEffect(this.effectType, this.duration, this.amplifier);
        context.player.addPotionEffect(effect);
    }
}
