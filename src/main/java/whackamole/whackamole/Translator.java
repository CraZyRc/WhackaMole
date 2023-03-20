package whackamole.whackamole;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bukkit.entity.Player;

public enum Translator {
        MAIN_OLDVERSION                                     ("Main.oldVersion")
    ,   CONFIG_OLDVERSION                                   ("Config.oldVersion", String.class)
    ,   UPDATEFAIL                                          ("Update.updateFail", String.class)
    ,   LOGGER_WARNING                                      ("Logger.Warning", String.class)
    ,   LOGGER_ERROR                                        ("Logger.Error", String.class)
    ,   YML_NOTFOUNDEXCEPTION                               ("YML.notFoundException", YMLFile.class)
    ,   YML_SAVEDFILE                                       ("YML.savedFile", YMLFile.class)
    ,   YML_DELETEDFILE                                     ("YML.deletedFile", YMLFile.class)
    ,   YML_CREATEFILE                                      ("YML.createFile", YMLFile.class)
    ,   YML_CREATEFAIL                                      ("YML.createFail", YMLFile.class)
    ,   CONFIG_TICKET_NAME                                  ("Config.Ticket.Name")
    ,   CONFIG_TICKET_LORE1                                 ("Config.Ticket.Lore1")
    ,   CONFIG_TICKET_LORE2                                 ("Config.Ticket.Lore2")
    ,   CONFIG_TICKET_LORE3                                 ("Config.Ticket.Lore3")
    ,   MANAGER_LOADINGGAMES                                ("Manager.loadingGames")
    ,   MANAGER_NOGAMESFOUND                                ("Manager.noGamesFound")
    ,   MANAGER_NAMEEXISTS                                  ("Manager.nameExists", String.class)
    ,   MANAGER_ALREADYACTIVE                               ("Manager.alreadyActive")
    ,   MANAGER_TICKETUSE_GAMENOTFOUND                      ("Manager.ticketUse.gameNotFound")
    ,   MANAGER_TICKETUSE_SUCCESS                           ("Manager.ticketUse.Success")
    ,   MANAGER_TICKETUSE_NOCOOLDOWN                        ("Manager.ticketUse.noCooldown")
    ,   MANAGER_TICKETUSE_NOPERMISSION                      ("Manager.ticketUse.noPremission")
    ,   GRID_EMPTYGRID                                      ("Grid.emptyGrid")
    ,   GRID_INVALIDSIZE                                    ("Grid.invalidSize")
    ,   GAME_INVALIDECONOMY                                 ("Game.invalidEconomy")
    ,   GAME_STOP_REWARD_SING                               ("Game.Stop.Reward.Sing", Game.class)
    ,   GAME_STOP_REWARD_PLUR                               ("Game.Stop.Reward.Plur", Game.class)
    ,   GAME_STOP_REWARD_NONE                               ("Game.Stop.Reward.None")
    ,   GAME_START_FULLINVENTORY                            ("Game.Start.fullInventory")
    ,   GAME_INVALIDCOOLDOWN                                ("Game.invalidCooldown")
    ,   GAME_ACTIONBAR_ERROR                                ("Game.Actionbar.Error")
    ,   GAME_ACTIONBAR_MOLEGAMEOVER                         ("Game.Actionbar.moleGameOver", Game.class)
    ,   GAME_ACTIONBAR_GAMEOVER                             ("Game.Actionbar.gameOver")
    ,   GAME_MOLEMISSED                                     ("Game.moleMissed", Game.class)
    ,   GAME_LOADSUCCESS                                    ("Game.loadSuccess", Game.class)
    ,   GAME_CONFIG_FIRSTNOTE                               ("Game.Config.firstNote")
    ,   GAME_CONFIG_SECONDNOTE                              ("Game.Config.secondNote")
    ,   GAME_CONFIG_THIRDNOTE                               ("Game.Config.thirdNote")
    ,   GAME_CONFIG_NAME                                    ("Game.Config.Name")
    ,   GAME_CONFIG_DIRECTION                               ("Game.Config.Direction")
    ,   GAME_CONFIG_JACKPOT                                 ("Game.Config.Jackpot")
    ,   GAME_CONFIG_JACKPOTSPAWN                            ("Game.Config.jackpotSpawn")
    ,   GAME_CONFIG_GAMELOST                                ("Game.Config.gameLost")
    ,   GAME_CONFIG_POINTSPERKILL                           ("Game.Config.pointsPerKill")
    ,   GAME_CONFIG_SPAWNRATE                               ("Game.Config.spawnRate")
    ,   GAME_CONFIG_SPAWNCHANCE                             ("Game.Config.spawnChance")
    ,   GAME_CONFIG_MOLESPEED                               ("Game.Config.Molespeed")
    ,   GAME_CONFIG_DIFFICULTYSCALE                         ("Game.Config.difficultyScale")
    ,   GAME_CONFIG_DIFFICULTYINCREASE                      ("Game.Config.difficultyIncrease")
    ,   GAME_CONFIG_COOLDOWN                                ("Game.Config.Cooldown")
    ,   GAME_CONFIG_ENDMESSAGE                              ("Game.Config.endMessage")
    ,   COMMANDS_TIPS_NAME                                  ("Commands.Tips.Name")
    ,   COMMANDS_TIPS_DIRECTION                             ("Commands.Tips.Direction")
    ,   COMMANDS_TIPS_JACKPOT                               ("Commands.Tips.Jackpot")
    ,   COMMANDS_TIPS_JACKPOTSPAWNS                         ("Commands.Tips.jackpotSpawns")
    ,   COMMANDS_TIPS_MAXMISSED                             ("Commands.Tips.maxMissed")
    ,   COMMANDS_TIPS_HITPOINTS                             ("Commands.Tips.hitPoints")
    ,   COMMANDS_TIPS_INTERVAL                              ("Commands.Tips.Interval")
    ,   COMMANDS_TIPS_SPAWNCHANCE                           ("Commands.Tips.spawnChance")
    ,   COMMANDS_TIPS_MOLESPEED                             ("Commands.Tips.moleSpeed")
    ,   COMMANDS_TIPS_DIFFICULTYSCALE                       ("Commands.Tips.difficultyScale")
    ,   COMMANDS_TIPS_DIFFICULTYINCREASE                    ("Commands.Tips.difficultyIncrease")
    ,   COMMANDS_TIPS_COOLDOWN                              ("Commands.Tips.Cooldown")
    ,   COMMANDS_CREATE                                     ("Commands.Create")
    ,   COMMANDS_CREATE_SUCCESS                             ("Commands.Create.Success")
    ,   COMMANDS_REMOVE                                     ("Commands.Remove")
    ,   COMMANDS_REMOVE_CONFIRM                             ("Commands.Remove.Confirm")
    ,   COMMANDS_REMOVE_SUCCESS                             ("Commands.Remove.Success")
    ,   COMMANDS_BUY                                        ("Commands.Buy")
    ,   COMMANDS_BUY_FULLINVENTORY                          ("Commands.Buy.fullInventory")
    ,   COMMANDS_BUY_CONFIRMATION                           ("Commands.Buy.Confirmation")
    ,   COMMANDS_BUY_LOWECONOMY                             ("Commands.Buy.lowEconomy")
    ,   COMMANDS_BUY_ECONOMYERROR_PLAYER                    ("Commands.Buy.economyError.Player")
    ,   COMMANDS_BUY_ECONOMYERROR_CONSOLE                   ("Commands.Buy.economyError.Console", Player.class)
    ,   COMMANDS_BUY_SUCCESS                                ("Commands.Buy.Success")
    ,   COMMANDS_SETTINGS                                   ("Commands.Settings")
    ,   COMMANDS_SETTINGS_DIRECTION                         ("Commands.Settings.Direction")
    ,   COMMANDS_SETTINGS_DIRECTION_SUCCESS                 ("Commands.Settings.Directions.Success")
    ,   COMMANDS_SETTINGS_JACKPOT                           ("Commands.Settings.Jackpot")
    ,   COMMANDS_SETTINGS_JACKPOT_SUCCESS                   ("Commands.Settings.Jackpot.Success")
    ,   COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE                ("Commands.Settings.jackpotSpawnChance")
    ,   COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_SUCCESS        ("Commands.Settings.jackpotSpawnChance.Success")
    ,   COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE  ("Commands.Settings.jackpotSpawnChance.Error.Console", Player.class)
    ,   COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER   ("Commands.Settings.jackpotSpawnChance.Error.Player")
    ,   COMMANDS_SETTINGS_MAXMISSED                         ("Commands.Settings.maxMissed")
    ,   COMMANDS_SETTINGS_MAXMISSED_SUCCESS                 ("Commands.Settings.maxMissed.Success")
    ,   COMMANDS_SETTINGS_SCOREPOINTS                       ("Commands.Settings.scorePoints")
    ,   COMMANDS_SETTINGS_SCOREPOINTS_SUCCESS               ("Commands.Settings.scorePoints.Success")
    ,   COMMANDS_SETTINGS_SPAWNRATE                         ("Commands.Settings.spawnRate")
    ,   COMMANDS_SETTINGS_SPAWNRATE_SUCCESS                 ("Commands.Settings.spawnRate.Success")
    ,   COMMANDS_SETTINGS_SPAWNCHANCE                       ("Commands.Settings.spawnChance")
    ,   COMMANDS_SETTINGS_SPAWNCHANCE_SUCCESS               ("Commands.Settings.spawnChance.Success")
    ,   COMMANDS_SETTINGS_MOLESPEED                         ("Commands.Settings.Molespeed")
    ,   COMMANDS_SETTINGS_MOLESPEED_SUCCESS                 ("Commands.Settings.Molespeed.Success")
    ,   COMMANDS_SETTINGS_DIFFICULTYSCALE                   ("Commands.Settings.difficultyScale")
    ,   COMMANDS_SETTINGS_DIFFICULTYSCALE_SUCCESS           ("Commands.Settings.difficultyScale.Success")
    ,   COMMANDS_SETTINGS_DIFFICULTYINCREASE                ("Commands.Settings.difficultyIncrease")
    ,   COMMANDS_SETTINGS_DIFFICULTYINCREASE_SUCCESS        ("Commands.Settings.difficultyIncrease.Success")
    ,   COMMANDS_SETTINGS_COOLDOWN                          ("Commands.Settings.Cooldown")
    ,   COMMANDS_SETTINGS_COOLDOWN_SUCCESS                  ("Commands.Settings.Cooldown.Success")
    ,   COMMANDS_RELOAD                                     ("Commands.Reload")
    ,   COMMANDS_RELOAD_SUCCESS                             ("Commands.Reload.Success")
    ,   COMMANDS_ONGRID_FAIL_PLAYER                         ("Commands.onGrid.Fail.Player")
    ,   COMMANDS_ONGRID_FAIL_CONSOLE                        ("Commands.onGrid.Fail.Console", Player.class)
    ,   ECON_INVALIDECONOMY                                 ("Econ.invalidEconomy")
    ,   ECON_INVALIDVAULT                                   ("Econ.invalidVault")
    ,   ECON_INVALIDOBJECTIVE                               ("Econ.invalidObjective");

