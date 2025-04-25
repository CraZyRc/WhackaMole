package whackamole.whackamole.CD.Commands.Game;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class GameStartCMD extends SubCommand {

  @Override
  protected String GetName() {
    return Translator.COMMANDS_START.Format();
  }

  @Override
  protected String Permission() {
    return "wam.game.start";
  }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[] {
            Arguments.Games()
    };
  }

  @Override
  protected PlayerCommandExecutor ExecutesPlayer() {
    return (sender, args) -> {
      var game = args.<Game>getUnchecked("Game");
      if (game == null) return;

      Player player = null;
      for(var p : Bukkit.getOnlinePlayers()) {
        if (game.onGrid(p)) {
          if (player == null) player = p;
          else {
            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_START_ERROR_TOOMANYPLAYERS);
            return;
          }
        }
      }

      if (player == null)
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_START_ERROR_NOPLAYERFOUND);
      else {
        game.cooldown.remove(player.getUniqueId());
        game.Start(player);
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_START_SUCCESS.Format(game));
      }
    };
  }
}
