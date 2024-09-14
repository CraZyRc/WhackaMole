package whackamole.whackamole.CD;

import java.util.Hashtable;

import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;

public abstract class ConfirmSubCommand extends SubCommand {
    static final Hashtable<String, Long> confirmationTable = new Hashtable<>();


    /**
     * In miliseconds how long the player has to confirm
     * @return miliseconds
     */
    protected long ConfirmationTime() { return 10000L; }

    /**
     * Marks this command as a confirmation command.
     * When the user firsts calls
     * @return String
     */
    protected abstract String ConfirmationMessage();

    /**
     * Executes command when player has confirmed the action
     */
    protected abstract PlayerCommandExecutor ExecutesConfirmed();
    
    /**
     * Not allowed to be used in a ConfirmSubCommand.
     * Use {@link ConfirmSubCommand#ExecutesConfirmed()} instead
     */
    final protected CommandExecutor Executes() { return null; }

    /**
     * Not allowed to be used in a ConfirmSubCommand.
     * Use {@link ConfirmSubCommand#ExecutesConfirmed()} instead
     */
    final protected PlayerCommandExecutor ExecutesPlayer() {
        return (sender, args) -> {
            var key = this.getClass().getName() + sender.getUniqueId();
            if (confirmationTable.containsKey(key) && confirmationTable.get(key) > System.currentTimeMillis()) {
                confirmationTable.remove(key);
                this.ExecutesConfirmed().run(sender, args);
            } else {
                confirmationTable.put(key, System.currentTimeMillis() + this.ConfirmationTime());
                sender.sendMessage(this.ConfirmationMessage());
            }
        };
    }
}
