package whackamole.whackamole.CD;

import java.util.Hashtable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ConfirmSubCommand extends SubCommand {
    static final Hashtable<String, Long> confirmationTable = new Hashtable<>();

    /**
     * In miliseconds how long the player has to confirm
     * @return miliseconds
     */
    protected abstract long ConfirmationTime();

    /**
     * Check whether the player has confirmed the action
     * @param player
     * @return boolean
     */
    protected boolean HasCommandBeenConfirmed(Player player) {
        return HasCommandBeenConfirmed(player.getUniqueId().toString());
    }
    
    /**
     * Check whether the player has confirmed the action
     * @param sender
     * @return boolean
     */
    protected boolean HasCommandBeenConfirmed(CommandSender sender) {
        return HasCommandBeenConfirmed(sender.getName());
    }

    /**
     * check whether the player has confirmed the action
     * @param key
     * @return
     */
    protected boolean HasCommandBeenConfirmed(String key) {
        key = this.GetName() + key;
        if (confirmationTable.containsKey(key) && confirmationTable.get(key) > System.currentTimeMillis()) {
            confirmationTable.remove(key);
            return true;
        } else {
            confirmationTable.put(key, System.currentTimeMillis() + this.ConfirmationTime());
            return false;
        }
    }
}
