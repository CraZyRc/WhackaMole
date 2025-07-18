package whackamole.whackamole.RS.Reward.Steps;

import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.Reward.ValidationException;

public abstract class RewardStep {

    public abstract void Execute(RewardExecutorContext context);

    public abstract void Validate() throws ValidationException;
}
