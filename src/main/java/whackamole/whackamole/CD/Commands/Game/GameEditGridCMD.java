package whackamole.whackamole.CD.Commands.Game;

import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Translator;

public class GameEditGridCMD extends SubCommand {
  @Override
  protected String GetName() { return Translator.COMMANDS_EDITGRID.Format(); }

  @Override
  protected @Nullable String Permission() {
    return "wam.game.edit";
  }

  @Override
  protected @Nullable PlayerCommandExecutor ExecutesPlayer() {
    return (player, args) -> {
      if (player.getScoreboardTags().contains("wamEditGrid")) {
        player.getScoreboardTags().remove("wamEditGrid");
        player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_EDITGRID_SUCCESS.Format("False"));

      } else {
        player.getScoreboardTags().add("wamEditGrid");
        player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_EDITGRID_SUCCESS.Format("True"));
      }
    };
  }
}
