package whackamole.whackamole.RS_new.Reward.Steps;

import whackamole.whackamole.RS_new.RewardExecutorContext;
import whackamole.whackamole.RS_new.Reward.ValidationException;

public abstract class RewardStep {

    public abstract void Execute(RewardExecutorContext context);

    public abstract void Validate() throws ValidationException;
}
