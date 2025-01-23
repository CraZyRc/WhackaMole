package whackamole.whackamole.CD.Commands.Game;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.ConfirmSubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class GameRemoveCMD extends ConfirmSubCommand {

  @Override
  protected String GetName() {
    return Translator.COMMANDS_REMOVE.Format();
  }

  @Override
  protected String Permission() {
    return Config.Permissions.PERM_REMOVE;
  }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[] {
            Arguments.Games()
    };
  }

  @Override
  protected long ConfirmationTime() {
    return 10*1000L;
  }

  @Override
  protected PlayerCommandExecutor ExecutesPlayer() {
    return (sender, args) -> {
      var game = args.<Game>getUnchecked("Game");
      if (game == null) return;
      if (!HasCommandBeenConfirmed(sender)) {
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_REMOVE_CONFIRM.Format());
        game.highlightGameStart(sender);
        return;
      }

      game.highlightGameStop();
      Manager.deleteGame(game);
      sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_REMOVE_SUCCESS);
    };
  }
}
