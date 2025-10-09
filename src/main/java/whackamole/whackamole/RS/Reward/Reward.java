package whackamole.whackamole.RS.Reward;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import whackamole.whackamole.RS.Reward.Steps.Types.*;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.RS.Reward.Steps.*;
import whackamole.whackamole.Utils.Translator;

public class Reward {
    public boolean Enabled;
    public boolean UseInteract;
    public int Threshold;
    public int Chance;
    public String Name;

    protected ArrayList<RewardStep> Steps = new ArrayList<>();

    static private RewardStep GetStep(StepType type, Map<String, ?> settings) {
      return switch (type) {
        case Invalid -> null;
        case Message -> new MessageReward(settings);
        case Item -> new ItemReward(settings);
        case Teleport -> new TeleportReward(settings);
        case Sound -> new SoundReward(settings);
        case Effect -> new EffectReward(settings);
        case Currency -> new CurrencyReward(settings);
        case Animation -> new AnimationReward(settings);
          case Custom -> new CustomReward(settings);
      };
    }

    public Reward(Map<String, ?> rewardMap) {
        var type = StepType.Parse(RewardsManager.getOrDefault(rewardMap, "Type", ""));

        var settings = RewardsManager.<String, Map<String, ?>>getOrDefault(rewardMap, "Settings", null);
        
        this.Enabled    = RewardsManager.getOrDefault(settings, "Enabled", true);
        this.UseInteract= RewardsManager.getOrDefault(settings, "UseInteract", false);
        this.Threshold  = RewardsManager.getOrDefault(settings, "Threshold", -1);
        this.Chance     = RewardsManager.getOrDefault(settings, "RewardChance", 100);
        this.Name       = type.toString();

        if (this.UseInteract) {
            this.Steps.add(new InteractRewardStep(type, settings));
        }

        this.Steps.add(GetStep(type, settings));
    }

    public void Validate() throws ValidationException {
        if (this.Chance <= 0 || this.Chance > 100) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_REWARDCHANCE);
        }

        var minimalStepCount = 1;
        if (this.UseInteract) minimalStepCount += 1;

        if (this.Steps.size() < minimalStepCount) { throw new ValidationException(Translator.REWARDS_INVALIDREWARDTYPE);  }

        ArrayList<ValidationException> errors = new ArrayList<>();

        for (RewardStep rewardStep : Steps) {
            try {
                rewardStep.Validate();
            } catch(ValidationException ex) {
                errors.add(ex);
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public ArrayList<RewardStep> getSteps(int score) {
        if (! this.Enabled) return null;
        if (this.Threshold > score) return null;

        return this.Steps;
    }

    public boolean getThreshold(int score) {
        return this.Threshold <= score;
    }
}
