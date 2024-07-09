package whackamole.whackamole.Rewards;

import org.bukkit.entity.Player;
import whackamole.whackamole.Config;
import whackamole.whackamole.Game;
import whackamole.whackamole.Utils.Translator;

public class RewardsManager {


    public static void sendScoreToPlayer(Player player, int score) {
        var message = "";
        if(score == 0) message = Translator.GAME_STOP_REWARD_NONE.Format();
        if(score == 1) message = Translator.GAME_STOP_REWARD_SING.Format(String.valueOf(score));
        if(score >  1) message = Translator.GAME_STOP_REWARD_PLUR.Format(String.valueOf(score));
        player.sendMessage(Config.AppConfig.PREFIX + message);
    }

    public void executeRewards(Player player) {
        for (var reward : Reward.rewards) {
            reward.Execute(player);
        }
    }
}
