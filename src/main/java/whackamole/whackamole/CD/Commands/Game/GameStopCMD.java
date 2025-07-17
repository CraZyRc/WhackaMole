package whackamole.whackamole.CD.Commands.Game;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class GameStopCMD extends SubCommand {

  @Override
  protected String GetName() {
    return Translator.COMMANDS_STOP.Format();
  }

  @Override
  protected String Permission() {
    return "wam.game.stop";
  }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[] {
            Arguments.Games()
    };
  }

  @Override
  protected CommandExecutor Executes() {
    return (sender, args) -> {
      var game = args.<Game>getUnchecked("Game");
      if (game == null) return;

      if (game.game.player == null) {
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_START_ERROR_NOPLAYERFOUND.Format());
        return;
      }

      if (game.isRunning()) {
        game.Stop();
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_STOP_SUCCESS.Format(game));
      }
      else {
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_STOP_ERROR_NOACTIVEGAME);
      }
    };
  }
}
