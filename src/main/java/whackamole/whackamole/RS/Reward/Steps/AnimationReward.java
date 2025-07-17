package whackamole.whackamole.RS.Reward.Steps;

import java.io.FileNotFoundException;
import java.util.Map;

import whackamole.whackamole.Config;
import whackamole.whackamole.RS.Reward.Steps.Animation.Command;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.RS.Reward.ValidationException;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

public class AnimationReward extends RewardStep implements IRewardStepTickable {
    private String animationFileName;
    private int duration;
    
    private Command command;

    public AnimationReward(Map<String, ?> settings) {
        this.animationFileName = RewardsManager.getOrDefault(settings, "Animation", "");
        this.duration = RewardsManager.getOrDefault(settings, "Duration", 1);
    }
    
    @Override
    public void Validate() throws ValidationException {
        if (this.animationFileName.equals("")) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_STRING.Format("Animation"));
        }

        YMLFile animationFile;
        try {
            animationFile = new YMLFile(Config.AppConfig.storageFolder + "/animations", this.animationFileName + ".yml");
        } catch(FileNotFoundException ex) {
            throw new ValidationException("Animation folder could not be created. Validate folder permissions.");
        }
        
        var pixels = animationFile.getList("Animation");
        if (pixels.size() == 0) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_ANIMATIONFILE.Format(this.animationFileName));
        }

        try {
            this.command = new Command(pixels);
        } catch (ValidationException ex) {
            throw new ValidationException(Translator.ANIMATIONCOMMAND_INVALID_COMMAND.Format(this.animationFileName)).addCause(ex);
        }
    }
    
    @Override
    public int getTimer() {
        return this.duration;
    }

    @Override
    public void Execute(RewardExecutorContext context) {
        this.ExecuteTick(context, 0);
    }

    @Override
    public void ExecuteTick(RewardExecutorContext context, long _ticksPassed) {
        this.command.render(context.location);
    }

    @Override
    public void ExecuteAfter(RewardExecutorContext context) {
    }
}
