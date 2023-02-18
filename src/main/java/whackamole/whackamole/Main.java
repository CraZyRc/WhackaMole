package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public GamesManager manager = GamesManager.getInstance();
    // private Commands commands;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(false));

        Logger.onLoad(this);
        Config.onLoad(this);
        Translator.onLoad();
        
    }

    @Override
    public void onEnable() {
        Econ.onEnable();
        this.manager.onLoad(this);
        new Commands(this);

        CommandAPI.onEnable(this);
        
        this.getServer().getPluginManager().registerEvents(this.manager, this);
        new Updater(this, 106405).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                Logger.warning(Translator.MAIN_OLDVERSION);
            }
        });

        Logger.success("Done! V" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        this.manager.onUnload();
    }
}
