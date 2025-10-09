package whackamole.whackamole.RS;

import org.bukkit.plugin.Plugin;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

public class RewardFile {
  public static void loadFile(Plugin main) {
    YMLFile rewardsFile;
    try {
      rewardsFile = new YMLFile(Config.AppConfig.storageFolder + "/rewards.yml");
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return;
    }

    try {
      if (rewardsFile.created) {
        Logger.success(Translator.YML_CREATEFILE.Format(rewardsFile));
        main.saveResource("rewards.yml", true);
      }
    } catch (Exception e) {
      Logger.error(e.getMessage());
    }
  }
}