package whackamole.whackamole.CD.Commands.Game;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class GameToggleCMD extends SubCommand {
  @Override
  protected String GetName() { return Translator.COMMANDS_TOGGLE.Format(); }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[]{
            Arguments.Games()
    };
  }

  @Override
  protected PlayerCommandExecutor ExecutesPlayer() {
    return (sender, args) -> {
      Game game = (Game) args.get(0);

      if (game.toggleDisplay()) {
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_TOGGLE_SUCCESS.Format());
      } else sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_ARGUMENTS_UNKNOWNGAMENAME.Format(args.get(0))); // TODO: FIX args.get(0)
    };
  }
}
