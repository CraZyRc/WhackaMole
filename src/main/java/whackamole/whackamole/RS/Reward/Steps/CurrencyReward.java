package whackamole.whackamole.RS.Reward.Steps;

import java.util.Map;

import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.RS.Reward.ValidationException;
import whackamole.whackamole.Utils.Translator;

public class CurrencyReward extends RewardStep {
    private int quantity;

    public CurrencyReward(Map<String, ?> settings) {
        this.quantity = RewardsManager.getOrDefault(settings, "Quantity", 0);
    }
    
    @Override
    public void Validate() throws ValidationException {
        if (this.quantity <= 0) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_INT.Format("Quantity", "Quantity"));
        }
    }

    @Override
    public void Execute(RewardExecutorContext context) {
        RewardsManager.sendScoreToPlayer(context.player, this.quantity);
    }
}