    public String key;
    public String value = "";
    public String formattedValue;
    private Translator(String key) {
        this.key = key;
        this.requiredTypes = new Object[0];
        LookupTranslation();
    }
    
    public Object[] requiredTypes;
    private Translator(String key, Object... requiredTypes) {
        this(key);
        this.requiredTypes = requiredTypes;
    }

    private void LookupTranslation() {
        try {
            ResourceBundle resource = ResourceBundle.getBundle("Lang", Config.AppConfig.Language);
            this.value = resource.getString(this.key);
        } catch(Exception e) {}
    }

    public String Format() {
        return Format(this, new Object[0]);
    }
    public String Format(Object... replacements) {
        return Translator.Format(this, replacements); 
    }
    public static String Format(Translator type) {
        return Format(type, new Object[0]);
    }
    public static String Format(Translator type, Object... replacements) {
        if(!typesToString(type.requiredTypes).equals(typesToString(replacements))) {
            Logger.error("Translator format failed for " + type.key + ": " + typesToString(type.requiredTypes) + " | "+ typesToString(replacements));
        }


        type.formattedValue = type.value;
        // * Config formatting
        type.configFormat();

        List<String> stringReplacements = new ArrayList<String>();
        for(Object replacement : replacements) {
            if(replacement instanceof Game)     type.Format((Game) replacement);
            if(replacement instanceof YMLFile)  type.Format((YMLFile) replacement);
            if(replacement instanceof Player)   type.Format((Player) replacement);
            if(replacement instanceof String)   stringReplacements.add((String) replacement);
        }
        for(String item : stringReplacements) {
            type.Format(item);
        }
        return Config.Color(type.formattedValue);
    }


