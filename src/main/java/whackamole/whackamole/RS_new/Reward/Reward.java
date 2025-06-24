package whackamole.whackamole.RS_new.Reward;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import whackamole.whackamole.RS_new.RewardsManager;
import whackamole.whackamole.RS_new.Reward.Steps.*;
import whackamole.whackamole.Utils.Translator;

public class Reward {
    public boolean Enabled;
    public boolean UseInteract;
    public int Threshold;
    public int Chance;

    protected ArrayList<RewardStep> Steps = new ArrayList<>();

    static private RewardStep GetStep(StepType type, Map<String, ?> settings) {
        switch (type) {
            case Invalid: return null;
            case Message: return new MessageReward(settings);
            case Teleport: return new TeleportReward(settings);
        }
        return null;
    }

    public Reward(Map<String, ?> rewardMap) {
        var type = StepType.Parse(RewardsManager.getOrDefault(rewardMap, "Type", ""));

        var settings = RewardsManager.<String, Map<String, ?>>getOrDefault(rewardMap, "Settings", null);
        
        this.Enabled    = RewardsManager.getOrDefault(settings, "Enabeld", true);
        this.UseInteract= RewardsManager.getOrDefault(settings, "useinteract", false);
        this.Threshold  = RewardsManager.getOrDefault(settings, "Threshold", -1);
        this.Chance     = RewardsManager.getOrDefault(settings, "Chance", 100);

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

        if (errors.size() > 0) {
            throw new ValidationException(errors);
        }
    }

    public ArrayList<RewardStep> getSteps(int score) {
        if (! this.Enabled) return null;
        if (this.Threshold > score) return null;
        if (this.Chance < new Random().nextInt(100)) return null;

        return this.Steps;
    }
}
