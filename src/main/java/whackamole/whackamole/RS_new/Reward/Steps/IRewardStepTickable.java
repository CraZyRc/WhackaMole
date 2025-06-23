package whackamole.whackamole.RS_new.Reward.Steps;

import whackamole.whackamole.RS_new.RewardExecutorContext;

public interface IRewardStepTickable {

    public int getTimer();
    public void ExecuteTick(RewardExecutorContext context, long ticksPassed);
    public void ExecuteAfter(RewardExecutorContext context);
}
