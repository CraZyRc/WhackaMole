package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

public class Commands {
    private Logger logger = Logger.getInstance();
    private Config config = Config.getInstance();
    private Econ econ;
    public GamesManager manager;

    private Hashtable<UUID, Long> buyTicket = new Hashtable<>();

    public Commands(Main main) {

        String gameNameTip      = "The name of this game (playfield)";
        String intervalTip      = "How often in seconds a mole will spawn (numbers)";
        String maxMissedTip     = "How many moles can be missed till game over (numbers)";
        String jackpotTip       = "Allow jackpot spawns (0.1%)";
        String hitpointsTip     = "How many points per mole hit (numbers)";

        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(new StringArgument("Game name")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("WhackaGame", gameNameTip),
                        StringTooltip.of("MOLESTER ", gameNameTip),
                        StringTooltip.of("Something...Somthing", gameNameTip),
                        StringTooltip.of("Name", gameNameTip)
                }))
        );
        arguments.add(new LongArgument("Interval")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("1", intervalTip),
                        StringTooltip.of("2", intervalTip),
                        StringTooltip.of("3", intervalTip),
                        StringTooltip.of("600", intervalTip)
                }))
        );
        arguments.add(new BooleanArgument("Jackpot")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("true", jackpotTip),
                        StringTooltip.of("false", jackpotTip)
                }))
        );
        arguments.add(new IntegerArgument("Max missed")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("1", maxMissedTip),
                        StringTooltip.of("2", maxMissedTip),
                        StringTooltip.of("3", maxMissedTip),
                        StringTooltip.of("600", maxMissedTip)
                }))
        );
        arguments.add(new IntegerArgument("Hitpoints")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("1", hitpointsTip),
                        StringTooltip.of("2", hitpointsTip),
                        StringTooltip.of("3", hitpointsTip),
                        StringTooltip.of("600", hitpointsTip)
                })));


        new CommandAPICommand("WhackaMole")
                .withPermission("WAM")
                .withAliases("WAM", "Whack")
                .withSubcommand(new CommandAPICommand("create")
                        .withPermission(this.config.PERM_CREATE)
                        .withArguments(arguments)
                        .executesPlayer((player, args) -> {
                            try {
                                String gameName = (String) args[0];
                                this.manager.addGame(gameName, new Grid(player.getWorld(), player));
                                player.sendMessage(this.config.PREFIX + "Game succesfully created, feel free to play around with the settings (" + ChatColor.AQUA + "/wam settings" + ChatColor.WHITE + ")");
                            } catch (Exception e) {
                                this.logger.error(e.getMessage());
                                throw CommandAPI.fail(this.config.PREFIX + e.getMessage());
                            }
                            Game game = this.manager.getOnGrid(player);
                            if (game !=null) {
                                try {
                                    game.Interval = (long) args[1];
                                    game.Jackpot = (boolean) args[2];
                                    game.maxMissed = (int) args[3];
                                    game.pointsPerKill = (int) args[4];
                                    game.saveGame();
                                } catch (Exception e) {
                                    this.logger.error(e.getMessage());
                                    throw CommandAPI.fail(this.config.PREFIX + e.getMessage());
                                }
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("remove")
                        .withPermission(this.config.PERM_REMOVE)
                        .executesPlayer((player, args) -> {
                            Game game = this.manager.getOnGrid(player);
                            if (game != null) {
                                this.manager.removeGame(game);
                            } else {
                                throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game game you wish to remove");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("buy")
                        .withPermission(this.config.PERM_BUY)
                        .executesPlayer((player, args) -> {
                            if (econ.has(player, this.config.TICKETPRICE)) {
                                if (this.buyConfirmation(player.getUniqueId())) {
                                    player.getInventory().addItem(manager.ticket);
                                    econ.withdrawPlayer(player, this.config.TICKETPRICE);
                                    player.sendMessage(this.config.PREFIX + "Ticket bought! removed " + ChatColor.AQUA + this.config.SYMBOL + this.config.TICKETPRICE + " " + ChatColor.WHITE + this.config.CURRENCY_PLUR);
                                } else {
                                    player.sendMessage(this.config.PREFIX + "Reset ticket costs: " + ChatColor.AQUA + this.config.SYMBOL + this.config.TICKETPRICE + " " + ChatColor.WHITE + this.config.CURRENCY_PLUR + ". To confirm, please re-enter: " + ChatColor.AQUA + "/wam buy" + ChatColor.WHITE + ". Offer stands for 10s");
                                }
                            } else {
                                player.sendMessage(this.config.PREFIX + ChatColor.RED + "Insufficient funds !");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("settings")
                        .withPermission(this.config.PERM_SETTINGS)
                        .withSubcommand(new CommandAPICommand("jackpot")
                                .withArguments(new BooleanArgument("true/false")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("true", jackpotTip),
                                                StringTooltip.of("false", jackpotTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    Game game = this.manager.getOnGrid(player);
                                    if (game != null) {
                                        game.Jackpot = (Boolean) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the Jackpot value to: " + ChatColor.AQUA + args[0]);
                                    } else {
                                        this.logger.error("Player settings change failed: Player is not standing on a game grid");
                                        throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game Grid to edit the game grid");
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("interval")
                                .withArguments(new LongArgument("Integer")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", intervalTip),
                                                StringTooltip.of("2", intervalTip),
                                                StringTooltip.of("3", intervalTip),
                                                StringTooltip.of("600", intervalTip)
                                        }))
                                )
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
                        .withSubcommand(new CommandAPICommand("max-missed")
                                .withArguments(new IntegerArgument("moles missed max")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", maxMissedTip),
                                                StringTooltip.of("2", maxMissedTip),
                                                StringTooltip.of("3", maxMissedTip),
                                                StringTooltip.of("600", maxMissedTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    Game game = this.manager.getOnGrid(player);
                                    if (game != null) {
                                        game.maxMissed = (int) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the max. moles missed value to: " + ChatColor.AQUA + args[0]);
                                    } else {
                                        this.logger.error("Player settings change failed: Player is not standing on a game grid");
                                        throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game Grid to edit the game grid");
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("hitpoints")
                                .withArguments(new IntegerArgument("Number of Points")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", hitpointsTip),
                                                StringTooltip.of("2", hitpointsTip),
                                                StringTooltip.of("3", hitpointsTip),
                                                StringTooltip.of("600", hitpointsTip)
                                        }))
                                )
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
                            this.manager.unloadGames();
                            this.manager = null;
                            this.config = Config.reload(main);
                            this.logger = Logger.reload();
                            this.econ = Econ.reload(main);
                            this.manager = new GamesManager(main);
                            sender.sendMessage(this.config.PREFIX + "Reload complete!");
                            this.logger.success("Done! V" + main.getDescription().getVersion());
                            return 0;

                        })
                )
        .register();
    }

    public void onEnable() {
        this.econ = Econ.getInstance();
        this.manager = GamesManager.getInstance();
    }

    private boolean buyConfirmation(UUID player) {
        if (this.buyTicket.containsKey(player) && this.buyTicket.get(player) > System.currentTimeMillis()) {
            this.buyTicket.remove(player);
            return true;
        } else {
            this.buyTicket.put(player, System.currentTimeMillis() + 10000);
            return false;
        }
    }
}

