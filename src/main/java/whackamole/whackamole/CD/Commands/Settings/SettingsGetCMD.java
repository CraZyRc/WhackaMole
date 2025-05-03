package whackamole.whackamole.CD.Commands.Settings;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class SettingsGetCMD extends SubCommand {

  @Override
  protected String GetName() { return Translator.COMMANDS_SETTINGS_GET.Format(); }

  @Override
  protected String Permission() {
    return "wam.settings.get";
  }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[] {
            Arguments.Games()
    };
  }

  @Override
  protected @Nullable CommandExecutor Executes() {
    return ((sender, args) -> {
      Game game = (Game) args.get(0);
      var settings = game.getSettings();
      ChatColor w = ChatColor.WHITE;
      ChatColor a = ChatColor.AQUA;
      ChatColor y = ChatColor.YELLOW;
      String line = y + "\n| ";
      String outputString = y + "\n[>------------------------------------<]\n" +
              "|" + w + " Game: " + a + game.getName() +
              line +
              line + w + Translator.COMMANDS_SETTINGS_DIRECTION            + ": " + a + settings.spawnRotation +
              line + w + Translator.COMMANDS_SETTINGS_JACKPOT              + ": " + a + settings.hasJackpot +
              line + w + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE   + ": " + a + settings.jackpotSpawnChance +
              line + w + Translator.COMMANDS_SETTINGS_MAXMISSED            + ": " + a + settings.missCount +
              line + w + Translator.COMMANDS_SETTINGS_SCOREPOINTS          + ": " + a + settings.scorePoints +
              line + w + Translator.COMMANDS_SETTINGS_SPAWNRATE            + ": " + a + settings.spawnTimer +
              line + w + Translator.COMMANDS_SETTINGS_SPAWNCHANCE          + ": " + a + settings.spawnChance +
              line + w + Translator.COMMANDS_SETTINGS_MOLESPEED            + ": " + a + settings.moleSpeed +
              line + w + Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE      + ": " + a + settings.difficultyScale +
              line + w + Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE   + ": " + a + settings.difficultyScore +
              line + w + Translator.COMMANDS_SETTINGS_MOLEHEAD             + ": " + a + settings.moleHead +
              line + w + Translator.COMMANDS_SETTINGS_JACKPOTHEAD          + ": " + a + settings.jackpotHead +
              line + w + Translator.COMMANDS_SETTINGS_COOLDOWN             + ": " + a + settings.getCooldown() +
              line + w + Translator.COMMANDS_SETTINGS_MUSIC                + ": " + a + settings.Music +
              line + w + Translator.COMMANDS_SETTINGS_TOGGLESCOREBOARD     + ": " + a + settings.toggleScoreboard +
              y + "\n| \n[>------------------------------------<]";
      sender.sendMessage(outputString);
    });
  }
}
