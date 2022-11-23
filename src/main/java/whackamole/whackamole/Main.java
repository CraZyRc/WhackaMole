package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public final class Main extends JavaPlugin {

    public Config config;
    public Logger logger = Logger.getInstance();
    public GamesManager gameManager;
    public static Plugin plugin;

    @Override
    public void onLoad() {
        Logger.Prefix = this.getDescription().getPrefix();
        this.config = Config.getInstance(this);
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(false));
        Main.plugin = this;

        new CommandAPICommand("WhackaMole")
                .withAliases("WAM", "Whack")
                .withSubcommand(new CommandAPICommand("create")
                        .withPermission(this.config.PERM_CREATE)
                        .withArguments(new StringArgument("Game Name"))
                        .executesPlayer((player, args) -> {
                            if (!(player.hasPermission(this.config.PERM_ALL) || player.hasPermission(this.config.PERM_CREATE))) {
                                player.sendMessage(this.config.PREFIX + this.config.NO_PERM);
                                return;
                            }
                            try {
                                String gameName = (String) args[0];
                                this.gameManager.addGame(gameName, new Grid(player.getWorld(), player));
                                player.sendMessage(this.config.PREFIX + "Game succesfully created, feel free to play around with the settings (" + ChatColor.AQUA + "/WAM settings" + ChatColor.WHITE + ")");
                            } catch (Exception e) {
                                this.logger.error(e.getMessage());
                                throw CommandAPI.fail(this.config.PREFIX + e.getMessage());
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("remove")
                        .withPermission(this.config.PERM_REMOVE)
                        .executesPlayer((player, args) -> {
                            GameHandler game = this.gameManager.getOnGrid(player);
                            if (!(player.hasPermission(this.config.PERM_ALL) || player.hasPermission(this.config.PERM_REMOVE))) {
                                player.sendMessage(this.config.PREFIX + this.config.NO_PERM);
                                return;
                            }
                            if (game != null) {
                                    this.gameManager.removeGame(game);
                            } else {throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game Grid you wish to remove");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("settings")
                        .withPermission(this.config.PERM_SETTINGS)
                        .withSubcommand(new CommandAPICommand("cash-hat")
                                .withArguments(new BooleanArgument("true/false"))
                                .executesPlayer((player, args) -> {
                                    GameHandler game = this.gameManager.getOnGrid(player);
                                    if (game != null) {
                                        game.cashHats = (Boolean) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the Cash-Hats value to: " + ChatColor.AQUA + args[0]);
                                    } else {
                                        this.logger.error("Player settings change failed: Player is not standing on a game grid");
                                        throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game Grid to edit the game grid");
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("Interval")
                                .withArguments(new LongArgument("Interval"))
                                .executesPlayer((player, args) -> {
                                    GameHandler game = this.gameManager.getOnGrid(player);
                                    if (game != null) {
                                        game.Interval = (Long) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the Interval value to: " + ChatColor.AQUA + args[0]);
                                    } else {
                                        this.logger.error("Player settings change failed: Player is not standing on a game grid");
                                        throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game Grid to edit the game grid");
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("killpoints")
                                .withArguments(new IntegerArgument("Points"))
                                .executesPlayer((player, args) -> {
                                    GameHandler game = this.gameManager.getOnGrid(player);
                                    if (game != null) {
                                        game.pointsPerKill = (Integer) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the points earned per kill value to: " + ChatColor.AQUA + args[0]);
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
        this.gameManager = new GamesManager();
        this.getServer().getPluginManager().registerEvents(this.gameManager, this);
        CommandAPI.onEnable(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
