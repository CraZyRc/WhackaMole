package whackamole.whackamole;

import dev.jorel.commandapi.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;



public final class Main extends JavaPlugin {

    private Config config;
    private Logger logger = Logger.getInstance();
    private Econ econ;
    private Commands commands;
    public GamesManager manager;
    public static Plugin plugin;

    @Override
    public void onLoad() {
        Logger.Prefix = this.getDescription().getPrefix();
        this.config = Config.getInstance(this);
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(false));
        Main.plugin = this;
        this.commands = new Commands(this);
    }



    @Override
    public void onEnable() {
        this.econ = Econ.getInstance(this);
        this.manager = GamesManager.getInstance(this);
        this.commands.onEnable();
        this.getServer().getPluginManager().registerEvents(this.manager, this);
        CommandAPI.onEnable(this);
        this.logger.success("Done! V" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
<<<<<<< HEAD
=======
        if(this.manager != null) {
            this.manager.unloadGames(true);
        }
>>>>>>> 2f10a80 (Beta release:)
    }
}
