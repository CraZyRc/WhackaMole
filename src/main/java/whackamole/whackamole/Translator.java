package whackamole.whackamole;


import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;


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

        return Config.color(message);
    }
    public String Format(String message, String placeholder) {
        message = message.replace("{Symbol}", this.config.SYMBOL);
        message = message.replace("{currencyPlur}",  this.config.CURRENCY_PLUR);
        message = message.replace("{currencySing}",  this.config.CURRENCY_SING);
        message = message.replace("{gameName}", placeholder);
        message = message.replace("{Direction}", placeholder);
        message = message.replace("{jackpotSpawn}", placeholder);
        message = message.replace("{jackpotSpawnChance}", placeholder);
        message = message.replace("{maxMissed}", placeholder);
        message = message.replace("{scorePoints}", placeholder);
        message = message.replace("{Interval}", placeholder);
        message = message.replace("{spawnChance}", placeholder);
        message = message.replace("{Molespeed}", placeholder);
        message = message.replace("{difficultyScale}", placeholder);
        message = message.replace("{difficultyIncrease}", placeholder);
        message = message.replace("{Player}", placeholder);
        message = message.replace("{Message}", placeholder);
        message = message.replace("{missedMoles}", placeholder);
        return Config.color(message);
    }
    public String Format(String message, String placeholder1, String placeholder2) {
        message = message.replace("{missedMoles}", placeholder1);
        message = message.replace("{maxMissed}", placeholder2);
        return Config.color(message);
    }
    public String Format(String message, File file) {
        message = message.replace("{File}", file.getName());
        return Config.color(message);
    }


    public void Translation() {
        this.MAIN_OLDVERSION                                = this.Messages.getString("Main.oldVersion");
        this.UPDATEFAIL                                     = this.Messages.getString("Update.updateFail");
        this.LOGGER_WARNING                                 = this.Messages.getString("Logger.Warning");
        this.LOGGER_ERROR                                   = this.Messages.getString("Logger.Error");
        this.CONFIG_OLDVERSION                              = this.Messages.getString("Config.oldVersion");
        this.CONFIG_TICKET_NAME                             = this.Messages.getString("Config.Ticket.Name");
        this.CONFIG_TICKET_LORE1                            = this.Messages.getString("Config.Ticket.Lore1");
        this.CONFIG_TICKET_LORE2                            = this.Messages.getString("Config.Ticket.Lore2");
        this.CONFIG_TICKET_LORE3                            = this.Messages.getString("Config.Ticket.Lore3");
        this.YML_NOTFOUNDEXCEPTION                          = this.Messages.getString("YML.notFoundException");
        this.YML_SAVEDFILE                                  = this.Messages.getString("YML.savedFile");
        this.YML_DELETEDFILE                                = this.Messages.getString("YML.deletedFile");
        this.YML_CREATEFILE                                 = this.Messages.getString("YML.createFile");
        this.YML_CREATEFAIL                                 = this.Messages.getString("YML.createFail");
        this.COMMANDS_TIPS_NAME                             = this.Messages.getString("Commands.Tips.Name");
        this.COMMANDS_TIPS_DIRECTION                        = this.Messages.getString("Commands.Tips.Direction");
        this.COMMANDS_TIPS_JACKPOT                          = this.Messages.getString("Commands.Tips.Jackpot");
        this.COMMANDS_TIPS_JACKPOTSPAWNS                    = this.Messages.getString("Commands.Tips.jackpotSpawns");
        this.COMMANDS_TIPS_MAXMISSED                        = this.Messages.getString("Commands.Tips.maxMissed");
        this.COMMANDS_TIPS_HITPOINTS                        = this.Messages.getString("Commands.Tips.hitPoints");
        this.COMMANDS_TIPS_INTERVAL                         = this.Messages.getString("Commands.Tips.Interval");
        this.COMMANDS_TIPS_SPAWNCHANCE                      = this.Messages.getString("Commands.Tips.spawnChance");
        this.COMMANDS_TIPS_MOLESPEED                        = this.Messages.getString("Commands.Tips.moleSpeed");
        this.COMMANDS_TIPS_DIFFICULTYSCALE                  = this.Messages.getString("Commands.Tips.difficultyScale");
        this.COMMANDS_TIPS_DIFFICULTYINCREASE               = this.Messages.getString("Commands.Tips.difficultyIncrease");
        this.COMMANDS_CREATE                                = this.Messages.getString("Commands.Create");
        this.COMMANDS_CREATE_SUCCESS                        = this.Messages.getString("Commands.Create.Success");
        this.COMMANDS_REMOVE                                = this.Messages.getString("Commands.Remove");
        this.COMMANDS_REMOVE_CONFIRM                        = this.Messages.getString("Commands.Remove.Confirm");
        this.COMMANDS_REMOVE_SUCCESS                        = this.Messages.getString("Commands.Remove.Success");
        this.COMMANDS_BUY                                   = this.Messages.getString("Commands.Buy");
        this.COMMANDS_BUY_FULLINVENTORY                     = this.Messages.getString("Commands.Buy.fullInventory");
        this.COMMANDS_BUY_CONFIRMATION                      = this.Messages.getString("Commands.Buy.Confirmation");
        this.COMMANDS_BUY_LOWECONOMY                        = this.Messages.getString("Commands.Buy.lowEconomy");
        this.COMMANDS_BUY_ECONOMYERROR_PLAYER               = this.Messages.getString("Commands.Buy.economyError.Player");
        this.COMMANDS_BUY_ECONOMYERROR_CONSOLE              = this.Messages.getString("Commands.Buy.economyError.Console");
        this.COMMANDS_BUY_SUCCESS                           = this.Messages.getString("Commands.Buy.Success");
        this.COMMANDS_SETTINGS                              = this.Messages.getString("Commands.Settings");
        this.COMMANDS_SETTINGS_DIRECTION                    = this.Messages.getString("Commands.Settings.Direction");
        this.COMMANDS_SETTINGS_DIRECTION_SUCCESS            = this.Messages.getString("Commands.Settings.Directions.Success");
        this.COMMANDS_SETTINGS_JACKPOT                      = this.Messages.getString("Commands.Settings.Jackpot");
        this.COMMANDS_SETTINGS_JACKPOT_SUCCESS              = this.Messages.getString("Commands.Settings.Jackpot.Success");
        this.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE           = this.Messages.getString("Commands.Settings.jackpotSpawnChance");
        this.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_SUCCESS   = this.Messages.getString("Commands.Settings.jackpotSpawnChance.Success");
        this.COMMANDS_SETTINGS_MAXMISSED                    = this.Messages.getString("Commands.Settings.maxMissed");
        this.COMMANDS_SETTINGS_MAXMISSED_SUCCESS            = this.Messages.getString("Commands.Settings.maxMissed.Success");
        this.COMMANDS_SETTINGS_SCOREPOINTS                  = this.Messages.getString("Commands.Settings.scorePoints");
        this.COMMANDS_SETTINGS_SCOREPOINTS_SUCCESS          = this.Messages.getString("Commands.Settings.scorePoints.Success");
        this.COMMANDS_SETTINGS_SPAWNRATE                    = this.Messages.getString("Commands.Settings.spawnRate");
        this.COMMANDS_SETTINGS_SPAWNRATE_SUCCESS            = this.Messages.getString("Commands.Settings.spawnRate.Success");
        this.COMMANDS_SETTINGS_SPAWNCHANCE                  = this.Messages.getString("Commands.Settings.spawnChance");
        this.COMMANDS_SETTINGS_SPAWNCHANCE_SUCCESS          = this.Messages.getString("Commands.Settings.spawnChance.Success");
        this.COMMANDS_SETTINGS_MOLESPEED                    = this.Messages.getString("Commands.Settings.Molespeed");
        this.COMMANDS_SETTINGS_MOLESPEED_SUCCESS            = this.Messages.getString("Commands.Settings.Molespeed.Success");
        this.COMMANDS_SETTINGS_DIFFICULTYSCALE              = this.Messages.getString("Commands.Settings.difficultyScale");
        this.COMMANDS_SETTINGS_DIFFICULTYSCALE_SUCCESS      = this.Messages.getString("Commands.Settings.difficultyScale.Success");
        this.COMMANDS_SETTINGS_DIFFICULTYINCREASE           = this.Messages.getString("Commands.Settings.difficultyIncrease");
        this.COMMANDS_SETTINGS_DIFFICULTYINCREASE_SUCCESS   = this.Messages.getString("Commands.Settings.difficultyIncrease.Success");
        this.COMMANDS_RELOAD                                = this.Messages.getString("Commands.Reload");
        this.COMMANDS_RELOAD_SUCCESS                        = this.Messages.getString("Commands.Reload.Success");
        this.COMMANDS_ONGRID_FAIL_PLAYER                    = this.Messages.getString("Commands.onGrid.Fail.Player");
        this.COMMANDS_ONGRID_FAIL_CONSOLE                   = this.Messages.getString("Commands.onGrid.Fail.Console");
        this.ECON_INVALIDECONOMY                            = this.Messages.getString("Econ.invalidEconomy");
        this.ECON_INVALIDVAULT                              = this.Messages.getString("Econ.invalidVault");
        this.ECON_INVALIDOBJECTIVE                          = this.Messages.getString("Econ.invalidObjective");
        this.GRID_EMPTYGRID                                 = this.Messages.getString("Grid.emptyGrid");
        this.GRID_INVALIDSIZE                               = this.Messages.getString("Grid.invalidSize");
        this.MANAGER_LOADINGGAMES                           = this.Messages.getString("Manager.loadingGames");
        this.MANAGER_NOGAMESFOUND                           = this.Messages.getString("Manager.noGamesFound");
        this.MANAGER_NAMEEXISTS                             = this.Messages.getString("Manager.nameExists");
        this.MANAGER_TICKETUSE_GAMENOTFOUND                 = this.Messages.getString("Manager.ticketUse.gameNotFound");
        this.MANAGER_TICKETUSE_SUCCESS                      = this.Messages.getString("Manager.ticketUse.Success");
        this.MANAGER_TICKETUSE_NOCOOLDOWN                   = this.Messages.getString("Manager.ticketUse.noCooldown");
        this.MANAGER_TICKETUSE_NOPERMISSION                 = this.Messages.getString("Manager.ticketUse.noPremission");
        this.GAME_INVALIDECONOMY                            = this.Messages.getString("Game.invalidEconomy");
        this.GAME_STOP_REWARD_SING                          = this.Messages.getString("Game.Stop.Reward.Sing");
        this.GAME_STOP_REWARD_PLUR                          = this.Messages.getString("Game.Stop.Reward.Plur");
        this.GAME_STOP_REWARD_NONE                          = this.Messages.getString("Game.Stop.Reward.None");
        this.GAME_START_FULLINVENTORY                       = this.Messages.getString("Game.Start.fullInventory");
        this.GAME_INVALIDCOOLDOWN                           = this.Messages.getString("Game.invalidCooldown");
        this.GAME_ACTIONBAR_ERROR                           = this.Messages.getString("Game.Actionbar.Error");
        this.GAME_ACTIONBAR_GAMEOVER                        = this.Messages.getString("Game.Actionbar.moleGameOver");
        this.GAME_ACTIONBAR_MOLEGAMEOVER                    = this.Messages.getString("Game.Actionbar.gameOver");
        this.GAME_MOLEMISSED                                = this.Messages.getString("Game.moleMissed");
        this.GAME_LOADSUCCESS                               = this.Messages.getString("Game.loadSuccess");
        this.GAME_CONFIG_FIRSTNOTE                          = this.Messages.getString("Game.Config.firstNote");
        this.GAME_CONFIG_SECONDNOTE                         = this.Messages.getString("Game.Config.secondNote");
        this.GAME_CONFIG_THIRDNOTE                          = this.Messages.getString("Game.Config.thirdNote");
        this.GAME_CONFIG_NAME                               = this.Messages.getString("Game.Config.Name");
        this.GAME_CONFIG_DIRECTION                          = this.Messages.getString("Game.Config.Direction");
        this.GAME_CONFIG_JACKPOT                            = this.Messages.getString("Game.Config.Jackpot");
        this.GAME_CONFIG_JACKPOTSPAWN                       = this.Messages.getString("Game.Config.jackpotSpawn");
        this.GAME_CONFIG_GAMELOST                           = this.Messages.getString("Game.Config.gameLost");
        this.GAME_CONFIG_POINTSPERKILL                      = this.Messages.getString("Game.Config.pointsPerKill");
        this.GAME_CONFIG_SPAWNRATE                          = this.Messages.getString("Game.Config.spawnRate");
        this.GAME_CONFIG_SPAWNCHANCE                        = this.Messages.getString("Game.Config.spawnChance");
        this.GAME_CONFIG_MOLESPEED                          = this.Messages.getString("Game.Config.Molespeed");
        this.GAME_CONFIG_DIFFICULTYSCALE                    = this.Messages.getString("Game.Config.difficultyScale");
        this.GAME_CONFIG_DIFFICULTYINCREASE                 = this.Messages.getString("Game.Config.difficultyIncrease");
        this.GAME_CONFIG_COOLDOWN                           = this.Messages.getString("Game.Config.Cooldown");
        this.GAME_CONFIG_ENDMESSAGE                         = this.Messages.getString("Game.Config.endMessage");
    }
}
