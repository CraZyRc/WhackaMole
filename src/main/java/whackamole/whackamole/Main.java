package whackamole.whackamole;

import dev.jorel.commandapi.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private Commands commands;
    public GamesManager manager;

    @Override
    public void onLoad() {
        Logger.onLoad(this);
        Config.onLoad(this);
        Translator.onLoad();
        Econ.onLoad();

        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(false));
    }

    @Override
    public void onEnable() {
        this.commands = new Commands(this);
        this.manager = GamesManager.getInstance(this);
        this.commands.onEnable();
        this.getServer().getPluginManager().registerEvents(this.manager, this);
        CommandAPI.onEnable(this);

        new Updater(this, 106405).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                Logger.warning(Translator.MAIN_OLDVERSION);
            }
        });

        Logger.success("Done! V" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        if (this.manager != null) {
            this.manager.unloadGames();
        }
    }
}
