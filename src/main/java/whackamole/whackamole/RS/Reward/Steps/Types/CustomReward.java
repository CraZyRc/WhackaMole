package whackamole.whackamole.RS.Reward.Steps.Types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import whackamole.whackamole.RS.Reward.Steps.RewardStep;
import whackamole.whackamole.RS.Reward.ValidationException;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

import java.util.Map;

public class CustomReward extends RewardStep {
  private String cmd;


  public CustomReward(Map<String,?> settings) {
    this.cmd = RewardsManager.getOrDefault(settings, "Command", "");
  }

  @Override
  public void Validate() throws ValidationException {
    if (this.cmd == null) {
      throw new ValidationException(Translator.REWARDS_TYPE_INVALID_STRING.Format("Command"));
    }
  }

  @Override
  public void Execute(RewardExecutorContext context) {
    if (this.cmd.contains("@s")) {
      this.Format(context.player);
    }

    context.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), this.cmd);
  }



  private void Format(Player player) {
    this.cmd = this.cmd.replace("@s", player.getName());
  }
}
