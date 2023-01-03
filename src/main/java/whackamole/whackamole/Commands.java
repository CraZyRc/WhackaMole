package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.management.StringValueExp;
import java.util.Hashtable;
import java.util.UUID;

public class Commands {
    private Logger logger = Logger.getInstance();
    private Config config = Config.getInstance();
    private final Translator translator = Translator.getInstance();
    private Econ econ;
    public GamesManager manager;

    private final Hashtable<UUID, Long> buyTicket = new Hashtable<>();
    private final Hashtable<UUID, Long> removeGame = new Hashtable<>();

    public Commands(Main main) {

        String gameNameTip      = this.translator.COMMANDS_TIPS_NAME;
        String DirectionTip     = this.translator.COMMANDS_TIPS_DIRECTION;
        String jackpotTip       = this.translator.COMMANDS_TIPS_JACKPOT;
        String jackpotSpawnTip  = this.translator.COMMANDS_TIPS_SPAWNCHANCE;
        String maxMissedTip     = this.translator.COMMANDS_TIPS_MAXMISSED;
        String hitpointsTip     = this.translator.COMMANDS_TIPS_HITPOINTS;
        String intervalTip      = this.translator.COMMANDS_TIPS_INTERVAL;
        String spawnChanceTip   = this.translator.COMMANDS_TIPS_SPAWNCHANCE;
        String moleSpeedTip     = this.translator.COMMANDS_TIPS_MOLESPEED;
        String diffScaleTip     = this.translator.COMMANDS_TIPS_DIFFICULTYSCALE;
        String diffIncreaseTip  = this.translator.COMMANDS_TIPS_DIFFICULTYINCREASE;


        new CommandAPICommand("WhackaMole")
                .withAliases("WAM", "Whack")
                .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_CREATE)
                        .withPermission(this.config.PERM_CREATE)
                        .withArguments(new StringArgument("Game name")
                                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                        StringTooltip.of("WhackaGame", gameNameTip),
                                        StringTooltip.of("MOLESTER ", gameNameTip),
                                        StringTooltip.of("Something...Somthing", gameNameTip),
                                        StringTooltip.of("Moling", gameNameTip)
                                }))
                        )
                        .executesPlayer((player, args) -> {
                            try {
                                String gameName = (String) args[0];
                                this.manager.addGame(gameName, new Grid(player.getWorld(), player), player);
                                player.sendMessage(this.config.PREFIX + this.translator.Format(this.translator.COMMANDS_CREATE_SUCCESS));
                            } catch (Exception e) {
                                this.logger.error(e.getMessage());
                                throw CommandAPI.fail(this.config.PREFIX + e.getMessage());
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_REMOVE)
                        .withPermission(this.config.PERM_REMOVE)
                        .executesPlayer((player, args) -> {
                            if (this.isOnGrid(player)) {
                                if (this.removeConfirmation(player.getUniqueId())) {
                                    Game game = this.manager.getOnGrid(player);
                                    this.manager.removeGame(game);
                                    player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_REMOVE_SUCCESS);
                                } else {
                                    player.sendMessage(this.config.PREFIX + this.translator.Format(this.translator.COMMANDS_REMOVE_CONFIRM));
                                }
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_BUY)
                        .withPermission(this.config.PERM_BUY)
                        .executesPlayer((player, args) -> {
                            if (this.econ.currencyType != Econ.Currency.NULL) {
                                if (this.econ.has(player, this.config.TICKETPRICE)) {
                                    if (this.buyConfirmation(player.getUniqueId())) {
                                        if (player.getInventory().firstEmpty() == -1) {
                                            player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_BUY_FULLINVENTORY);
                                        } else {
                                            player.getInventory().addItem(this.config.TICKET);
                                            econ.withdrawPlayer(player, this.config.TICKETPRICE);
                                            player.sendMessage(this.config.PREFIX + this.translator.Format(this.translator.COMMANDS_BUY_SUCCESS));
                                        }
                                    } else {
                                        player.sendMessage(this.config.PREFIX + this.translator.Format(this.translator.COMMANDS_BUY_CONFIRMATION));
                                    }
                                } else {
                                    player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_BUY_LOWECONOMY);
                                }
                            } else {
                                player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_BUY_ECONOMYERROR_PLAYER);
                                this.logger.error(this.translator.COMMANDS_BUY_ECONOMYERROR_CONSOLE);
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS)
                        .withPermission(this.config.PERM_SETTINGS)
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_DIRECTION)
                                .withArguments(new StringArgument("String")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("NORTH",       DirectionTip),
                                                StringTooltip.of("NORTH_EAST",  DirectionTip),
                                                StringTooltip.of("EAST",        DirectionTip),
                                                StringTooltip.of("SOUTH_EAST",  DirectionTip),
                                                StringTooltip.of("SOUTH",       DirectionTip),
                                                StringTooltip.of("SOUTH_WEST",  DirectionTip),
                                                StringTooltip.of("WEST",        DirectionTip),
                                                StringTooltip.of("NORTH_WEST",  DirectionTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.spawnRotation = BlockFace.valueOf((String) args[0]);
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_DIRECTION_SUCCESS + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_JACKPOT)
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
                                        player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_JACKPOT_SUCCESS + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE)
                                .withArguments(new IntegerArgument("Integer")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1",   jackpotSpawnTip),
                                                StringTooltip.of("10",  jackpotSpawnTip),
                                                StringTooltip.of("50",  jackpotSpawnTip),
                                                StringTooltip.of("100", jackpotSpawnTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        if ((int) args[0] <= 100) {
                                            Game game = this.manager.getOnGrid(player);
                                            game.jackpotSpawn = (int) args[0];
                                            game.saveGame();
                                            player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_SUCCESS + args[0]);
                                        } else {
                                            this.logger.error(this.translator.Format(this.translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE, player.getDisplayName()));
                                            player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER);
                                        }
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_MAXMISSED)
                                .withArguments(new IntegerArgument("Moles missed max")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1",   maxMissedTip),
                                                StringTooltip.of("2",   maxMissedTip),
                                                StringTooltip.of("3",   maxMissedTip),
                                                StringTooltip.of("600", maxMissedTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.maxMissed = (int) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_MAXMISSED_SUCCESS + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_SCOREPOINTS)
                                .withArguments(new IntegerArgument("Number of points")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1",   hitpointsTip),
                                                StringTooltip.of("2",   hitpointsTip),
                                                StringTooltip.of("3",   hitpointsTip),
                                                StringTooltip.of("600", hitpointsTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.pointsPerKill = (Integer) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_SCOREPOINTS_SUCCESS + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_SPAWNRATE)
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1",   intervalTip),
                                                StringTooltip.of("2",   intervalTip),
                                                StringTooltip.of("3",   intervalTip),
                                                StringTooltip.of("600", intervalTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.Interval = (double) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_SPAWNRATE_SUCCESS + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_SPAWNCHANCE)
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1", spawnChanceTip),
                                                StringTooltip.of("10",  spawnChanceTip),
                                                StringTooltip.of("50",  spawnChanceTip),
                                                StringTooltip.of("100", spawnChanceTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        if ((double) args[0] <= 100) {
                                            Game game = this.manager.getOnGrid(player);
                                            game.spawnChance = (double) args[0];
                                            game.saveGame();
                                            player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_SPAWNCHANCE_SUCCESS + args[0]);
                                        } else {
                                            this.logger.error(this.translator.Format(this.translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE, player.getDisplayName()));
                                            player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER);
                                        }
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_MOLESPEED)
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("0.5", moleSpeedTip),
                                                StringTooltip.of("1",   moleSpeedTip),
                                                StringTooltip.of("2",   moleSpeedTip),
                                                StringTooltip.of("4",   moleSpeedTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.moleSpeed = (double) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_MOLESPEED_SUCCESS + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_DIFFICULTYSCALE)
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1",   diffScaleTip),
                                                StringTooltip.of("5",   diffScaleTip),
                                                StringTooltip.of("10",  diffScaleTip),
                                                StringTooltip.of("20",  diffScaleTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.difficultyScale = (double) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_DIFFICULTYSCALE_SUCCESS + args[0]);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE)
                                .withArguments(new IntegerArgument("int")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("1",   diffIncreaseTip),
                                                StringTooltip.of("5",   diffIncreaseTip),
                                                StringTooltip.of("10",  diffIncreaseTip),
                                                StringTooltip.of("20",  diffIncreaseTip)
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.difficultyPoints = (int) args[0];
                                        game.saveGame();
                                        player.sendMessage(this.config.PREFIX + this.translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE_SUCCESS + args[0]);
                                    }
                                })
                        )
                )
                .withSubcommand(new CommandAPICommand(this.translator.COMMANDS_RELOAD)
                        .withPermission(this.config.PERM_RELOAD)
                        .executes((sender, args) ->{
                            this.manager.unloadGames(false);
                            this.config = Config.reload(main);
                            this.config.onEnable();
                            this.logger = Logger.reload();
                            this.econ = Econ.reload(main);
                            this.manager.loadGames();
                            sender.sendMessage(this.config.PREFIX + this.translator.COMMANDS_RELOAD_SUCCESS);
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
            this.logger.error(this.translator.Format(this.translator.COMMANDS_ONGRID_FAIL_CONSOLE, player.getDisplayName()));
            throw CommandAPI.fail(this.config.PREFIX + this.translator.COMMANDS_ONGRID_FAIL_PLAYER);
        }
    }


}

