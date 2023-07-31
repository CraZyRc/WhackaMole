package whackamole.whackamole;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.ChatColor;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.util.*;

public class Commands {
    private Econ econ = new Econ();
    public GamesManager manager = GamesManager.getInstance();

    private final Hashtable<UUID, Long> buyTicket = new Hashtable<>();
    private final Hashtable<UUID, Long> removeGame = new Hashtable<>();
    String gameNameTip      = String.valueOf(Translator.COMMANDS_TIPS_NAME);
    String DirectionTip     = String.valueOf(Translator.COMMANDS_TIPS_DIRECTION);
    String jackpotTip       = String.valueOf(Translator.COMMANDS_TIPS_JACKPOT);
    String jackpotSpawnTip  = String.valueOf(Translator.COMMANDS_TIPS_SPAWNCHANCE);
    String maxMissedTip     = String.valueOf(Translator.COMMANDS_TIPS_MAXMISSED);
    String hitpointsTip     = String.valueOf(Translator.COMMANDS_TIPS_HITPOINTS);
    String intervalTip      = String.valueOf(Translator.COMMANDS_TIPS_INTERVAL);
    String spawnChanceTip   = String.valueOf(Translator.COMMANDS_TIPS_SPAWNCHANCE);
    String moleSpeedTip     = String.valueOf(Translator.COMMANDS_TIPS_MOLESPEED);
    String diffScaleTip     = String.valueOf(Translator.COMMANDS_TIPS_DIFFICULTYSCALE);
    String diffIncreaseTip  = String.valueOf(Translator.COMMANDS_TIPS_DIFFICULTYINCREASE);
    String cooldownTip      = String.valueOf(Translator.COMMANDS_TIPS_COOLDOWN);
    String moleHeadTip      = String.valueOf(Translator.COMMANDS_TIPS_MOLEHEAD);
    String highscoreTip     = String.valueOf(Translator.COMMANDS_TIPS_HIGHSCORE);
    String teleportTip     = String.valueOf(Translator.COMMANDS_TIPS_TELEPORT);
    String streakTip     = String.valueOf(Translator.COMMANDS_TIPS_STREAK);
    public Settings settingType = Settings.NULL;
    enum Settings {
        NULL,
        DIRECTION(Translator.COMMANDS_SETTINGS_DIRECTION),
        HASJACKPOT(Translator.COMMANDS_SETTINGS_JACKPOT),
        JACKPOTSPAWNCHANCE(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE),
        MISSCOUNT(Translator.COMMANDS_SETTINGS_MAXMISSED),
        SCOREPOINTS(Translator.COMMANDS_SETTINGS_SCOREPOINTS),
        SPAWNTIMER(Translator.COMMANDS_SETTINGS_SPAWNRATE),
        SPAWNCHANCE(Translator.COMMANDS_SETTINGS_SPAWNCHANCE),
        MOLESPEED(Translator.COMMANDS_SETTINGS_MOLESPEED),
        DIFFICULTYSCALE(Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE),
        DIFFICULTYSCORE(Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE),
        COOLDOWN(Translator.COMMANDS_SETTINGS_COOLDOWN),
        MOLEHEAD(Translator.COMMANDS_SETTINGS_MOLEHEAD),
        JACKPOTHEAD(Translator.COMMANDS_SETTINGS_JACKPOTHEAD);

        Translator value;
        Settings() {}
        Settings(Translator settings) {
            this.value = settings;
        }

        @Override
        public String toString() {
            return this.value.Format();
        }
    }

