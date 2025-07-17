package whackamole.whackamole.RS.Reward.Steps;

import whackamole.whackamole.RS.RewardExecutorContext;

public interface IRewardStepTickable {

    /**
     * Get the timer in seconds
     * @return int
     */
    public int getTimer();

    /**
     * Execute method on every tick 
     * @param context
     * @param ticksPassed
     */
    public void ExecuteTick(RewardExecutorContext context, long ticksPassed);

    /**
     * Execute method after timer expires (clean up method)
     * @param context
     */
    public void ExecuteAfter(RewardExecutorContext context);
}
