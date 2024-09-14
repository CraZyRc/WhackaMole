package whackamole.whackamole.CD.Commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.Config;
import whackamole.whackamole.CD.Arguments;
import whackamole.whackamole.CD.ConfirmSubCommand;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class GameRemoveCommand extends ConfirmSubCommand {
    
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
            if (!HasCommandBeenConfirmed(sender)) {
                sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_REMOVE_CONFIRM.Format());
                return;
            }
            
            var game = args.<Game>getUnchecked("Game");
            if (game == null) return;
            
            if (game.isRunning()) {
                game.Stop();
                sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_STOP_SUCCESS, game.getName());
            }
            else {
                sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_STOP_ERROR_NOACTIVEGAME);
            }
        };
    }
}