    public Commands(Main main) {
        new CommandAPICommand("WhackaMole")
                .withAliases("WAM", "Whack")
                .withSubcommand(new CommandAPICommand(String.valueOf(Translator.COMMANDS_CREATE))
                        .withPermission(Config.Permissions.PERM_CREATE)
                        .withArguments(new StringArgument("Game name")
                                .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                                        StringTooltip.ofString("WhackaGame", gameNameTip),
                                        StringTooltip.ofString("MOLESTER ", gameNameTip),
                                        StringTooltip.ofString("Something...Somthing", gameNameTip),
                                        StringTooltip.ofString("Moling", gameNameTip)
                                }))
                        )
                        .executesPlayer((player, args) -> {
                            try {
                                String gameName = (String) args[0];
                                this.manager.addGame(gameName, new Grid(player.getWorld(), player), player);
                                player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_CREATE_SUCCESS.Format());
                            } catch (Exception e) {
                                Logger.error(e.getMessage());
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand(String.valueOf(Translator.COMMANDS_REMOVE))
                        .withPermission(Config.Permissions.PERM_REMOVE)
                        .withArguments(gameNameArgument("Game"))
                        .executesPlayer((player, args) -> {
                            if (this.removeConfirmation(player.getUniqueId())) {
                                Game game = (Game) args[0];
                                this.manager.deleteGame(game);
                                player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_REMOVE_SUCCESS);
                            } else {
                                player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_REMOVE_CONFIRM.Format());
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand(String.valueOf(Translator.COMMANDS_BUY))
                        .withPermission(Config.Permissions.PERM_BUY)
                        .executesPlayer((player, args) -> {
                            if (Econ.currencyType != Econ.Currency.NULL) {
                                if (this.econ.has(player, Config.Currency.TICKETPRICE)) {
                                    if (this.buyConfirmation(player.getUniqueId())) {
                                        if (player.getInventory().firstEmpty() == -1) {
                                            player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_FULLINVENTORY);
                                        } else {
                                            player.getInventory().addItem(Config.Game.TICKET);
                                            econ.withdrawPlayer(player, Config.Currency.TICKETPRICE);
                                            player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_SUCCESS.Format());
                                        }
                                    } else {
                                        player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_CONFIRMATION.Format());
                                    }
                                } else {
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_LOWECONOMY);
                                }
                            } else {
                                player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_BUY_ECONOMYERROR_PLAYER);
                                Logger.error(Translator.COMMANDS_BUY_ECONOMYERROR_CONSOLE.Format(player));
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand(String.valueOf(Translator.COMMANDS_SETTINGS))
                        .withPermission(Config.Permissions.PERM_SETTINGS)
                        .withArguments(gameNameArgument("Game"))
                        .executesPlayer((player, args) -> {
                            Game game = (Game) args[0];
                            var settings = game.getSettings();
                            String line = ChatColor.YELLOW + "\n| ";
                            String outputString = ChatColor.YELLOW + "\n[>------------------------------------<]\n" +
                                    "|" + ChatColor.WHITE + " Game: " + ChatColor.AQUA + game.getName() +
                                    line +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_DIRECTION            + ": " + ChatColor.AQUA + settings.spawnRotation +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_JACKPOT              + ": " + ChatColor.AQUA + settings.hasJackpot +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE   + ": " + ChatColor.AQUA + settings.jackpotSpawnChance +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MAXMISSED            + ": " + ChatColor.AQUA + settings.missCount +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_SCOREPOINTS          + ": " + ChatColor.AQUA + settings.scorePoints +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_SPAWNRATE            + ": " + ChatColor.AQUA + settings.spawnTimer +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_SPAWNCHANCE          + ": " + ChatColor.AQUA + settings.spawnChance +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_MOLESPEED            + ": " + ChatColor.AQUA + settings.moleSpeed +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE      + ": " + ChatColor.AQUA + settings.difficultyScale +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE   + ": " + ChatColor.AQUA + settings.difficultyScore +
                                    line + ChatColor.WHITE + Translator.COMMANDS_SETTINGS_COOLDOWN             + ": " + ChatColor.AQUA + settings.getCooldown() +
                                    ChatColor.YELLOW + "\n| \n[>------------------------------------<]";
                            player.sendMessage(outputString);

                        })
                )
                .withSubcommand(new CommandAPICommand(String.valueOf(Translator.COMMANDS_SETTINGS))
                        .withPermission(Config.Permissions.PERM_SETTINGS)
                        .withArguments(gameNameArgument("Game"))
                        .withArguments(settingsArgument())
                        .executesPlayer((player, args) -> {
                            Game game = (Game) args[0];
                            switch (this.settingType) {
                                case NULL -> Logger.error(Translator.COMMANDS_ARGUMENTS_INVALIDSETTING);
                                case DIRECTION -> {
                                    game.setSpawnRotation(BlockFace.valueOf((String) args[2]));
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_DIRECTION_SUCCESS.Format(args[2]));
                                }
                                case HASJACKPOT -> {
                                    game.setJackpot(Boolean.parseBoolean((String) args[2]));
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOT_SUCCESS.Format(args[2]));
                                }
                                case JACKPOTSPAWNCHANCE -> {
                                    if (Integer.parseInt((String) args[2]) <= 100) {
                                        game.setJackpotSpawn(Integer.parseInt((String) args[2]));
                                        player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_SUCCESS.Format(args[2].toString()));
                                    } else {
                                        Logger.error(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE.Format(player.getDisplayName()));
                                        player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER);
                                    }
                                }
                                case MISSCOUNT -> {
                                    game.setMaxMissed(Integer.parseInt((String) args[2]));
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MAXMISSED_SUCCESS.Format(args[2].toString()));
                                }
                                case SCOREPOINTS -> {
                                    game.setPointsPerKill (Integer.parseInt((String) args[2]));
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_SCOREPOINTS_SUCCESS.Format(args[2].toString()));
                                }
                                case SPAWNTIMER -> {
                                    game.setInterval (Double.parseDouble((String) args[2]));
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_SPAWNRATE_SUCCESS.Format(args[2].toString()));
                                }
                                case SPAWNCHANCE -> {
                                    if (Double.parseDouble((String) args[2]) <= 100) {
                                        game.setSpawnChance(Double.parseDouble((String) args[2]));
                                        player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_SPAWNCHANCE_SUCCESS.Format(args[2].toString()));
                                    } else {
                                        Logger.error(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE.Format(player.getDisplayName()));
                                        player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER);
                                    }
                                }
                                case MOLESPEED -> {
                                    game.setMoleSpeed(Double.parseDouble((String) args[2]));
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MOLESPEED_SUCCESS.Format(args[2].toString()));
                                }
                                case DIFFICULTYSCALE -> {
                                    game.setDifficultyScale(Double.parseDouble((String) args[2]));
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE_SUCCESS.Format(args[2].toString()));
                                }
                                case DIFFICULTYSCORE -> {
                                    game.setDifficultyScore(Integer.parseInt((String) args[2]));
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE_SUCCESS.Format(args[2].toString()));
                                }
                                case COOLDOWN -> {
                                    game.setCooldown((String) args[2]);
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_COOLDOWN_SUCCESS.Format(args[2]));
                                }
                                case MOLEHEAD -> {
                                    game.setMoleHead((String) args[2]);
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MOLEHEAD_SUCCESS.Format(args[2]));
                                }
                                case JACKPOTHEAD -> {
                                    game.setJackpotHead((String) args[2]);
                                    player.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTHEAD_SUCCESS.Format(args[2]));
                                }
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand(String.valueOf(Translator.COMMANDS_POSITIONS))
                        .withPermission(Config.Permissions.PERM_POSITIONS)
                        .withArguments(positionsTypeArgument())
                        .executes(((sender, args) -> {
                            if (!args[1].equals("failed")) sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_POSITIONS_SUCCESS.Format( args[1]));
                        }))
                )
                .withSubcommand(new CommandAPICommand(String.valueOf(Translator.COMMANDS_RELOAD))
                        .withPermission(Config.Permissions.PERM_RELOAD)
                        .executes((sender, args) ->{
                            this.manager.unloadGames();
                            Config.onLoad(main);
                            Logger.onLoad(main);
                            Econ.onEnable(main);
                            try {
                                this.manager.loadGames();
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_RELOAD_SUCCESS);
                            Logger.success("Done! V" + main.getDescription().getVersion());
                            return 0;

                        })
                )
                .withSubcommand(new CommandAPICommand(String.valueOf(Translator.COMMANDS_TOP))
                        .withPermission(Config.Permissions.PERM_TOP)
                        .withArguments(topTypeArgument())
                        .executes((sender, args) ->{
                            sender.sendMessage((String) args[1]);
                        })
                )
                .register();
    }
    public Argument<Game> gameNameArgument(String name) {
        return new CustomArgument<>(new StringArgument(name), customArgumentInfo -> {
            for (Game game : this.manager.games) {
                if (game.getName().equals(customArgumentInfo.input())) {
                    return game;
                }
            }
            String Arg = String.valueOf(new CustomArgument.MessageBuilder().appendArgInput());
            throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder(Translator.COMMANDS_ARGUMENTS_UNKNOWNGAMENAME.Format(Arg)));
        }).replaceSuggestions(ArgumentSuggestions.strings(S ->
                (this.manager == null) ? List.of("").toArray(new String[0]) :
                        this.manager.games.stream()
                                .map(Game::getName)
                                .toList().toArray(new String[0])));
    }
    private List<Argument<?>> settingsArgument() {
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(new CustomArgument<>(new TextArgument("Settings"), Info -> {
            if (Settings.DIRECTION.toString().equals(Info.input()))                 return Settings.DIRECTION;
            if (Settings.HASJACKPOT.toString().equals(Info.input()))                return Settings.HASJACKPOT;
            if (Settings.JACKPOTSPAWNCHANCE.toString().equals(Info.input()))        return Settings.JACKPOTSPAWNCHANCE;
            if (Settings.MISSCOUNT.toString().equals(Info.input()))                 return Settings.MISSCOUNT;
            if (Settings.SCOREPOINTS.toString().equals(Info.input()))               return Settings.SCOREPOINTS;
            if (Settings.SPAWNTIMER.toString().equals(Info.input()))                return Settings.SPAWNTIMER;
            if (Settings.SPAWNCHANCE.toString().equals(Info.input()))               return Settings.SPAWNCHANCE;
            if (Settings.MOLESPEED.toString().equals(Info.input()))                 return Settings.MOLESPEED;
            if (Settings.DIFFICULTYSCALE.toString().equals(Info.input()))           return Settings.DIFFICULTYSCALE;
            if (Settings.DIFFICULTYSCORE.toString().equals(Info.input()))           return Settings.DIFFICULTYSCORE;
            if (Settings.COOLDOWN.toString().equals(Info.input()))                  return Settings.COOLDOWN;
            if (Settings.MOLEHEAD.toString().equals(Info.input()))                  return Settings.MOLEHEAD;
            if (Settings.JACKPOTHEAD.toString().equals(Info.input()))               return Settings.JACKPOTHEAD;
            return Settings.NULL;

        }).replaceSuggestions(ArgumentSuggestions.strings(String.valueOf(Translator.COMMANDS_SETTINGS_DIRECTION)
                , String.valueOf(Translator.COMMANDS_SETTINGS_JACKPOT)
                , String.valueOf(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE)
                , String.valueOf(Translator.COMMANDS_SETTINGS_MAXMISSED)
                , String.valueOf(Translator.COMMANDS_SETTINGS_SCOREPOINTS)
                , String.valueOf(Translator.COMMANDS_SETTINGS_SPAWNRATE)
                , String.valueOf(Translator.COMMANDS_SETTINGS_SPAWNCHANCE)
                , String.valueOf(Translator.COMMANDS_SETTINGS_MOLESPEED)
                , String.valueOf(Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE)
                , String.valueOf(Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE)
                , String.valueOf(Translator.COMMANDS_SETTINGS_COOLDOWN)
                , String.valueOf(Translator.COMMANDS_SETTINGS_MOLEHEAD)
                , String.valueOf(Translator.COMMANDS_SETTINGS_JACKPOTHEAD)
        )));

        arguments.add(new TextArgument("settingValue").replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(info -> {
            this.settingType = (Settings) info.previousArgs()[1];
            switch (this.settingType) {
                case NULL -> Logger.error(Translator.COMMANDS_ARGUMENTS_INVALIDSETTING);
                case DIRECTION -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("NORTH",             this.DirectionTip),
                        StringTooltip.ofString("NORTH_EAST",        this.DirectionTip),
                        StringTooltip.ofString("EAST",              this.DirectionTip),
                        StringTooltip.ofString("SOUTH_EAST",        this.DirectionTip),
                        StringTooltip.ofString("SOUTH",             this.DirectionTip),
                        StringTooltip.ofString("SOUTH_WEST",        this.DirectionTip),
                        StringTooltip.ofString("WEST",              this.DirectionTip),
                        StringTooltip.ofString("NORTH_WEST",        this.DirectionTip)
                    };
                }
                case HASJACKPOT -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("true",              this.jackpotTip),
                        StringTooltip.ofString("false",             this.jackpotTip)
                    };
                }
                case JACKPOTSPAWNCHANCE -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("1",                 this.jackpotSpawnTip),
                        StringTooltip.ofString("10",                this.jackpotSpawnTip),
                        StringTooltip.ofString("50",                this.jackpotSpawnTip),
                        StringTooltip.ofString("100",               this.jackpotSpawnTip)
                    };
                }
                case MISSCOUNT -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("1",                 this.maxMissedTip),
                        StringTooltip.ofString("2",                 this.maxMissedTip),
                        StringTooltip.ofString("3",                 this.maxMissedTip),
                        StringTooltip.ofString("600",               this.maxMissedTip)
                    };
                }
                case SCOREPOINTS -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("1",                 this.hitpointsTip),
                        StringTooltip.ofString("2",                 this.hitpointsTip),
                        StringTooltip.ofString("3",                 this.hitpointsTip),
                        StringTooltip.ofString("600",               this.hitpointsTip)
                    };
                }
                case SPAWNTIMER -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("1",                 this.intervalTip),
                        StringTooltip.ofString("2",                 this.intervalTip),
                        StringTooltip.ofString("3",                 this.intervalTip),
                        StringTooltip.ofString("600",               this.intervalTip)
                    };
                }
                case SPAWNCHANCE -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("1",                 this.spawnChanceTip),
                        StringTooltip.ofString("10",                this.spawnChanceTip),
                        StringTooltip.ofString("50",                this.spawnChanceTip),
                        StringTooltip.ofString("100",               this.spawnChanceTip)
                    };
                }
                case MOLESPEED -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("0.5",               this.moleSpeedTip),
                        StringTooltip.ofString("1",                 this.moleSpeedTip),
                        StringTooltip.ofString("2",                 this.moleSpeedTip),
                        StringTooltip.ofString("4",                 this.moleSpeedTip)
                    };
                }
                case DIFFICULTYSCALE -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("1",                 this.diffScaleTip),
                        StringTooltip.ofString("5",                 this.diffScaleTip),
                        StringTooltip.ofString("10",                this.diffScaleTip),
                        StringTooltip.ofString("20",                this.diffScaleTip)
                    };
                }
                case DIFFICULTYSCORE -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("1",                 this.diffIncreaseTip),
                        StringTooltip.ofString("5",                 this.diffIncreaseTip),
                        StringTooltip.ofString("10",                this.diffIncreaseTip),
                        StringTooltip.ofString("20",                this.diffIncreaseTip)
                    };
                }
                case COOLDOWN -> {
                    return new IStringTooltip[] {
                        StringTooltip.ofString("\"24:00:00\"",      this.cooldownTip),
                        StringTooltip.ofString("\"01:00:00\"",      this.cooldownTip),
                        StringTooltip.ofString("\"00:30:00\"",      this.cooldownTip),
                        StringTooltip.ofString("\"00:10:00\"",      this.cooldownTip),
                        StringTooltip.ofString("\"00:00:10\"",      this.cooldownTip)
                    };
                }
                case MOLEHEAD -> {
                    return new IStringTooltip[] {
                            StringTooltip.ofString("\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIwNjczYTdhNTUyZmFjMDk0NjYxYjgzOGI3NWZiN2FiMTIwMGU2OTFkZTdiZWVjMGE4NDZhOWU5NzgxMDU4ZCJ9fX0=\"",      this.moleHeadTip),
                            StringTooltip.ofString("\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzU4NGIzZjEwZGE2M2QzZDVhYTVlZTBhM2NjZDA1NmJlMjhkM2NlYWY3Mjk1YjJiMDdjOTc1ODQ2MTI0MWVjMiJ9fX0=\"",      this.moleHeadTip),
                            StringTooltip.ofString("\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFhMzU0NTlhNmI0ZjA3ZDhlNjBmMTE0YjU3ZTZiNzZlOTBkOTljNmY1Mzg3OGVkYWUxOTUzZDRlZDM5NjM2In19fQ==\"",      this.moleHeadTip),
                            StringTooltip.ofString("\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjE5ZWM3MTIwOGJhZWQ0NTIzOGQyNGM0MzA2MTU5Nzc5ZWFiMTUyZGRmNGUyZGQwMzA1MDdiZDI4NjNlMjFlIn19fQ==\"",      this.moleHeadTip),
                    };
                }
                case JACKPOTHEAD -> {
                    return new IStringTooltip[] {
                            StringTooltip.ofString("\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY0NzgwY2EwZTZjMjMwOTBiZDE0OWMyYzM2ZTY0MDViMjVhZTdlYmY2NWI0ZGZlYjMxOTNiZjBlYzQ4ZGFiMyJ9fX0=\"",      this.moleHeadTip),
                            StringTooltip.ofString("\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTUyZTU2MDI4OWNlM2JlZTBjYzIzNDQ0MjdhMjc2MWE0OGQ3ODM5ZTgyZGM1MDYxNzM5NzQ4NWM0ZGEwY2I1MCJ9fX0=\"",      this.moleHeadTip),
                            StringTooltip.ofString("\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JhNWFiNjlhYjBlZmMyZmE1ZGRhMjMzYTYzMThjNWRlYmE0NTQzYmE0NjM1YWIyMWY5YmEyM2Q1ODlkZWViMCJ9fX0=\"",      this.moleHeadTip),
                            StringTooltip.ofString("\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjViMjVlMWIyNjdlZTQxOGMwM2FmZjk0OGQzNDhiOWI0ZDQ2MDNmOTYwY2QxMWIwYWRjZDYzMDk5ZTRhNTBmZCJ9fX0=\"",      this.moleHeadTip),
                    };
                }
            }
            return new IStringTooltip[] {StringTooltip.ofString("","")};
        })));
        return arguments;
    }

    private List<Argument<?>> topTypeArgument() {
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(gameNameArgument("Game"));
        arguments.add(new CustomArgument<>(new StringArgument("Type"), Info -> {
            Game game = (Game) Info.previousArgs()[0];
            String line = ChatColor.YELLOW + "\n| ";
            StringBuilder outputString = new StringBuilder(ChatColor.YELLOW + "\n[>------------------------------------<]\n" +
                    "|" + ChatColor.WHITE + " Game: " + ChatColor.AQUA + ChatColor.BOLD + game.getName());

            switch (Info.input()) {
                case "score" -> {
                    var score = game.getScoreboard().getTop(0);
                    outputString.append(line).append(ChatColor.WHITE).append("Type: ").append(ChatColor.GOLD).append("Score").append(line);
                    for (int i = 0; i < score.length; i++) {
                        outputString.append(line).append(ChatColor.DARK_AQUA).append(i + 1).append(". ").append(ChatColor.WHITE).append(score[i].player.getName()).append(" : ").append(ChatColor.AQUA).append(score[i].Score).append(ChatColor.WHITE).append(", ").append(ChatColor.YELLOW).append(score[i].Datetime.toLocalDate().toString());
                    }
                    
                }
                case "streak" -> {
                    var score = game.getScoreboard().getTop(1);
                    outputString.append(line).append(ChatColor.WHITE).append("Type: ").append(ChatColor.GOLD).append("Streak").append(line);
                    for (int i = 0; i < score.length; i++) {
                        outputString.append(line).append(ChatColor.DARK_AQUA).append(i + 1).append(". ").append(ChatColor.WHITE).append(score[i].player.getName()).append(" : ").append(ChatColor.AQUA).append(score[i].scoreStreak).append(ChatColor.WHITE).append(", ").append(ChatColor.YELLOW).append(score[i].Datetime.toLocalDate().toString());
                    }

                }
                case "moles" -> {
                    var score = game.getScoreboard().getTop(2);
                    outputString.append(line).append(ChatColor.WHITE).append("Type: ").append(ChatColor.GOLD).append("Moles").append(line);
                    for (int i = 0; i < score.length; i++) {
                        outputString.append(line).append(ChatColor.DARK_AQUA).append(i + 1).append(". ").append(ChatColor.WHITE).append(score[i].player.getName()).append(" : ").append(ChatColor.AQUA).append(score[i].molesHit).append(ChatColor.WHITE).append(", ").append(ChatColor.YELLOW).append(score[i].Datetime.toLocalDate().toString());
                    }
                }
            }
            outputString.append(ChatColor.YELLOW + "\n| \n[>------------------------------------<]");
            return outputString.toString();

        }).replaceSuggestions(ArgumentSuggestions.strings("score", "streak", "moles")));
        return arguments;
    }


    private List<Argument<?>> positionsTypeArgument() {
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(gameNameArgument("Game"));
        arguments.add(new CustomArgument<>(new StringArgument("Positions"), Info -> {
            Game game = (Game) Info.previousArgs()[0];
            Player player = (Player) Info.sender();

            switch (Info.input()) {
                case "highscore" -> game.getScoreboard().tpTopHolo(player.getLocation().add(0,2,0));
                case "teleport" -> {
                    if (!game.setTeleportLocation(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ())){
                        player.sendMessage(Config.AppConfig.PREFIX + ChatColor.DARK_RED + Translator.COMMANDS_POSITIONS_TELEPORT_ONGRID);
                        return "failed";
                    }

                }
                case "streak" -> game.setStreakHoloLocation(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ());
            }
            return Info.input();
        }).replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(suggestionInfo -> new IStringTooltip[] {
                StringTooltip.ofString("highscore", this.highscoreTip),
                StringTooltip.ofString("teleport", this.teleportTip),
                StringTooltip.ofString("streak", this.streakTip)
        })));
        return arguments;
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

}
