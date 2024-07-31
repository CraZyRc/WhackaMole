package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import whackamole.whackamole.DB.SQLite;

import org.bukkit.plugin.java.JavaPlugin;
import whackamole.whackamole.GS.GamesManager;
import whackamole.whackamole.RS.AnimationFile;
import whackamole.whackamole.RS.RewardFile;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.Utils.*;

import java.io.File;

public final class Main extends JavaPlugin {
    public GamesManager manager = GamesManager.getInstance();
    private boolean valid_config = false;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));

        Logger.onLoad(this);
        valid_config = Config.onLoad(this);
        if (! valid_config) return;

        ResourceManager.onLoad();
        Translator.onLoad();
        SQLite.onLoad();
        RewardFile.loadFile(this);
        AnimationFile.loadFiles(this);


        /* Delete in next update */
        File gamesFolder = new File(Config.AppConfig.storageFolder + "/Games");
        if (gamesFolder.exists()) {
            for (File f : gamesFolder.listFiles()) {
                f.delete();
            }
            gamesFolder.delete();
        }


    }

    @Override
    public void onEnable() {
        if (!valid_config) {
            Logger.error(Translator.MAIN_CONFIGLOADFAIL);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!Econ.onEnable()) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.manager.onLoad(this);
        RewardsManager.onLoad();
        CommandAPI.onEnable();

        new Commands(this);

        this.getServer().getPluginManager().registerEvents(this.manager, this);
        
        new Updater(this, 106405);
        Logger.success("Done! V" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        this.manager.onUnload();
    }

}
