package whackamole.whackamole.RS;

import java.util.ArrayList;
import java.util.List;

import whackamole.whackamole.RS.Types.IRewardType;


public class GameRewards {

    private String gameName;
    private List<IRewardType> Rewards = new ArrayList<>();

    GameRewards(String gameName) {
        this.gameName = gameName;
    }

    protected void addReward(IRewardType reward) {
        if (reward.Check()) {
            this.Rewards.add(reward);
        }
    }

    protected RewardExecutor getExecutor() {
        return new RewardExecutor(Rewards);
    }
}
