package whackamole.whackamole.RS.Types;

public interface IRewardWaitableType extends IRewardType {
    int getTimer();

    /**
     * Exection method which is called every tick for {@code getTimer()} duration
     */
    void TickExecute();

    /**
     * Execution method which is called after the time hes run out
     */
    void AfterExecute(RewardExecutorContext ctx);
}
