package whackamole.whackamole.RS.Reward.Steps;

import org.bukkit.entity.Interaction;

public interface IRewardStepInteractable extends IRewardStepTickable{

    Interaction getInteractable();
}
