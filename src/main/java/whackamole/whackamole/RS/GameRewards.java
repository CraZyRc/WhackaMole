package whackamole.whackamole.RS;

import java.util.ArrayList;
import java.util.List;

import whackamole.whackamole.RS.Reward.Reward;

public class GameRewards {

    private List<Reward> Rewards = new ArrayList<>();

    GameRewards() {
    }

    protected void addReward(Reward reward) {
        this.Rewards.add(reward);
    }

    protected RewardExecutor getExecutor() {
        return new RewardExecutor(Rewards);
    }
}
