package whackamole.whackamole.RS;

import org.bukkit.entity.Player;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

import java.util.ArrayList;
import java.util.List;

public class RewardsManager {

    public static List<Reward> Rewards = new ArrayList<>();


    public static void sendScoreToPlayer(Player player, double score) {
        var message = "";
        if(score == 0) message = Translator.GAME_STOP_REWARD_NONE.Format();
        if(score == 1) message = Translator.GAME_STOP_REWARD_SING.Format(String.valueOf(score));
        if(score >  1) message = Translator.GAME_STOP_REWARD_PLUR.Format(String.valueOf(score));
        player.sendMessage(Config.AppConfig.PREFIX + message);
    }

    public static void executeRewards(Player player, Game game) {
        boolean Payout = false;
        Econ econ = new Econ();
        double score = game.getRunning().get().score;

        for (Reward reward : Rewards) {
            if (reward.Games.contains(game.getName())) {
                if (score >= reward.Threshold) {
                    Payout = true;
                    reward.Payout(player);
                }
            }
        }
        if (!Payout) {
            econ.depositPlayer(player, score);
            RewardsManager.sendScoreToPlayer(player, score);
        }
    }

    public static void onLoad(YMLFile rewardsFile) {
        for (String key : rewardsFile.FileConfig.getConfigurationSection("Rewards").getValues(false).keySet()) {
            Rewards.add(new Reward(rewardsFile, key));
        }
    }

    public static void onReload(YMLFile rewardsFile) {
        Rewards.clear();

        for (String key : rewardsFile.FileConfig.getConfigurationSection("Rewards").getValues(false).keySet()) {
            Rewards.add(new Reward(rewardsFile, key));
        }
    }
}
