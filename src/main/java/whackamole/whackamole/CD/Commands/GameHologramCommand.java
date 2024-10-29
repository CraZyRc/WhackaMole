package whackamole.whackamole.CD.Commands;

import whackamole.whackamole.CD.SubCommand;

public class GameHologramCommand extends SubCommand {
    
    @Override
    protected String GetName() {
        // TODO: Holo command name Translator
        return "holo"; 
    }

    @Override
    protected String Permission() {
        // TODO: Holo command Permissions
        return null;
    }

    protected SubCommand[] SubCommands() {
        return new SubCommand[] {
            new GameHologramCreateCommand(),
            new GameHologramDeleteCommand(),
        };
    }

}