package whackamole.whackamole.CD.Commands.Settings;

import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class PositionsCMD extends SubCommand {

  @Override
  protected String GetName() { return Translator.COMMANDS_POSITIONS.Format(); }

  @Override
  protected @Nullable String Permission() { return Config.Permissions.PERM_POSITIONS; }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[] {
            Arguments.Games(),
            this.positionArgument()

    };
  }

  @Override
  protected @Nullable PlayerCommandExecutor ExecutesPlayer() {
    return (sender, args) -> {
      if (!args.get(1).equals("failed")) sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_POSITIONS_SUCCESS.Format(args.get(1)));
    };
  }

  private Argument<String> positionArgument() {
    return new CustomArgument<>(new StringArgument("Positions"), Info -> {
      Game game = (Game) Info.previousArgs().get(0);
      Player player = (Player) Info.sender();

      switch (Info.input()) {
        case "highscore" -> game.setHighScoreLocation(player.getLocation().add(0,2,0));
        case "teleport" -> {
          if (!game.setTeleportLocation(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ())){
            player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_POSITIONS_TELEPORT_ONGRID);
            return "failed";
          }

        }
        case "streak" -> game.setStreakHoloLocation(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ());
      }
      return Info.input();
    }).replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
            StringTooltip.ofString("highscore", Translator.COMMANDS_TIPS_HIGHSCORE.Format()),
            StringTooltip.ofString("teleport", Translator.COMMANDS_TIPS_TELEPORT.Format()),
            StringTooltip.ofString("streak", Translator.COMMANDS_TIPS_STREAK.Format())
    }));
  }
}
