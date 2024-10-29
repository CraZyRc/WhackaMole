package whackamole.whackamole.CD.Commands;

import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.Config;
import whackamole.whackamole.CD.ConfirmSubCommand;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

public class TicketBuyCommand extends ConfirmSubCommand {
    private static final Econ econ = new Econ();

    @Override
    protected String GetName() {
        return Translator.COMMANDS_BUY.Format();
    }

    @Override
    protected String Permission() {
        return Config.Permissions.PERM_BUY;
    }

    @Override
    protected long ConfirmationTime() {
        return 10*1000L;
    }

    @Override
    protected PlayerCommandExecutor ExecutesPlayer() {
        return (sender, args) -> {
            if (Econ.currencyType == Econ.Currency.NULL) {
                sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_ECONOMYERROR_PLAYER);
                Logger.error(Translator.COMMANDS_BUY_ECONOMYERROR_CONSOLE.Format(sender));
                return;
            }

            if (! TicketBuyCommand.econ.has(sender, Config.Currency.TICKETPRICE)) {
                sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_LOWECONOMY);
                return;
            }

            if (!HasCommandBeenConfirmed(sender)) {
                sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_CONFIRMATION.Format());
                return;
            }

            var inventory = sender.getInventory();
            if (inventory.firstEmpty() == -1) {
                sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_FULLINVENTORY);
                return;
            }

            inventory.addItem(Config.Game.TICKET);
            TicketBuyCommand.econ.withdrawPlayer(sender, Config.Currency.TICKETPRICE);
            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_SUCCESS);
        };
    }
}