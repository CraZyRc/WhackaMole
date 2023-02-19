package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import whackamole.whackamole.DB.SQLite;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Hashtable;
import java.util.UUID;

public class Commands {
    private Econ econ = new Econ();
    public GamesManager manager = GamesManager.getInstance();

    private final Hashtable<UUID, Long> buyTicket = new Hashtable<>();
    private final Hashtable<UUID, Long> removeGame = new Hashtable<>();

    public Commands(Main main) {

        String gameNameTip = Translator.COMMANDS_TIPS_NAME.Format();
        String DirectionTip = Translator.COMMANDS_TIPS_DIRECTION.Format();
        String jackpotTip = Translator.COMMANDS_TIPS_JACKPOT.Format();
        String jackpotSpawnTip = Translator.COMMANDS_TIPS_SPAWNCHANCE.Format();
        String maxMissedTip = Translator.COMMANDS_TIPS_MAXMISSED.Format();
        String hitpointsTip = Translator.COMMANDS_TIPS_HITPOINTS.Format();
        String intervalTip = Translator.COMMANDS_TIPS_INTERVAL.Format();
        String spawnChanceTip = Translator.COMMANDS_TIPS_SPAWNCHANCE.Format();
        String moleSpeedTip = Translator.COMMANDS_TIPS_MOLESPEED.Format();
        String diffScaleTip = Translator.COMMANDS_TIPS_DIFFICULTYSCALE.Format();
        String diffIncreaseTip = Translator.COMMANDS_TIPS_DIFFICULTYINCREASE.Format();

        new CommandAPICommand("WhackaMole")
                .withAliases("WAM", "Whack")
                .withSubcommand(new CommandAPICommand(Translator.COMMANDS_CREATE.Format())
                        .withPermission(Config.Permissions.PERM_CREATE)
                        .withArguments(new StringArgument("Game name")
                                .replaceSuggestions(
                                        ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                StringTooltip.of("WhackaGame", gameNameTip),
                                                StringTooltip.of("MOLESTER ", gameNameTip),
                                                StringTooltip.of("Something...Somthing", gameNameTip),
                                                StringTooltip.of("Moling", gameNameTip)
                                        })))
                        .executesPlayer((player, args) -> {
                            try {
                                String gameName = (String) args[0];
                                this.manager.addGame(gameName, new Grid(player.getWorld(), player), player);
                                player.sendMessage(Config.AppConfig.PREFIX
                                        + Translator.COMMANDS_CREATE_SUCCESS.Format());
                            } catch (Exception e) {
                                Logger.error(e.getMessage());
                                throw CommandAPI.fail(Config.AppConfig.PREFIX + e.getMessage());
                            }
                        }))
                .withSubcommand(new CommandAPICommand(Translator.COMMANDS_REMOVE.Format())
                        .withPermission(Config.Permissions.PERM_REMOVE)
                        .executesPlayer((player, args) -> {
                            if (this.isOnGrid(player)) {
                                if (this.removeConfirmation(player.getUniqueId())) {
                                    Game game = this.manager.getOnGrid(player);
                                    this.manager.deleteGame(game);
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_REMOVE_SUCCESS);
                                } else {
                                    player.sendMessage(Config.AppConfig.PREFIX
                                            + Translator.COMMANDS_REMOVE_CONFIRM.Format());
                                }
                            }
                        }))
                .withSubcommand(new CommandAPICommand(Translator.COMMANDS_BUY.Format())
                        .withPermission(Config.Permissions.PERM_BUY)
                        .executesPlayer((player, args) -> {
                            if (Econ.currencyType != Econ.Currency.NULL) {
                                if (this.econ.has(player, Config.Currency.TICKETPRICE)) {
                                    if (this.buyConfirmation(player.getUniqueId())) {
                                        if (player.getInventory().firstEmpty() == -1) {
                                            player.sendMessage(
                                                    Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_FULLINVENTORY);
                                        } else {
                                            player.getInventory().addItem(Config.Game.TICKET);
                                            econ.withdrawPlayer(player, Config.Currency.TICKETPRICE);
                                            player.sendMessage(Config.AppConfig.PREFIX
                                                    + Translator.COMMANDS_BUY_SUCCESS.Format());
                                        }
                                    } else {
                                        player.sendMessage(Config.AppConfig.PREFIX
                                                + Translator.COMMANDS_BUY_CONFIRMATION.Format());
                                    }
                                } else {
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_LOWECONOMY);
                                }
                            } else {
                                player.sendMessage(
                                        Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_ECONOMYERROR_PLAYER);
                                Logger.error(Translator.COMMANDS_BUY_ECONOMYERROR_CONSOLE);
                            }
                        }))
                .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS.Format())
                        .withPermission(Config.Permissions.PERM_SETTINGS)
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_DIRECTION.Format())
                                .withArguments(new StringArgument("String")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("NORTH", DirectionTip),
                                                        StringTooltip.of("NORTH_EAST", DirectionTip),
                                                        StringTooltip.of("EAST", DirectionTip),
                                                        StringTooltip.of("SOUTH_EAST", DirectionTip),
                                                        StringTooltip.of("SOUTH", DirectionTip),
                                                        StringTooltip.of("SOUTH_WEST", DirectionTip),
                                                        StringTooltip.of("WEST", DirectionTip),
                                                        StringTooltip.of("NORTH_WEST", DirectionTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.setSpawnRotation(BlockFace.valueOf((String) args[0]));
                                        player.sendMessage(Config.AppConfig.PREFIX
                                                + Translator.COMMANDS_SETTINGS_DIRECTION_SUCCESS.Format() + args[0]);
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_JACKPOT.Format())
                                .withArguments(new BooleanArgument("true/false")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("true", jackpotTip),
                                                        StringTooltip.of("false", jackpotTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.setJackpot((Boolean) args[0]);
                                        player.sendMessage(Config.AppConfig.PREFIX
                                                + Translator.COMMANDS_SETTINGS_JACKPOT_SUCCESS.Format() + args[0]);
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE.Format())
                                .withArguments(new IntegerArgument("Integer")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("1", jackpotSpawnTip),
                                                        StringTooltip.of("10", jackpotSpawnTip),
                                                        StringTooltip.of("50", jackpotSpawnTip),
                                                        StringTooltip.of("100", jackpotSpawnTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        if ((int) args[0] <= 100) {
                                            Game game = this.manager.getOnGrid(player);
                                            game.setJackpotSpawn((int) args[0]);
                                            player.sendMessage(Config.AppConfig.PREFIX
                                                    + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_SUCCESS.Format()
                                                    + args[0]);
                                        } else {
                                            Logger.error(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE
                                                    .Format(player.getDisplayName()));
                                            player.sendMessage(Config.AppConfig.PREFIX
                                                    + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER);
                                        }
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_MAXMISSED.Format())
                                .withArguments(new IntegerArgument("Moles missed max")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("1", maxMissedTip),
                                                        StringTooltip.of("2", maxMissedTip),
                                                        StringTooltip.of("3", maxMissedTip),
                                                        StringTooltip.of("600", maxMissedTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.setMaxMissed((int) args[0]);
                                        player.sendMessage(Config.AppConfig.PREFIX
                                                + Translator.COMMANDS_SETTINGS_MAXMISSED_SUCCESS.Format() + args[0]);
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_SCOREPOINTS.Format())
                                .withArguments(new IntegerArgument("Number of points")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("1", hitpointsTip),
                                                        StringTooltip.of("2", hitpointsTip),
                                                        StringTooltip.of("3", hitpointsTip),
                                                        StringTooltip.of("600", hitpointsTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.setPointsPerKill((Integer) args[0]);
                                        player.sendMessage(Config.AppConfig.PREFIX
                                                + Translator.COMMANDS_SETTINGS_SCOREPOINTS_SUCCESS.Format() + args[0]);
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_SPAWNRATE.Format())
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("1", intervalTip),
                                                        StringTooltip.of("2", intervalTip),
                                                        StringTooltip.of("3", intervalTip),
                                                        StringTooltip.of("600", intervalTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.setInterval((double) args[0]);
                                        player.sendMessage(Config.AppConfig.PREFIX
                                                + Translator.COMMANDS_SETTINGS_SPAWNRATE_SUCCESS.Format() + args[0]);
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_SPAWNCHANCE.Format())
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("1", spawnChanceTip),
                                                        StringTooltip.of("10", spawnChanceTip),
                                                        StringTooltip.of("50", spawnChanceTip),
                                                        StringTooltip.of("100", spawnChanceTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        if ((double) args[0] <= 100) {
                                            Game game = this.manager.getOnGrid(player);
                                            game.setSpawnChance((double) args[0]);
                                            player.sendMessage(Config.AppConfig.PREFIX
                                                    + Translator.COMMANDS_SETTINGS_SPAWNCHANCE_SUCCESS.Format()
                                                    + args[0]);
                                        } else {
                                            Logger.error(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE
                                                    .Format(player.getDisplayName()));
                                            player.sendMessage(Config.AppConfig.PREFIX
                                                    + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER);
                                        }
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_MOLESPEED.Format())
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("0.5", moleSpeedTip),
                                                        StringTooltip.of("1", moleSpeedTip),
                                                        StringTooltip.of("2", moleSpeedTip),
                                                        StringTooltip.of("4", moleSpeedTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.setMoleSpeed((double) args[0]);
                                        player.sendMessage(Config.AppConfig.PREFIX
                                                + Translator.COMMANDS_SETTINGS_MOLESPEED_SUCCESS.Format() + args[0]);
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE.Format())
                                .withArguments(new DoubleArgument("Double")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("1", diffScaleTip),
                                                        StringTooltip.of("5", diffScaleTip),
                                                        StringTooltip.of("10", diffScaleTip),
                                                        StringTooltip.of("20", diffScaleTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.setDifficultyScale((double) args[0]);
                                        player.sendMessage(Config.AppConfig.PREFIX
                                                + Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE_SUCCESS.Format()
                                                + args[0]);
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand(Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE.Format())
                                .withArguments(new IntegerArgument("int")
                                        .replaceSuggestions(ArgumentSuggestions
                                                .stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                                        StringTooltip.of("1", diffIncreaseTip),
                                                        StringTooltip.of("5", diffIncreaseTip),
                                                        StringTooltip.of("10", diffIncreaseTip),
                                                        StringTooltip.of("20", diffIncreaseTip)
                                                })))
                                .executesPlayer((player, args) -> {
                                    if (this.isOnGrid(player)) {
                                        Game game = this.manager.getOnGrid(player);
                                        game.setDifficultyScore((int) args[0]);
                                        player.sendMessage(Config.AppConfig.PREFIX
                                                + Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE_SUCCESS.Format()
                                                + args[0]);
                                    }
                                })))
                .withSubcommand(new CommandAPICommand(Translator.COMMANDS_RELOAD.Format())
                        .withPermission(Config.Permissions.PERM_RELOAD)
                        .executes((sender, args) -> {
                            this.manager.onUnload();

                            SQLite.onLoad();
                            Config.onLoad(main);
                            Translator.onLoad();
                            Econ.onEnable();
                            this.manager.onLoad(main);

                            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_RELOAD_SUCCESS);
                            Logger.success("Done! V" + main.getDescription().getVersion());
                            return 0;

                        }))
                .register();
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
            Logger.error(Translator.COMMANDS_ONGRID_FAIL_CONSOLE.Format(player.getDisplayName()));
            throw CommandAPI.fail(Config.AppConfig.PREFIX + Translator.COMMANDS_ONGRID_FAIL_PLAYER);
        }
    }

}
