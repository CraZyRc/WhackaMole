package whackamole.whackamole.RS;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.RS.Types.IRewardType;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

import java.util.*;

public class RewardsManager {

    private static YMLFile rewardFile = new YMLFile(Config.AppConfig.storageFolder + "/rewards.yml");
    private static HashMap<String, GameRewards> gameRewards = new HashMap<>();
    private static Map<Player, RewardExecutor> executors = new HashMap<>();


    public static void sendScoreToPlayer(Player player, double score) {
        var message = "";
        if(score == 0) message = Translator.GAME_STOP_REWARD_NONE.Format();
        if(score == 1) message = Translator.GAME_STOP_REWARD_SING.Format(String.valueOf(score));
        if(score >  1) message = Translator.GAME_STOP_REWARD_PLUR.Format(String.valueOf(score));
        player.sendMessage(Config.AppConfig.PREFIX + message);
    }

    public static void executeRewards(Game game) {
        Econ econ = new Econ();
        var rewards = gameRewards.get(game.getName());
        if (rewards != null) {
            game.getRunning().ifPresent((gameRunner) -> {
                if (executors.containsKey(gameRunner.getPlayer())) {
                    return;
                }
                var executor = rewards.getExecutor();
                executors.put(gameRunner.getPlayer(), executor.setGame(game).setPlayer(gameRunner.getPlayer()));
                executor.Start();
            });
        } else {
            econ.depositPlayer(game.getRunning().get().player, game.getRunning().get().score);
            RewardsManager.sendScoreToPlayer(game.getRunning().get().player, game.getRunning().get().score);
            game.setState(Game.gameState.READY);
        }
    }

    public static void Tick() {
        var values = executors.values();
        for (var executor : values) {
            executor.Tick();
            if (executor.getState() == RewardExecutor.State.ForRemoval) {
                values.remove(executor);
            }
        }
    }


    public static void onInteractEvent(Player player, Entity entity) {
        if (executors.containsKey(player)) {
            executors.get(player).onInteractEvent(entity);
        }
    }


    public static void onLoad() {
        loadRewards();
    }

    public static void onReload() {
        gameRewards.clear(); // TODO: this doesn't remove old data

        loadRewards(); // TODO: this doesn't load new data...

        //TODO: this function doesn't change the changes from the rewardsFile
    }


    @SuppressWarnings("unchecked")
    static void loadRewards()
    {
        var data = (LinkedHashMap<String, ?>) rewardFile.getMap("Rewards");
        for(var key : data.keySet()) {
            var rewardData = (LinkedHashMap<String, ?>) rewardFile.getMap("Rewards." + key);
            var threshold = (int) rewardData.get("Threshold");
            var games = (List<String>) rewardData.get("Games");


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

    private static void addRewardToGames(List<String> gameNames, IRewardType reward) {
        for (String gameName : gameNames) {

            if (! gameRewards.containsKey(gameName)) {
                gameRewards.put(gameName, new GameRewards(gameName));
            }
            gameRewards.get(gameName).addReward(reward);
        }
    }

}