    private void Format(Game game) {
        this.formattedValue = this.formattedValue.replace("{missedMoles}", String.valueOf(game.getRunning().missed));
        this.formattedValue = this.formattedValue.replace("{maxMissed}", String.valueOf(game.getSettings().maxMissed));
        this.formattedValue = this.formattedValue.replace("{Score}", String.valueOf(game.getRunning().score));
        this.formattedValue = this.formattedValue.replace("{gameName}", String.valueOf(game.name));
    }
    private void Format(Player player) {
        this.formattedValue = this.formattedValue.replace("{Player}", String.valueOf(player.getName()));
    }
    private void Format(YMLFile file) {
        this.formattedValue = this.formattedValue.replace("{File}", String.valueOf(file.file.getName()));
    }
    private void Format(String item) {
        this.formattedValue = this.formattedValue.replaceFirst("\\{.*?\\}", item);
    }   
    private void configFormat() {
        this.formattedValue = this.formattedValue
        .replace("{configVersion}",     Config.AppConfig.configVersion)
        .replace("{Symbol}",            Config.Currency.SYMBOL)
        .replace("{ticketPrice}",       String.valueOf(Config.Currency.TICKETPRICE))
        .replace("{currencyPlur}",      Config.Currency.CURRENCY_PLUR)
        .replace("{currencySing}",      Config.Currency.CURRENCY_SING)
        .replace("{commandSettings}",  "/wam " + COMMANDS_SETTINGS)
        .replace("{commandRemove}",    "/wam " + COMMANDS_REMOVE)
        .replace("{commandReload}",    "/wam " + COMMANDS_RELOAD)
        .replace("{commandBuy}",       "/wam " + COMMANDS_BUY);
    }
    public String toString() {
        return Config.Color(this.value);
    }


    private static String typesToString(Object[] types) {
        List<String> out = new ArrayList<String>();
        for(Object i : types) {
            String name = "";
            if(i == null) name = "null";
            else if(i instanceof Class) name = ((Class<?>) i).getName();
            else name = i.getClass().getName();
            out.add(name.substring(name.lastIndexOf('.') + 1));
        }
        if(types.length == 0) return "";
        else return " '" + String.join("', '", out) + "' ";
    }

    public static void onLoad() {
        for(Translator item : values()) {
            item.LookupTranslation();
        }
    }
}
