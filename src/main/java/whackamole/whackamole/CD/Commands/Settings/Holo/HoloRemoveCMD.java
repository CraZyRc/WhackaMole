package whackamole.whackamole.CD.Commands.Settings.Holo;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class HoloRemoveCMD extends SubCommand {
  @Override
  protected String GetName() { return Translator.COMMANDS_HOLO_REMOVE.Format(); }

  @Override
  protected @Nullable String Permission() {
    return Config.Permissions.PERM_SETTINGS_HOLO_REMOVE;
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
      if (game.holoDelete((int) args.get(1))) {
        sender.sendMessage(String.valueOf(Translator.COMMANDS_HOLO_REMOVE_SUCCESS));
      } else {
        sender.sendMessage(String.valueOf(Translator.COMMANDS_HOLO_REMOVE_ERROR));
      }
    });
  }


}
