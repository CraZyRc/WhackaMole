package whackamole.whackamole.CD.Commands;

import org.bukkit.ChatColor;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.Config;
import whackamole.whackamole.CD.SubCommand;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class GameSettingsDisplayCommand extends SubCommand {
    
    @Override
    protected String GetName() {
        return Translator.COMMANDS_SETTINGS.Format();
    }

    @Override
    protected String Permission() {
        return Config.Permissions.PERM_SETTINGS;
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

            var settings = game.getSettings();
            String line = ChatColor.YELLOW + "\n| ";
            var sb = new StringBuilder(ChatColor.YELLOW + "\n[>------------------------------------<]\n");
            sb.append("|" + ChatColor.WHITE + " Game: " + ChatColor.AQUA + game.getName());
            sb.append(line);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_DIRECTION            + ": " + ChatColor.AQUA + settings.spawnRotation);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_JACKPOT              + ": " + ChatColor.AQUA + settings.hasJackpot);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE   + ": " + ChatColor.AQUA + settings.jackpotSpawnChance);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MAXMISSED            + ": " + ChatColor.AQUA + settings.missCount);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_SCOREPOINTS          + ": " + ChatColor.AQUA + settings.scorePoints);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_SPAWNRATE            + ": " + ChatColor.AQUA + settings.spawnTimer);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_SPAWNCHANCE          + ": " + ChatColor.AQUA + settings.spawnChance);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MOLESPEED            + ": " + ChatColor.AQUA + settings.moleSpeed);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE      + ": " + ChatColor.AQUA + settings.difficultyScale);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE   + ": " + ChatColor.AQUA + settings.difficultyScore);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MOLEHEAD             + ": " + ChatColor.AQUA + settings.moleHead);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_JACKPOTHEAD          + ": " + ChatColor.AQUA + settings.jackpotHead);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_COOLDOWN             + ": " + ChatColor.AQUA + settings.getCooldown());
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MUSIC                + ": " + ChatColor.AQUA + settings.Music);
            sb.append(line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_TOGGLESCOREBOARD     + ": " + ChatColor.AQUA + settings.toggleScoreboard);
            sb.append(ChatColor.YELLOW + "\n| \n[>------------------------------------<]");
            
            sender.sendMessage(sb.toString());
        };
    }
}