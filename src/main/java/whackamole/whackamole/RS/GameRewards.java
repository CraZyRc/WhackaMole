package whackamole.whackamole.RS;

import java.util.ArrayList;
import java.util.List;

import whackamole.whackamole.RS.Types.IRewardType;

public class GameRewards {
    private String gameName;
    private List<IRewardType> Rewards = new ArrayList<>();
    private Animation animation;

    GameRewards(String gameName)
    {
        this.gameName = gameName;
    }

    protected void addReward(IRewardType reward)
    {
        this.Rewards.add(reward);
    }
    
    protected void addAnimation(Animation animation)
    {
        this.animation = animation;
    }
}
