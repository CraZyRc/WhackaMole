package whackamole.whackamole.RS;

import java.util.ArrayList;
import java.util.List;

import whackamole.whackamole.RS.Types.IRewardType;


public class GameRewards {

    private List<IRewardType> Rewards = new ArrayList<>();

    GameRewards() {
    }

    protected void addReward(IRewardType reward) {
        this.Rewards.add(reward);
    }

    protected RewardExecutor getExecutor() {
        return new RewardExecutor(Rewards);
    }
}
