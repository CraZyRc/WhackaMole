package whackamole.whackamole.RS.Types;

import org.bukkit.entity.Player;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Logger;

import java.util.LinkedHashMap;

public class CurrencyType implements RewardType {
    private int quantity;
    private Econ econ = new Econ();

    @Override
    public RewardType Load(LinkedHashMap Settings) {
        this.quantity = (int) Settings.get("Quantity");
        return this;
    }

    @Override
    public boolean Check() {
        if (this.quantity <= 0) {
            Logger.error("Invalid Quantity set in the RewardsFile, Quantity has to be bigger than 0"); // TODO: add Translator message
            return false;
        } else return true;
    }

    @Override
    public void Execute(Player player) {
        this.econ.depositPlayer(player, this.quantity);
        RewardsManager.sendScoreToPlayer(player, this.quantity);
    }
}
