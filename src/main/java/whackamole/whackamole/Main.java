package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {

    public FileConfiguration configFile;
    public Config config;
    public Logger logger = Logger.getInstance();
    public GamesManager gameManager;

    private FileConfiguration gameConfig = new YamlConfiguration();


    @Override
    public void onLoad() {
        Logger.Prefix = this.getDescription().getPrefix();
        
        this.configFile = this.createConfig();
        this.config = Config.getInstance(this.configFile);

        this.gameManager = new GamesManager();
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true));

        new CommandAPICommand("WhackaMole")
                .withAliases("WAM", "Whack")
                .withSubcommand(new CommandAPICommand("create")
                        .withPermission(this.config.PERM_CREATE)
                        .withArguments(new StringArgument("Game Name"))
                        .executesPlayer((player, args) -> {
                            try {
                                String gameName = (String) args[0];
                                this.gameManager.addGame(gameName, new Grid(player.getWorld(), player));
                                player.sendMessage(this.config.PREFIX + "Game succesfully created, feel free to play around with the settings (" + ChatColor.AQUA + "/WAM settings" + ChatColor.WHITE + ")");
                            } catch (Exception e) {
                                this.logger.error(e.getMessage());
                                throw CommandAPI.fail(this.config.PREFIX + e.getMessage());
                            }
                        }))

                .withSubcommand(new CommandAPICommand("settings")
                        .withPermission(this.config.PERM_ALL)
                        .withPermission(this.config.PERM_SETTINGS)
                        .withSubcommand(new CommandAPICommand("cash-hat")
                                .withArguments(new BooleanArgument("true/false"))
                                .executesPlayer((player, args) -> {
                                    GameHandler game = this.gameManager.getOnGrid(player);
                                    if (game != null) {
                                        game.cashHats = (Boolean) args[0];
                                        game.setValues();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the Cash-Hats value to: " + ChatColor.AQUA + args[0]);
                                     } else {
                                        this.logger.error("Player settings change failed: Player is not standing on a game grid");
                                        throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game Grid to edit the game grid");
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand("Interval")
                                .withArguments(new IntegerArgument("Interval"))
                                .executesPlayer((player, args) -> {
                                    GameHandler game = this.gameManager.getOnGrid(player);
                                    if (game != null) {
                                        game.Interval = (Integer) args[0];
                                        game.setValues();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the Interval value to: " + ChatColor.AQUA + args[0]);
                                    } else {
                                        this.logger.error("Player settings change failed: Player is not standing on a game grid");
                                        throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game Grid to edit the game grid");
                                    }
                                })
                        )
                )

                .withSubcommand(new CommandAPICommand("toggle")
                        .executes((sender, args) ->{
                            this.gameManager.toggleArmorStands();
                        })
                )
                .register();
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this.gameManager, this);
        CommandAPI.onEnable(this);

    }

    private FileConfiguration createConfig() {
        this.logger.info("Loading Config...");
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            this.logger.error(ChatColor.DARK_RED + "...Config failed to load!");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        this.logger.success("...Successfully loaded the config!");

        return config;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
