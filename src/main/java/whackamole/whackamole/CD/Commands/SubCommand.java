package whackamole.whackamole.CD.Commands;


import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.*;
import whackamole.whackamole.GS.GamesManager;

public abstract class SubCommand {

    /**
     * Direct link to the games manager
     */
    protected final static GamesManager Manager = GamesManager.getInstance();

    /**
     * Get The name of the Command
     */
    protected abstract String GetName();

    /**
     * Get The required permission for the command
     * @return Permission | null
     */
    @Nullable
    protected String Permission() { return null; }

    /**
     * Get the Arguments for this Command
     * @return Arguments[]
     */
    protected Argument<?>[] Arguments() { return new Argument[] {}; }

    /**
     * Get the aliases for this command
     * @return String[]
     */
    protected String[] Aliases() { return new String[] {}; }

    /**
     * Add subcommand to this command
     * @return SubCommand[]
     */
    protected SubCommand[] SubCommands() { return new SubCommand[] {}; }

    /**
     * Method to run when this command is reached
     * @return CommandExecuter | null
     */
    @Nullable
    protected CommandExecutor Executes() { return null; }

    /**
     * Method to run when this command is reached
     * @return PlayerCommandExecutor | null
     */
    @Nullable
    protected PlayerCommandExecutor ExecutesPlayer() { return null; }

    private CommandAPICommand GetCommand() {
        var command         = new CommandAPICommand(this.GetName());
        var aliases         = this.Aliases();
        var permission      = this.Permission();
        var arguments       = this.Arguments();
        
        var executes        = this.Executes();
        var executesPlayer  = this.ExecutesPlayer();
        
        var subCommands     = this.SubCommands();
        
        if (aliases.length > 0)      command.setAliases(aliases);
        if (arguments.length > 0)    command.setArguments(Arrays.asList(arguments));
        if (permission != null)      command.withPermission(permission);
    
        if (executes != null)            command.executes(executes);
        else if (executesPlayer != null) command.executesPlayer(executesPlayer);
    
        for (SubCommand subCommand : subCommands) {
            command.withSubcommand(subCommand.GetCommand());
        }
        return command;
    }

    public CommandAPICommand Register() {
        var command = GetCommand();
        command.register();
        return command;
    }

    public void UnRegister() {
        CommandAPI.unregister(this.GetName());
        for (var alias : this.Aliases()) {
            CommandAPI.unregister(alias);
        }
    }
}
