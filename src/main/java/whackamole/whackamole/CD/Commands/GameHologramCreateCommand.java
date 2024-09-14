package whackamole.whackamole.CD.Commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.CD.Arguments;
import whackamole.whackamole.CD.SubCommand;
import whackamole.whackamole.GS.Game;

public class GameHologramCreateCommand extends SubCommand {
    
    @Override
    protected String GetName() {
        // TODO: Holo create command name Translator
        return "create"; 
    }

    @Override
    protected String Permission() {
        // TODO: Holo create command Permissions
        return null;
    }

    @Override
    protected Argument<?>[] Arguments() {
        return new Argument[] {
            Arguments.Games(),
            new IntegerArgument("HoloID"),
            new StringArgument("Type"),
            new IntegerArgument("Rows"),
        };
    }

    @Override
    protected PlayerCommandExecutor ExecutesPlayer() {
        return (sender, args) -> {
            var game = args.<Game>getUnchecked("Game");
            var HoloID = args.<Integer>getUnchecked("HoloID");
            var Type = args.<String>getUnchecked("Type");
            var Rows = args.<Integer>getUnchecked("Rows");
            if (game == null || HoloID == null || Type == null || Rows == null) return;
            // TODO: Inform player
            // TODO: Validate if holo can be created
            game.holoCreate(HoloID, Type, sender.getLocation(), Rows);
        };
    }
}