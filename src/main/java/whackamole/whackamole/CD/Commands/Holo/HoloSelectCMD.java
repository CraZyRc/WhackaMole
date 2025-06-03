package whackamole.whackamole.CD.Commands.Holo;


import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class HoloSelectCMD extends SubCommand {
  @Override
  protected String GetName() { return Translator.COMMANDS_HOLO_SELECT.Format(); }

  @Override
  protected @Nullable String Permission() {
    return "wam.settings.holo.select";
  }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[] {
            Arguments.Games(),
            Arguments.holoIDArgument()
    };
  }

  @Override
  protected @Nullable PlayerCommandExecutor ExecutesPlayer() {
    return ((sender, args) -> {
      Game game = (Game) args.get(0);
      if (!game.holoSelect((int) args.get(1), sender)) {
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_HOLO_SELECT_ERROR.Format());
      }
    });
  }


}
