package whackamole.whackamole.CD.Commands;

import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

public class TicketBuyCMD extends ConfirmSubCommand {

  private static final Econ econ = new Econ();

  @Override
  protected String GetName() {
    return Translator.COMMANDS_BUY.Format();
  }

  @Override
  protected String Permission() {
    return "wam.buy";
  }

  @Override
  protected long ConfirmationTime() {
    return 10 * 1000L;
  }

  @Override
  protected PlayerCommandExecutor ExecutesPlayer() {
    return (sender, args) -> {
      if (Econ.currencyType == Econ.Currency.NULL) {
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_ECONOMYERROR_PLAYER);
        Logger.error(Translator.COMMANDS_BUY_ECONOMYERROR_CONSOLE.Format(sender));
        return;
      }

      if (!TicketBuyCMD.econ.has(sender, Config.Currency.TICKETPRICE)) {
        if (Config.Currency.TICKETPRICE < 1) {
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_LOWECONOMY_SING.Format());
        } else {
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_LOWECONOMY_PLUR.Format());
        }
        return;
      }

      if (!HasCommandBeenConfirmed(sender)) {
        if (Config.Currency.TICKETPRICE < 1) {
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_CONFIRMATION_SING.Format());
        } else {
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_CONFIRMATION_PLUR.Format());
        }
        return;
      }

      var inventory = sender.getInventory();
      if (inventory.firstEmpty() == -1) {
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_FULLINVENTORY.Format());
        return;
      }

      inventory.addItem(Config.Game.TICKET);
      TicketBuyCMD.econ.withdrawPlayer(sender, Config.Currency.TICKETPRICE);
      sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_SUCCESS.Format());
    };
  }
}
