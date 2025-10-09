package whackamole.whackamole.CD.Commands;

import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.Bukkit;
import whackamole.whackamole.Config;
import whackamole.whackamole.Main;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.ResourceManager;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

public class PluginReloadCMD extends SubCommand {

  @Override
  protected String GetName() {
    return Translator.COMMANDS_RELOAD.Format();
  }

  @Override
  protected String Permission() {
    return "wam.reload";
  }

  @Override
  protected CommandExecutor Executes() {
    return (sender, args) -> {
      var plugin = (Main) Bukkit.getPluginManager().getPlugin("WhackaMole");
      boolean langChange = Config.languageLoad(plugin);
      boolean valid_config;
      Manager.unloadGames();
      ResourceManager.onReload();
      Translator.onReload();

      valid_config = Config.configLoad(plugin);
      if (! valid_config) return;

      Logger.onLoad(plugin);
      if (!Econ.onEnable()) {
        plugin.getServer().getPluginManager().disablePlugin(plugin);
      }
      Manager.GameLoading(null);
      RewardsManager.onReload();
      if (langChange) {
        Logger.info(Translator.COMMANDS_RELOAD_KNOWNBUG.Format());
        var commandRoot = new WhackaMoleCMD();
        commandRoot.UnRegister();
        commandRoot.Register();
      }

      sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_RELOAD_SUCCESS);
      Logger.success("Done! V" + plugin.getDescription().getVersion());
    };
  }
}
