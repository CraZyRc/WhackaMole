package whackamole.whackamole.RS.Reward.Steps.Types;

import java.io.FileNotFoundException;
import java.util.Map;

import org.bukkit.plugin.Plugin;
import whackamole.whackamole.Config;
import whackamole.whackamole.RS.Reward.Steps.Animation.Command;
import whackamole.whackamole.RS.Reward.Steps.IRewardStepTickable;
import whackamole.whackamole.RS.Reward.Steps.RewardStep;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.RS.Reward.ValidationException;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

public class AnimationReward extends RewardStep implements IRewardStepTickable {
    private static final String[] exampleAnimations = new String[] {"Crown", "Butterfly_wings", "Star", "Dragon_wings", "Smile"};
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
            throw new ValidationException(Translator.YML_CREATEFAIL.Format(this.animationFileName));
        }
        
        var pixels = animationFile.getList("Animation");
        if (pixels == null || pixels.isEmpty()) {
            throw new ValidationException(Translator.REWARDS_TYPE_INVALID_ANIMATIONFILE.Format(this.animationFileName));
        }

        try {
            this.command = new Command(pixels);
        } catch (ValidationException ex) {
            throw new ValidationException(Translator.ANIMATIONREWARD_INVALID_COMMAND.Format(this.animationFileName)).addCause(ex);
        }
    }

    public static void loadAnimationFiles(Plugin main) {
        YMLFile animationFile;

        // * Create animation files from resource files
        for (String animation : exampleAnimations) {
            try {
                animationFile = new YMLFile(Config.AppConfig.storageFolder + "/animations/" + animation + ".yml");

                if (animationFile.created) {
                    Logger.success(Translator.YML_CREATEFILE.Format(animationFile));
                    main.saveResource("animations/" + animation + ".yml", true);
                }
            } catch (Exception e) {
                Logger.error(e.getMessage());
                return;
            }
        }
    }
    
    @Override
    public int getTimer() {
        return this.duration;
    }

    @Override
    public void Execute(RewardExecutorContext context) {
        this.ExecuteTick(context, 0L);
    }

    @Override
    public void ExecuteTick(RewardExecutorContext context, long _ticksPassed) {
        this.command.render(context.location);
    }

    @Override
    public void ExecuteAfter(RewardExecutorContext context) {
    }
}
