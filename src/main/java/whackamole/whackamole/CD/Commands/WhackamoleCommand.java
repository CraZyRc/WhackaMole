package whackamole.whackamole.CD.Commands;

import whackamole.whackamole.CD.SubCommand;

public class WhackamoleCommand extends SubCommand {
    
    protected String GetName() {
        return "whackamole";
    }

    protected String[] Aliases() {
        return new String[] {
            "wam"
        };
    }
    
    protected SubCommand[] SubCommands() {
        return new SubCommand[] {
            new GameCreateCommand(),
            new GameStartCommand(),
            new GameStopCommand(),
            new GameRemoveCommand(),
            new TicketBuyCommand(),
        };
    }
}
