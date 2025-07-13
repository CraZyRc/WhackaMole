package whackamole.whackamole.RS_new;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.RS_new.Reward.Reward;
import whackamole.whackamole.RS_new.Reward.ValidationException;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Logger;
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
                var executor = rewards.getExecutor().setGame(game).setPlayer(gameRunner.getPlayer());
                executors.put(gameRunner.getPlayer(), executor);
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

    public static void onTeleportEvent(Player player, Location loc) {
        if (executors.containsKey(player)){
            executors.get(player).onTeleportEvent(loc);
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
        gameRewards.clear();
        rewardFile = new YMLFile(Config.AppConfig.storageFolder + "/rewards.yml");
        loadRewards();
    }


    static void loadRewards()
    {
        for(var key : rewardFile.getMap("Rewards").keySet()) {
            var data = rewardFile.getMap("Rewards." + key);

            var games = RewardsManager.<String, List<String>>getOrDefault(data, "Games", null);
            var rewardData = RewardsManager.<String, List<Map<String, ?>>>getOrDefault(data, "rewardTypes", null);

            var rewards = loadRewardTypes(rewardData);

            var errors = new ArrayList<ValidationException>();
            try {
                for (Reward reward : rewards) {
                    reward.Validate();
                    addRewardToGames(games, reward);
                }
            } catch(ValidationException ex) {
                errors.add(ex);
            }

            if (errors.size() > 0) {
                for (var ex : errors) {
                    Logger.error(ex.getMessage());
                }
            }
        }
    }

    static Reward[] loadRewardTypes(List<Map<String, ?>> data)
    {
        var rewards = new Reward[data.size()];
        for (int i = 0; i < data.size(); i++) {
            rewards[i] = new Reward(data.get(i));
        }

        return rewards;
    }

    private static void addRewardToGames(List<String> gameNames, Reward reward) {
        for (String gameName : gameNames) {

            if (! gameRewards.containsKey(gameName)) {
                gameRewards.put(gameName, new GameRewards());
            }
            gameRewards.get(gameName).addReward(reward);
        }
    }



    @SuppressWarnings("unchecked")
    static public <T, V> V getOrDefault(Map<T, ?> hashmap, T key, V value) {
        try {
            return hashmap.containsKey(key) ? (V) hashmap.get(key) : value;
        } catch(ClassCastException e) {
            return value;
        }
    }
}
