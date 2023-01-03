package whackamole.whackamole;


import org.bukkit.ChatColor;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Translator {
    public static Translator Instance;
    private Config config;
    public Locale currentLocale = new Locale("en", "US");
    public ResourceBundle Messages;
    public String MAIN_OLDVERSION;
    public String UPDATEFAIL;
    public String LOGGER_WARNING;
    public String LOGGER_ERROR;
    public String CONFIG_OLDVERSION;
    public String CONFIG_TICKET_NAME;
    public String CONFIG_TICKET_LORE1;
    public String CONFIG_TICKET_LORE2;
    public String CONFIG_TICKET_LORE3;
    public String YML_NOTFOUNDEXCEPTION;
    public String YML_SAVEDFILE;
    public String YML_DELETEDFILE;
    public String YML_CREATEFILE;
    public String YML_CREATEFAIL;
    public String COMMANDS_TIPS_NAME;
    public String COMMANDS_TIPS_DIRECTION;
    public String COMMANDS_TIPS_JACKPOT;
    public String COMMANDS_TIPS_JACKPOTSPAWNS;
    public String COMMANDS_TIPS_MAXMISSED;
    public String COMMANDS_TIPS_HITPOINTS;
    public String COMMANDS_TIPS_INTERVAL;
    public String COMMANDS_TIPS_SPAWNCHANCE;
    public String COMMANDS_TIPS_MOLESPEED;
    public String COMMANDS_TIPS_DIFFICULTYSCALE;
    public String COMMANDS_TIPS_DIFFICULTYINCREASE;
    public String COMMANDS_CREATE;
    public String COMMANDS_CREATE_SUCCESS;
    public String COMMANDS_REMOVE;
    public String COMMANDS_REMOVE_CONFIRM;
    public String COMMANDS_REMOVE_SUCCESS;
    public String COMMANDS_BUY;
    public String COMMANDS_BUY_FULLINVENTORY;
    public String COMMANDS_BUY_CONFIRMATION;
    public String COMMANDS_BUY_LOWECONOMY;
    public String COMMANDS_BUY_ECONOMYERROR_PLAYER;
    public String COMMANDS_BUY_ECONOMYERROR_CONSOLE;
    public String COMMANDS_BUY_SUCCESS;
    public String COMMANDS_SETTINGS;
    public String COMMANDS_SETTINGS_DIRECTION;
    public String COMMANDS_SETTINGS_DIRECTION_SUCCESS;
    public String COMMANDS_SETTINGS_JACKPOT;
    public String COMMANDS_SETTINGS_JACKPOT_SUCCESS;
    public String COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE;
    public String COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_SUCCESS;
    public String COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE;
    public String COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER;
    public String COMMANDS_SETTINGS_MAXMISSED;
    public String COMMANDS_SETTINGS_MAXMISSED_SUCCESS;
    public String COMMANDS_SETTINGS_SCOREPOINTS;
    public String COMMANDS_SETTINGS_SCOREPOINTS_SUCCESS;
    public String COMMANDS_SETTINGS_SPAWNRATE;
    public String COMMANDS_SETTINGS_SPAWNRATE_SUCCESS;
    public String COMMANDS_SETTINGS_SPAWNCHANCE;
    public String COMMANDS_SETTINGS_SPAWNCHANCE_SUCCESS;
    public String COMMANDS_SETTINGS_MOLESPEED;
    public String COMMANDS_SETTINGS_MOLESPEED_SUCCESS;
    public String COMMANDS_SETTINGS_DIFFICULTYSCALE;
    public String COMMANDS_SETTINGS_DIFFICULTYSCALE_SUCCESS;
    public String COMMANDS_SETTINGS_DIFFICULTYINCREASE;
    public String COMMANDS_SETTINGS_DIFFICULTYINCREASE_SUCCESS;
    public String COMMANDS_RELOAD;
    public String COMMANDS_RELOAD_SUCCESS;
    public String COMMANDS_ONGRID_FAIL_PLAYER;
    public String COMMANDS_ONGRID_FAIL_CONSOLE;
    public String ECON_INVALIDECONOMY;
    public String ECON_INVALIDVAULT;
    public String ECON_INVALIDOBJECTIVE;
    public String GRID_EMPTYGRID;
    public String GRID_INVALIDSIZE;
    public String MANAGER_LOADINGGAMES;
    public String MANAGER_NOGAMESFOUND;
    public String MANAGER_NAMEEXISTS;
    public String MANAGER_TICKETUSE_GAMENOTFOUND;
    public String MANAGER_TICKETUSE_SUCCESS;
    public String MANAGER_TICKETUSE_NOCOOLDOWN;
    public String MANAGER_TICKETUSE_NOPERMISSION;
    public String GAME_INVALIDECONOMY;
    public String GAME_STOP_REWARD_SING;
    public String GAME_STOP_REWARD_PLUR;
    public String GAME_STOP_REWARD_NONE;
    public String GAME_START_FULLINVENTORY;
    public String GAME_INVALIDCOOLDOWN;
    public String GAME_ACTIONBAR_ERROR;
    public String GAME_ACTIONBAR_MOLEGAMEOVER;
    public String GAME_ACTIONBAR_GAMEOVER;
    public String GAME_MOLEMISSED;
    public String GAME_LOADSUCCESS;
    public String GAME_CONFIG_FIRSTNOTE;
    public String GAME_CONFIG_SECONDNOTE;
    public String GAME_CONFIG_THIRDNOTE;
    public String GAME_CONFIG_NAME;
    public String GAME_CONFIG_DIRECTION;
    public String GAME_CONFIG_JACKPOT;
    public String GAME_CONFIG_JACKPOTSPAWN;
    public String GAME_CONFIG_GAMELOST;
    public String GAME_CONFIG_POINTSPERKILL;
    public String GAME_CONFIG_SPAWNRATE;
    public String GAME_CONFIG_SPAWNCHANCE;
    public String GAME_CONFIG_MOLESPEED;
    public String GAME_CONFIG_DIFFICULTYSCALE;
    public String GAME_CONFIG_DIFFICULTYINCREASE;
    public String GAME_CONFIG_COOLDOWN;
    public String GAME_CONFIG_ENDMESSAGE;

    public Translator() {
        this.Messages(this.currentLocale);
        this.Translation();
    }
    public static Translator getInstance() {
        if (Translator.Instance == null) {
            Translator.Instance = new Translator();
            return Translator.Instance;
        }
        return Translator.Instance;
    }

    public void configLoad(String Language) {
        this.config = Config.getInstance();
        String[] fields = Language.split("_");
        this.currentLocale = new Locale(fields[0], fields[1]);
        this.Messages(this.currentLocale);
        this.Translation();
    }

    private void Messages(Locale currentLocale) {
        this.Messages = ResourceBundle.getBundle("Lang", currentLocale);
    }

    public String Format(String message) {
        message = message.replace("{configVersion}",this.config.configVersion);
        message = message.replace("{newConfigVersion}",this.config.newConfigVersion);
        message = message.replace("{commandSettings}", "/wam " + this.COMMANDS_SETTINGS);
        message = message.replace("{commandRemove}", "/wam " + this.COMMANDS_REMOVE);
        message = message.replace("{Symbol}", this.config.SYMBOL);
        message = message.replace("{ticketPrice}",  "" + this.config.TICKETPRICE);
        message = message.replace("{currencyPlur}",  this.config.CURRENCY_PLUR);
        message = message.replace("{commandBuy}",  "/wam " + this.COMMANDS_BUY);
        message = message.replace("{currencySing}",  this.config.CURRENCY_SING);

        return Color(message);
    }
    public String Format(String message, String placeholder) {
        message = message.replace("{Symbol}", this.config.SYMBOL);
        message = message.replace("{currencyPlur}",  this.config.CURRENCY_PLUR);
        message = message.replace("{currencySing}",  this.config.CURRENCY_SING);
        message = message.replace("{missedMoles}", placeholder);
        message = message.replace("{Score}", placeholder);
        message = message.replace("{gameName}", placeholder);
        message = message.replace("{Player}", placeholder);
        message = message.replace("{Message}", placeholder);
        return Color(message);
    }
    public String Format(String message, String placeholder1, String placeholder2) {
        message = message.replace("{missedMoles}", placeholder1);
        message = message.replace("{maxMissed}", placeholder2);
        return Color(message);
    }
    public String Format(String message, File file) {
        message = message.replace("{File}", file.getName());
        return Color(message);
    }

    public static String Color(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }


    public void Translation() {
        this.MAIN_OLDVERSION                                        = Color(this.Messages.getString("Main.oldVersion"));
        this.UPDATEFAIL                                             = Color(this.Messages.getString("Update.updateFail"));
        this.LOGGER_WARNING                                         = Color(this.Messages.getString("Logger.Warning"));
        this.LOGGER_ERROR                                           = Color(this.Messages.getString("Logger.Error"));
        this.CONFIG_OLDVERSION                                      = Color(this.Messages.getString("Config.oldVersion"));
        this.CONFIG_TICKET_NAME                                     = Color(this.Messages.getString("Config.Ticket.Name"));
        this.CONFIG_TICKET_LORE1                                    = Color(this.Messages.getString("Config.Ticket.Lore1"));
        this.CONFIG_TICKET_LORE2                                    = Color(this.Messages.getString("Config.Ticket.Lore2"));
        this.CONFIG_TICKET_LORE3                                    = Color(this.Messages.getString("Config.Ticket.Lore3"));
        this.YML_NOTFOUNDEXCEPTION                                  = Color(this.Messages.getString("YML.notFoundException"));
        this.YML_SAVEDFILE                                          = Color(this.Messages.getString("YML.savedFile"));
        this.YML_DELETEDFILE                                        = Color(this.Messages.getString("YML.deletedFile"));
        this.YML_CREATEFILE                                         = Color(this.Messages.getString("YML.createFile"));
        this.YML_CREATEFAIL                                         = Color(this.Messages.getString("YML.createFail"));
        this.COMMANDS_TIPS_NAME                                     = Color(this.Messages.getString("Commands.Tips.Name"));
        this.COMMANDS_TIPS_DIRECTION                                = Color(this.Messages.getString("Commands.Tips.Direction"));
        this.COMMANDS_TIPS_JACKPOT                                  = Color(this.Messages.getString("Commands.Tips.Jackpot"));
        this.COMMANDS_TIPS_JACKPOTSPAWNS                            = Color(this.Messages.getString("Commands.Tips.jackpotSpawns"));
        this.COMMANDS_TIPS_MAXMISSED                                = Color(this.Messages.getString("Commands.Tips.maxMissed"));
        this.COMMANDS_TIPS_HITPOINTS                                = Color(this.Messages.getString("Commands.Tips.hitPoints"));
        this.COMMANDS_TIPS_INTERVAL                                 = Color(this.Messages.getString("Commands.Tips.Interval"));
        this.COMMANDS_TIPS_SPAWNCHANCE                              = Color(this.Messages.getString("Commands.Tips.spawnChance"));
        this.COMMANDS_TIPS_MOLESPEED                                = Color(this.Messages.getString("Commands.Tips.moleSpeed"));
        this.COMMANDS_TIPS_DIFFICULTYSCALE                          = Color(this.Messages.getString("Commands.Tips.difficultyScale"));
        this.COMMANDS_TIPS_DIFFICULTYINCREASE                       = Color(this.Messages.getString("Commands.Tips.difficultyIncrease"));
        this.COMMANDS_CREATE                                        = Color(this.Messages.getString("Commands.Create"));
        this.COMMANDS_CREATE_SUCCESS                                = Color(this.Messages.getString("Commands.Create.Success"));
        this.COMMANDS_REMOVE                                        = Color(this.Messages.getString("Commands.Remove"));
        this.COMMANDS_REMOVE_CONFIRM                                = Color(this.Messages.getString("Commands.Remove.Confirm"));
        this.COMMANDS_REMOVE_SUCCESS                                = Color(this.Messages.getString("Commands.Remove.Success"));
        this.COMMANDS_BUY                                           = Color(this.Messages.getString("Commands.Buy"));
        this.COMMANDS_BUY_FULLINVENTORY                             = Color(this.Messages.getString("Commands.Buy.fullInventory"));
        this.COMMANDS_BUY_CONFIRMATION                              = Color(this.Messages.getString("Commands.Buy.Confirmation"));
        this.COMMANDS_BUY_LOWECONOMY                                = Color(this.Messages.getString("Commands.Buy.lowEconomy"));
        this.COMMANDS_BUY_ECONOMYERROR_PLAYER                       = Color(this.Messages.getString("Commands.Buy.economyError.Player"));
        this.COMMANDS_BUY_ECONOMYERROR_CONSOLE                      = Color(this.Messages.getString("Commands.Buy.economyError.Console"));
        this.COMMANDS_BUY_SUCCESS                                   = Color(this.Messages.getString("Commands.Buy.Success"));
        this.COMMANDS_SETTINGS                                      = Color(this.Messages.getString("Commands.Settings"));
        this.COMMANDS_SETTINGS_DIRECTION                            = Color(this.Messages.getString("Commands.Settings.Direction"));
        this.COMMANDS_SETTINGS_DIRECTION_SUCCESS                    = Color(this.Messages.getString("Commands.Settings.Directions.Success"));
        this.COMMANDS_SETTINGS_JACKPOT                              = Color(this.Messages.getString("Commands.Settings.Jackpot"));
        this.COMMANDS_SETTINGS_JACKPOT_SUCCESS                      = Color(this.Messages.getString("Commands.Settings.Jackpot.Success"));
        this.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE                   = Color(this.Messages.getString("Commands.Settings.jackpotSpawnChance"));
        this.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_SUCCESS           = Color(this.Messages.getString("Commands.Settings.jackpotSpawnChance.Success"));
        this.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE     = Color(this.Messages.getString("Commands.Settings.jackpotSpawnChance.Error.Console"));
        this.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER      = Color(this.Messages.getString("Commands.Settings.jackpotSpawnChance.Error.Player"));
        this.COMMANDS_SETTINGS_MAXMISSED                            = Color(this.Messages.getString("Commands.Settings.maxMissed"));
        this.COMMANDS_SETTINGS_MAXMISSED_SUCCESS                    = Color(this.Messages.getString("Commands.Settings.maxMissed.Success"));
        this.COMMANDS_SETTINGS_SCOREPOINTS                          = Color(this.Messages.getString("Commands.Settings.scorePoints"));
        this.COMMANDS_SETTINGS_SCOREPOINTS_SUCCESS                  = Color(this.Messages.getString("Commands.Settings.scorePoints.Success"));
        this.COMMANDS_SETTINGS_SPAWNRATE                            = Color(this.Messages.getString("Commands.Settings.spawnRate"));
        this.COMMANDS_SETTINGS_SPAWNRATE_SUCCESS                    = Color(this.Messages.getString("Commands.Settings.spawnRate.Success"));
        this.COMMANDS_SETTINGS_SPAWNCHANCE                          = Color(this.Messages.getString("Commands.Settings.spawnChance"));
        this.COMMANDS_SETTINGS_SPAWNCHANCE_SUCCESS                  = Color(this.Messages.getString("Commands.Settings.spawnChance.Success"));
        this.COMMANDS_SETTINGS_MOLESPEED                            = Color(this.Messages.getString("Commands.Settings.Molespeed"));
        this.COMMANDS_SETTINGS_MOLESPEED_SUCCESS                    = Color(this.Messages.getString("Commands.Settings.Molespeed.Success"));
        this.COMMANDS_SETTINGS_DIFFICULTYSCALE                      = Color(this.Messages.getString("Commands.Settings.difficultyScale"));
        this.COMMANDS_SETTINGS_DIFFICULTYSCALE_SUCCESS              = Color(this.Messages.getString("Commands.Settings.difficultyScale.Success"));
        this.COMMANDS_SETTINGS_DIFFICULTYINCREASE                   = Color(this.Messages.getString("Commands.Settings.difficultyIncrease"));
        this.COMMANDS_SETTINGS_DIFFICULTYINCREASE_SUCCESS           = Color(this.Messages.getString("Commands.Settings.difficultyIncrease.Success"));
        this.COMMANDS_RELOAD                                        = Color(this.Messages.getString("Commands.Reload"));
        this.COMMANDS_RELOAD_SUCCESS                                = Color(this.Messages.getString("Commands.Reload.Success"));
        this.COMMANDS_ONGRID_FAIL_PLAYER                            = Color(this.Messages.getString("Commands.onGrid.Fail.Player"));
        this.COMMANDS_ONGRID_FAIL_CONSOLE                           = Color(this.Messages.getString("Commands.onGrid.Fail.Console"));
        this.ECON_INVALIDECONOMY                                    = Color(this.Messages.getString("Econ.invalidEconomy"));
        this.ECON_INVALIDVAULT                                      = Color(this.Messages.getString("Econ.invalidVault"));
        this.ECON_INVALIDOBJECTIVE                                  = Color(this.Messages.getString("Econ.invalidObjective"));
        this.GRID_EMPTYGRID                                         = Color(this.Messages.getString("Grid.emptyGrid"));
        this.GRID_INVALIDSIZE                                       = Color(this.Messages.getString("Grid.invalidSize"));
        this.MANAGER_LOADINGGAMES                                   = Color(this.Messages.getString("Manager.loadingGames"));
        this.MANAGER_NOGAMESFOUND                                   = Color(this.Messages.getString("Manager.noGamesFound"));
        this.MANAGER_NAMEEXISTS                                     = Color(this.Messages.getString("Manager.nameExists"));
        this.MANAGER_TICKETUSE_GAMENOTFOUND                         = Color(this.Messages.getString("Manager.ticketUse.gameNotFound"));
        this.MANAGER_TICKETUSE_SUCCESS                              = Color(this.Messages.getString("Manager.ticketUse.Success"));
        this.MANAGER_TICKETUSE_NOCOOLDOWN                           = Color(this.Messages.getString("Manager.ticketUse.noCooldown"));
        this.MANAGER_TICKETUSE_NOPERMISSION                         = Color(this.Messages.getString("Manager.ticketUse.noPremission"));
        this.GAME_INVALIDECONOMY                                    = Color(this.Messages.getString("Game.invalidEconomy"));
        this.GAME_STOP_REWARD_SING                                  = Color(this.Messages.getString("Game.Stop.Reward.Sing"));
        this.GAME_STOP_REWARD_PLUR                                  = Color(this.Messages.getString("Game.Stop.Reward.Plur"));
        this.GAME_STOP_REWARD_NONE                                  = Color(this.Messages.getString("Game.Stop.Reward.None"));
        this.GAME_START_FULLINVENTORY                               = Color(this.Messages.getString("Game.Start.fullInventory"));
        this.GAME_INVALIDCOOLDOWN                                   = Color(this.Messages.getString("Game.invalidCooldown"));
        this.GAME_ACTIONBAR_ERROR                                   = Color(this.Messages.getString("Game.Actionbar.Error"));
        this.GAME_ACTIONBAR_GAMEOVER                                = Color(this.Messages.getString("Game.Actionbar.gameOver"));
        this.GAME_ACTIONBAR_MOLEGAMEOVER                            = Color(this.Messages.getString("Game.Actionbar.moleGameOver"));
        this.GAME_MOLEMISSED                                        = Color(this.Messages.getString("Game.moleMissed"));
        this.GAME_LOADSUCCESS                                       = Color(this.Messages.getString("Game.loadSuccess"));
        this.GAME_CONFIG_FIRSTNOTE                                  = Color(this.Messages.getString("Game.Config.firstNote"));
        this.GAME_CONFIG_SECONDNOTE                                 = Color(this.Messages.getString("Game.Config.secondNote"));
        this.GAME_CONFIG_THIRDNOTE                                  = Color(this.Messages.getString("Game.Config.thirdNote"));
        this.GAME_CONFIG_NAME                                       = Color(this.Messages.getString("Game.Config.Name"));
        this.GAME_CONFIG_DIRECTION                                  = Color(this.Messages.getString("Game.Config.Direction"));
        this.GAME_CONFIG_JACKPOT                                    = Color(this.Messages.getString("Game.Config.Jackpot"));
        this.GAME_CONFIG_JACKPOTSPAWN                               = Color(this.Messages.getString("Game.Config.jackpotSpawn"));
        this.GAME_CONFIG_GAMELOST                                   = Color(this.Messages.getString("Game.Config.gameLost"));
        this.GAME_CONFIG_POINTSPERKILL                              = Color(this.Messages.getString("Game.Config.pointsPerKill"));
        this.GAME_CONFIG_SPAWNRATE                                  = Color(this.Messages.getString("Game.Config.spawnRate"));
        this.GAME_CONFIG_SPAWNCHANCE                                = Color(this.Messages.getString("Game.Config.spawnChance"));
        this.GAME_CONFIG_MOLESPEED                                  = Color(this.Messages.getString("Game.Config.Molespeed"));
        this.GAME_CONFIG_DIFFICULTYSCALE                            = Color(this.Messages.getString("Game.Config.difficultyScale"));
        this.GAME_CONFIG_DIFFICULTYINCREASE                         = Color(this.Messages.getString("Game.Config.difficultyIncrease"));
        this.GAME_CONFIG_COOLDOWN                                   = Color(this.Messages.getString("Game.Config.Cooldown"));
        this.GAME_CONFIG_ENDMESSAGE                                 = Color(this.Messages.getString("Game.Config.endMessage"));
    }
}
