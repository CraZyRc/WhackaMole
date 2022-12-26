package whackamole.whackamole;

import dev.jorel.commandapi.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;



public final class Main extends JavaPlugin {

    private Translator translator = Translator.getInstance();
    private Logger logger = Logger.getInstance();
    private Commands commands;
    public GamesManager manager;
    public static Plugin plugin;

    @Override
    public void onLoad() {
        Logger.Prefix = this.getDescription().getPrefix();
        Config.getInstance(this);
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(false));
        Main.plugin = this;
    }

    @Override
    public void onEnable() {
        Config.getInstance().onEnable();
        this.commands = new Commands(this);
        Econ.getInstance(this);
        this.manager = GamesManager.getInstance(this);
        this.commands.onEnable();
        this.getServer().getPluginManager().registerEvents(this.manager, this);
        CommandAPI.onEnable(this);
        new Updater(this, 106405).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                this.logger.warning(this.translator.MAIN_OLDVERSION);
            }
        });
        this.logger.success("Done! V" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        if(this.manager != null) {
            this.manager.unloadGames(true);
        }
    }
}
