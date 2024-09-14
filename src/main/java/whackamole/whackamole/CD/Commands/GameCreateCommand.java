package whackamole.whackamole.CD.Commands;

import org.bukkit.World;

import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.Config;
import whackamole.whackamole.Grid;
import whackamole.whackamole.CD.SubCommand;
import whackamole.whackamole.Utils.Translator;

public class GameCreateCommand extends SubCommand {
    
    @Override
    protected String GetName() {
        return Translator.COMMANDS_CREATE.Format();
    }

    @Override
    protected String Permission() {
        return Config.Permissions.PERM_CREATE;
    }

    @Override
    protected Argument<?>[] Arguments() {
        return new Argument[] {
            new StringArgument("Game name").replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestInfo -> new IStringTooltip[] {
                StringTooltip.ofString("Whackamole", Translator.COMMANDS_TIPS_NAME.toString()),
                StringTooltip.ofString("MOLESTER", Translator.COMMANDS_TIPS_NAME.toString()),
                StringTooltip.ofString("Something...Something", Translator.COMMANDS_TIPS_NAME.toString()),
                StringTooltip.ofString("Moling", Translator.COMMANDS_TIPS_NAME.toString()),
            }))
        };
    }

    @Override
    protected PlayerCommandExecutor ExecutesPlayer() {
        return (player, args) -> {
            if (!IsWorldValid(player.getWorld())) {
                player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_CREATE_EROR_WORLDNOTENABLED);
                return;
            }
            var gameName = args.<String>getUnchecked("Game name");
            try {
                Manager.addGame(gameName, Grid.searchGrid(player.getWorld(), player), player);
                player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_CREATE_SUCCESS.Format());
            } catch (Exception e) {
                player.sendMessage(Config.AppConfig.PREFIX + e.getMessage());
            }
        };
    }

    private boolean IsWorldValid(World world) {
        if (Config.Game.ENABLED_WOLRDS.isEmpty()) {
            return true;
        }
        return Config.Game.ENABLED_WOLRDS.contains(world.getName());
    }
}