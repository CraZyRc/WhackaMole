package whackamole.whackamole.Rewards.Types;

import org.bukkit.entity.Player;
import whackamole.whackamole.Rewards.RewardsManager;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.YMLFile;

public class CurrencyType implements RewardType {
    private int quantity;
    private Econ econ = new Econ();

    @Override
    public void Load(YMLFile file, String Reward) {
        this.quantity = file.getInt(Reward + ".Settings.Quantity");
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
        if (this.Check()) {
            this.econ.depositPlayer(player, this.quantity);
            RewardsManager.sendScoreToPlayer(player, this.quantity);

        }
    }
}
