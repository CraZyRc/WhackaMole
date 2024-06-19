package whackamole.whackamole;


import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;


public class Rewards {
    private static YMLFile rewardsFile;


    private static void loadFiles(Plugin main) {
        try {
            rewardsFile = new YMLFile(Config.AppConfig.storageFolder + "/rewards.yml");
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return;
        }

        try {
            if (rewardsFile.created) {
                Logger.info(Translator.YML_CREATEFILE.Format(rewardsFile));
                main.saveResource("rewards.yml", true);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }


    public static void sendScoreToPlayer(Player player, int score, Game game) {
        var message = "";
        if(score == 0) message = Translator.GAME_STOP_REWARD_NONE.Format();
        if(score == 1) message = Translator.GAME_STOP_REWARD_SING.Format(game);
        if(score >  1) message = Translator.GAME_STOP_REWARD_PLUR.Format(game);
        player.sendMessage(Config.AppConfig.PREFIX + message);
    }

    public static void onLoad(Plugin main) {
        loadFiles(main);
    }
}
