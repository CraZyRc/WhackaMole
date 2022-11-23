package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
    private Hashtable<UUID, Long> removeGame = new Hashtable<>();

    public Commands(Main main) {

        String gameNameTip      = "The name of this game";
        String jackpotTip       = "Enable/Disable jackpot spawns";
        String jackpotSpawnTip  = "Spawn chance for a jackpot to spawn";
        String maxMissedTip     = "How many moles can be missed till game over";
        String hitpointsTip     = "How many points per successful mole hit";
        String intervalTip      = "How often in seconds a mole will spawn";
        String spawnChanceTip   = "Spawn chance per Spawn interval for a mole to spawn";
        String moleSpeedTip     = "How quickly should the mole be able to move (the lower the quicker)";
        String diffScaleTip     = "Per how many percent should the game difficulty be increased";
        String diffIncreaseTip  = "Per how many hits should the game difficulty increase";

        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(new StringArgument("Game name")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("WhackaGame", gameNameTip),
                        StringTooltip.of("MOLESTER ", gameNameTip),
                        StringTooltip.of("Something...Somthing", gameNameTip),
                        StringTooltip.of("Name", gameNameTip)
                }))
        );
        arguments.add(new BooleanArgument("Jackpot")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("true", jackpotTip),
                        StringTooltip.of("false", jackpotTip)
                }))
        );
        arguments.add(new IntegerArgument("Jackpot spawn chance")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("1", jackpotSpawnTip),
                        StringTooltip.of("10", jackpotSpawnTip),
                        StringTooltip.of("50", jackpotSpawnTip),
                        StringTooltip.of("100", jackpotSpawnTip)
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
                }))
        );
        arguments.add(new DoubleArgument("Spawn rate interval")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("1", intervalTip),
                        StringTooltip.of("2", intervalTip),
                        StringTooltip.of("3", intervalTip),
                        StringTooltip.of("600", intervalTip)
                }))
        );
        arguments.add(new DoubleArgument("Spawn chance")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("1", spawnChanceTip),
                        StringTooltip.of("10", spawnChanceTip),
                        StringTooltip.of("50", spawnChanceTip),
                        StringTooltip.of("100", spawnChanceTip)
                }))
        );
        arguments.add(new DoubleArgument("Mole speed")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("0.5", moleSpeedTip),
                        StringTooltip.of("1", moleSpeedTip),
                        StringTooltip.of("2", moleSpeedTip),
                        StringTooltip.of("4", moleSpeedTip)
                }))
        );
        arguments.add(new DoubleArgument("Difficulty scale")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("1", diffScaleTip),
                        StringTooltip.of("5", diffScaleTip),
                        StringTooltip.of("10", diffScaleTip),
                        StringTooltip.of("20", diffScaleTip)
                }))
        );
        arguments.add(new IntegerArgument("Difficulty increase")
                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                        StringTooltip.of("1", diffIncreaseTip),
                        StringTooltip.of("5", diffIncreaseTip),
                        StringTooltip.of("10", diffIncreaseTip),
                        StringTooltip.of("20", diffIncreaseTip)
                }))
        );


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
                                    game.Jackpot            = (boolean) args[1];
                                    game.jackpotSpawn       = (int) args[2];
                                    game.maxMissed          = (int) args[3];
                                    game.pointsPerKill      = (int) args[4];
                                    game.Interval           = (double) args[5];
                                    game.spawnChance        = (double) args[6];
                                    game.moleSpeed          = (double) args[7];
                                    game.difficultyScale    = (double) args[8];
                                    game.difficultyPoints   = (int) args[9];
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
                            if (this.isOnGrid(player)) {
                                if (this.removeConfirmation(player.getUniqueId())) {
                                    Game game = this.manager.getOnGrid(player);
                                    this.manager.removeGame(game);
                                    player.sendMessage(this.config.PREFIX + "Game successfully removed !");
                                } else {
                                    player.sendMessage(this.config.PREFIX + "Are you sure you wish to delete the game? To confirm, please re-enter: " + ChatColor.AQUA + "/wam remove" + ChatColor.WHITE + ". Confirmation resets after 10s");
                                }
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
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.Jackpot = (Boolean) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the jackpot value to: " + ChatColor.AQUA + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("jackpot-spawnchance")
                                .withArguments(new IntegerArgument("Integer")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", jackpotSpawnTip),
                                                StringTooltip.of("10", jackpotSpawnTip),
                                                StringTooltip.of("50", jackpotSpawnTip),
                                                StringTooltip.of("100", jackpotSpawnTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.jackpotSpawn = (int) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the jackpot spawn chance value to: " + ChatColor.AQUA + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("max-missed")
                                .withArguments(new IntegerArgument("Moles missed max")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", maxMissedTip),
                                                StringTooltip.of("2", maxMissedTip),
                                                StringTooltip.of("3", maxMissedTip),
                                                StringTooltip.of("600", maxMissedTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.maxMissed = (int) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the max. moles missed value to: " + ChatColor.AQUA + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("score-points")
                                .withArguments(new IntegerArgument("Number of points")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", hitpointsTip),
                                                StringTooltip.of("2", hitpointsTip),
                                                StringTooltip.of("3", hitpointsTip),
                                                StringTooltip.of("600", hitpointsTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.pointsPerKill = (Integer) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the points earned per kill value to: " + ChatColor.AQUA + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("spawnrate")
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", intervalTip),
                                                StringTooltip.of("2", intervalTip),
                                                StringTooltip.of("3", intervalTip),
                                                StringTooltip.of("600", intervalTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.Interval = (double) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the spawn rate value to: " + ChatColor.AQUA + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("spawnchance")
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", spawnChanceTip),
                                                StringTooltip.of("10", spawnChanceTip),
                                                StringTooltip.of("50", spawnChanceTip),
                                                StringTooltip.of("100", spawnChanceTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.spawnChance = (double) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the spawn chance value to: " + ChatColor.AQUA + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("molespeed")
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("0.5", moleSpeedTip),
                                                StringTooltip.of("1", moleSpeedTip),
                                                StringTooltip.of("2", moleSpeedTip),
                                                StringTooltip.of("4", moleSpeedTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.moleSpeed = (double) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the mole speed value to: " + ChatColor.AQUA + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("difficulty-scale")
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", diffScaleTip),
                                                StringTooltip.of("5", diffScaleTip),
                                                StringTooltip.of("10", diffScaleTip),
                                                StringTooltip.of("20", diffScaleTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.difficultyScale = (double) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the difficulty increase value to: " + ChatColor.AQUA + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("difficulty-increase")
                                .withArguments(new IntegerArgument("int")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", diffIncreaseTip),
                                                StringTooltip.of("5", diffIncreaseTip),
                                                StringTooltip.of("10", diffIncreaseTip),
                                                StringTooltip.of("20", diffIncreaseTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.difficultyPoints = (int) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + "Successfully changed the difficulty increase value to: " + ChatColor.AQUA + args[0]);
                                    }
                                })
                        )
                )
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission(this.config.PERM_RELOAD)
                        .executes((sender, args) ->{
                            this.manager.unloadGames(false);
                            this.config = Config.reload(main);
                            this.logger = Logger.reload();
                            this.econ = Econ.reload(main);
                            this.manager.loadGames();
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
    private boolean removeConfirmation(UUID player) {
        if (this.removeGame.containsKey(player) && this.removeGame.get(player) > System.currentTimeMillis()) {
            this.removeGame.remove(player);
            return true;
        } else {
            this.removeGame.put(player, System.currentTimeMillis() + 10000);
            return false;
        }
    }

    private boolean isOnGrid(Player player) throws WrapperCommandSyntaxException {
        Game game = this.manager.getOnGrid(player);
        if (game != null) {
            return true;
        } else {
            this.logger.error("Game change failed: Player is not standing on a game");
            throw CommandAPI.fail(this.config.PREFIX + "Please stand on the game field to edit the game");
        }
    }

}

