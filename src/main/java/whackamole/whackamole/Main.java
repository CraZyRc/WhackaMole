package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import whackamole.whackamole.DB.SQLite;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;

public final class Main extends JavaPlugin {
    public GamesManager manager = GamesManager.getInstance();
    private boolean valid_config = false;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(false));

        Logger.onLoad(this);
        valid_config = Config.onLoad(this);
        if (! valid_config) return;

        Translator.onLoad();

        SQLite.onLoad();
        
    }

    @Override
    public void onEnable() {
        if (!valid_config) {
            Logger.error(Translator.MAIN_CONFIGLOADFAIL);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Econ.onEnable(this);
        try {
            this.manager.onLoad(this);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        new Commands(this);

        CommandAPI.onEnable(this);
        
        this.getServer().getPluginManager().registerEvents(this.manager, this);
        
        new Updater(this, 106405);
        Logger.success("Done! V" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        this.manager.onUnload();
    }

}
