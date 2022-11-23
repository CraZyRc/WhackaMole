package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public final class Main extends JavaPlugin {

    private Config config;
    private Logger logger = Logger.getInstance();
    private Econ econ;
    public GamesManager manager;
    public static Plugin plugin;

    @Override
    public void onLoad() {
        Logger.Prefix = this.getDescription().getPrefix();
        this.config = Config.getInstance(this);
        this.econ = Econ.getInstance(this);
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
                                this.manager.addGame(gameName, new Grid(player.getWorld(), player));
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
                            Game game = this.manager.getOnGrid(player);
                            if (!(player.hasPermission(this.config.PERM_ALL) || player.hasPermission(this.config.PERM_REMOVE))) {
                                player.sendMessage(this.config.PREFIX + this.config.NO_PERM);
                                return;
                            }
                            if (game != null) {
                                    this.manager.removeGame(game);
                            } else {throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game game you wish to remove");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("buy")
                        .executesPlayer((player, args) -> {
                            Game game = this.manager.getOnGrid(player);
                            if (econ.has(player, 15)) {
                                game.removeCooldown(player.getUniqueId());
                                econ.withdrawPlayer(player, game.ticketCost);
                                player.sendMessage(this.config.PREFIX + "Cooldown removed!");
                            } else {
                                player.sendMessage(this.config.PREFIX + ChatColor.RED + "Insufficient funds !");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("settings")
                        .withPermission(this.config.PERM_SETTINGS)
                        .withSubcommand(new CommandAPICommand("cash-hat")
                                .withArguments(new BooleanArgument("true/false"))
                                .executesPlayer((player, args) -> {
                                    Game game = this.manager.getOnGrid(player);
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
                                    Game game = this.manager.getOnGrid(player);
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
                                    Game game = this.manager.getOnGrid(player);
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
                            this.manager.toggleArmorStands();
                        })
                )
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission(this.config.PERM_RELOAD)
                        .executes((sender, args) ->{

                        })
                )
                .register();
    }

    @Override
    public void onEnable() {
        this.manager = new GamesManager();
        this.getServer().getPluginManager().registerEvents(this.manager, this);
        CommandAPI.onEnable(this);
        this.logger.success("Done! V" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
    }
}
