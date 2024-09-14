package whackamole.whackamole.CD.Commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.Config;
import whackamole.whackamole.CD.ConfirmSubCommand;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.GS.Game;

public class GameHologramDeleteCommand extends ConfirmSubCommand {
    
    @Override
    protected String GetName() {
        // TODO: Holo delete command name Translator
        return "delete"; 
    }

    @Override
    protected String Permission() {
        // TODO: Holo delete command Permissions
        return null;
    }
    
    @Override
    protected long ConfirmationTime() {
        return 10*1000L;
    }

    @Override
    protected Argument<?>[] Arguments() {
        return new Argument[] {
            Arguments.Games(),
            new IntegerArgument("HoloID"), // TODO: Convert To custome argument
        };
    }

    @Override
    protected PlayerCommandExecutor ExecutesPlayer() {
        return (sender, args) -> {
            if (!HasCommandBeenConfirmed(sender)) {
                // TODO: Confirmation message to Translator
                sender.sendMessage(Config.AppConfig.PREFIX + "Are you sure? resend command to confirm.");
                return;
            }

            var game = args.<Game>getUnchecked("Game");
            var HoloID = args.<Integer>getUnchecked("HoloID");
            if (game == null || HoloID == null) return;
            
            // TODO: Inform player
            // TODO: Validate if holo can be deleted
            game.holoDelete(HoloID);
        };
    }
}