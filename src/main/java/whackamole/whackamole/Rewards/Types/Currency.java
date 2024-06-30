package whackamole.whackamole.Rewards.Types;

import org.bukkit.entity.Player;
import whackamole.whackamole.Game;
import whackamole.whackamole.Rewards.RewardsManager;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Logger;

public class Currency {
    private static Econ econ = new Econ();

    public void setCurrency(Player player, int Quantity, Game game) {
        if (Quantity > 0) {
            econ.depositPlayer(player, Quantity);
            RewardsManager.sendScoreToPlayer(player, Quantity, game);
        } else Logger.error("cannot give currency, currency has to be greater than 0 in rewards file"); // TODO: add Translator message
    }
}
