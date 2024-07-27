package whackamole.whackamole.RS;

import org.bukkit.entity.Player;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.RS.Types.IRewardType;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class RewardsManager {

    public static List<Reward> Rewards = new ArrayList<>();
    private static YMLFile rewardFile = new YMLFile(Config.AppConfig.storageFolder + "/rewards.yml");
    private static HashMap<String, GameRewards> gameRewards;


    public static void sendScoreToPlayer(Player player, double score) {
        var message = "";
        if(score == 0) message = Translator.GAME_STOP_REWARD_NONE.Format();
        if(score == 1) message = Translator.GAME_STOP_REWARD_SING.Format(String.valueOf(score));
        if(score >  1) message = Translator.GAME_STOP_REWARD_PLUR.Format(String.valueOf(score));
        player.sendMessage(Config.AppConfig.PREFIX + message);
    }

    public static void executeRewards(Player player, Game game) {
        game.updateActionBar();
        boolean Payout = false;
        Econ econ = new Econ();
        int score = game.getRunning().get().score;

        for (Reward reward : Rewards) {
            if (reward.Games.contains(game.getName())) {
                if (score >= reward.Threshold) {
                    Payout = true;
                    reward.Payout(player, game);
                }
            }
        }
        if (!Payout) {
            econ.depositPlayer(player, score);
            RewardsManager.sendScoreToPlayer(player, score);
            game.setState(Game.gameState.READY);
        }
    }

    public static void interactEvent(Player player, Game game) {
        for (Reward reward : Rewards) {
            if (reward.Games.contains(game.getName())) {
                reward.checkEntity(player, game);
            }
        }
    }

    public static void onLoad(YMLFile rewardsFile) {
        loadRewards();
        
        for (String key : rewardsFile.FileConfig.getConfigurationSection("Rewards").getValues(false).keySet()) {
            Rewards.add(new Reward(rewardsFile, key));
        }
    }

    public static void onReload(YMLFile rewardsFile) {
        gameRewards.clear();
        loadRewards();

        Rewards.clear();

        for (String key : rewardsFile.FileConfig.getConfigurationSection("Rewards").getValues(false).keySet()) {
            Rewards.add(new Reward(rewardsFile, key));
        }
    }


    @SuppressWarnings("unchecked")
    static void loadRewards()
    {
        LinkedHashMap<String, ?> data = rewardFile.get("Rewards");
        for(var key : data.keySet()) {
            var rewardData = (LinkedHashMap<String, ?>) data.get(key); 
            var threshold = (int) rewardData.get("threshold"); 
            var games = (List<String>) rewardData.get("Games");
            var animationName = (String) rewardData.get("Animation");

            if (! animationName.isEmpty()) {
                var animation = new Animation(animationName);
                addAnimationToGames(games, animation);
            }

            var rewardsData = (List<LinkedHashMap<String, ?>>) rewardData.get("RewardTypes"); 
            loadRewardTypes(rewardsData, games, threshold);
        }
    }

    @SuppressWarnings("unchecked")
    static void loadRewardTypes(List<LinkedHashMap<String, ?>> data, List<String> games, int threshold)
    {
        for (var rewardMap : data) {
            var type = (String) rewardMap.get("Type");
            var settings = (LinkedHashMap<String, ?>) rewardMap.get("Settings");
            var reward = IRewardType.Factory(type, threshold, settings);
            if (reward != null && reward.Check()) addRewardToGames(games, reward);;
        }
    }

    private static void addRewardToGames(List<String> gameNames, IRewardType reward)
    {
        for(var gameName : gameNames) {
            if (! gameRewards.containsKey(gameName)) {
                gameRewards.put(gameName, new GameRewards(gameName));
            }
            gameRewards.get(gameName).addReward(reward);
        }
    }
    
    private static void addAnimationToGames(List<String> gameNames, Animation animation)
    {
        for(var gameName : gameNames) {
            if (! gameRewards.containsKey(gameName)) {
                gameRewards.put(gameName, new GameRewards(gameName));
            }
            gameRewards.get(gameName).addAnimation(animation);
        }
    }
}
