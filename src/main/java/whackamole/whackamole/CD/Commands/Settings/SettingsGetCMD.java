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
      String line = ChatColor.YELLOW + "\n| ";
      String outputString = ChatColor.YELLOW + "\n[>------------------------------------<]\n" +
              "|" + ChatColor.WHITE + " Game: " + ChatColor.AQUA + game.getName() +
              line +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_DIRECTION            + ": " + ChatColor.AQUA + settings.spawnRotation +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_JACKPOT              + ": " + ChatColor.AQUA + settings.hasJackpot +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE   + ": " + ChatColor.AQUA + settings.jackpotSpawnChance +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MAXMISSED            + ": " + ChatColor.AQUA + settings.missCount +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_SCOREPOINTS          + ": " + ChatColor.AQUA + settings.scorePoints +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_SPAWNRATE            + ": " + ChatColor.AQUA + settings.spawnTimer +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_SPAWNCHANCE          + ": " + ChatColor.AQUA + settings.spawnChance +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MOLESPEED            + ": " + ChatColor.AQUA + settings.moleSpeed +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE      + ": " + ChatColor.AQUA + settings.difficultyScale +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE   + ": " + ChatColor.AQUA + settings.difficultyScore +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MOLEHEAD             + ": " + ChatColor.AQUA + settings.moleHead +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_JACKPOTHEAD          + ": " + ChatColor.AQUA + settings.jackpotHead +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_COOLDOWN             + ": " + ChatColor.AQUA + settings.getCooldown() +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MUSIC                + ": " + ChatColor.AQUA + settings.Music +
              line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_TOGGLESCOREBOARD     + ": " + ChatColor.AQUA + settings.toggleScoreboard +
              ChatColor.YELLOW + "\n| \n[>------------------------------------<]";
      sender.sendMessage(outputString);
    });
  }
}
