package whackamole.whackamole;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Config {
    private static YMLFile ConfigFile;
    public static boolean Loaded = false;

    public static class AppConfig {
        public static Locale Language = new Locale("en", "US");
        public final static String configFileName = "config.yml", configVersion = "1.5";

        public static String storageFolder = "./plugins/WhackaMole"
                ,   PREFIX = ChatColor.translateAlternateColorCodes('&', "&e&l[&6&lWAM&e&l] &f> ");

        private static void LoadConfig(YMLFile configFile) {
            String[] fields = configFile.getString("Language").split("_");
            Language = new Locale(fields[0], fields[1], "");
        }
    }

    public static class Currency {
        public static String CURRENCY_SING = "Dollar", CURRENCY_PLUR = "Dollars", SYMBOL = "$", ECONOMY = "",
                OBJECTIVE = "";

        public static int TICKETPRICE;

        private static void LoadConfig(YMLFile configFile) {
            CURRENCY_SING = configFile.getString("Singular Currency");
            CURRENCY_PLUR = configFile.getString("Plural Currency");
            SYMBOL = configFile.getString("Currency Symbol");
            ECONOMY = configFile.getString("Economy");
            OBJECTIVE = configFile.getString("Scoreboard Objective");
            TICKETPRICE = configFile.getInt("Ticket Price");
        }
    }

    public static class Game {
        public static String ACTIONTEXT, HAMMERNAME = "Hammer", HAMMER_ITEM;
        public static boolean ENABLE_GAMECONFIG;

        public static int FIELD_MAX_SIZE;
        public static Double FiELD_MARGIN_X, FiELD_MARGIN_Y;

        public static Sound HITSOUND, MISSSOUND;

        public static List<?> MOLEBLOCK;

        public static ItemStack PLAYER_AXE, TICKET, MOLE_SKULL, JACKPOT_SKULL;

        private static void LoadConfig(YMLFile configFile) {
            ACTIONTEXT          = configFile.getString("Actionbar Message");
            HAMMER_ITEM         = configFile.getString("Hammer item");
            HAMMERNAME          = DefaultFontInfo.Color(configFile.getString("Hammer Name"));

            FIELD_MAX_SIZE      = configFile.getInt("Max playfield");
            FiELD_MARGIN_X      = configFile.getDouble("Field extension.width");
            FiELD_MARGIN_Y      = configFile.getDouble("Field extension.height");

            ENABLE_GAMECONFIG    = configFile.getBoolean("Game config");
            HITSOUND            = configFile.getSound("HitSound");
            MISSSOUND           = configFile.getSound("MissedSound");

            MOLEBLOCK           = configFile.getList("Blocklist");

            if (Material.getMaterial(HAMMER_ITEM) != null) {
                PLAYER_AXE = new ItemStack(Material.valueOf(HAMMER_ITEM));
                ItemMeta axeMeta = PLAYER_AXE.getItemMeta();
                ((Damageable) axeMeta).setDamage(31);
                axeMeta.setUnbreakable(true);
                axeMeta.addEnchant(Enchantment.LURE, 1, true);
                axeMeta.addItemFlags(
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_UNBREAKABLE);
                axeMeta.setDisplayName(HAMMERNAME);
                PLAYER_AXE.setItemMeta(axeMeta);
            } else
                Logger.error(Translator.CONFIG_INVALIDHAMMERITEM);

            TICKET = new ItemStack(Material.MAP);
            ItemMeta ticketInfo = TICKET.getItemMeta();
            ticketInfo.addEnchant(Enchantment.LURE, 1, true);
            ticketInfo.setDisplayName(Translator.CONFIG_TICKET_NAME.toString());
            ticketInfo.setLore(
                    Arrays.asList(
                            Translator.CONFIG_TICKET_LORE1.toString(),
                            Translator.CONFIG_TICKET_LORE2.toString(),
                            Translator.CONFIG_TICKET_LORE3.toString()));
            ticketInfo.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            TICKET.setItemMeta(ticketInfo);
        }
    }

    public static class Permissions {
        public static String PERM_TICKET_USE, PERM_BUY, PERM_RELOAD, PERM_CREATE, PERM_REMOVE, PERM_SETTINGS, PERM_PLAY, PERM_TOP;

        private static void LoadConfig(YMLFile configFile) {
            PERM_TICKET_USE     = "WAM." + configFile.getString("Play.Use Reset Ticket");
            PERM_PLAY           = "WAM." + configFile.getString("Play.Play");
            PERM_BUY            = "WAM." + configFile.getString("Commands.Buy");
            PERM_SETTINGS       = "WAM." + configFile.getString("Commands.Settings");
            PERM_RELOAD         = "WAM." + configFile.getString("Commands.Reload");
            PERM_CREATE         = "WAM." + configFile.getString("Commands.Create");
            PERM_REMOVE         = "WAM." + configFile.getString("Commands.Remove");
            PERM_TOP            = "WAM." + configFile.getString("Commands.Top");
        }
    }

    private Config() {
    }

    public static void onLoad(Plugin Main) {
        try {
            ConfigFile = new YMLFile(AppConfig.storageFolder, AppConfig.configFileName);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

        if (ConfigFile.created) {
            Main.saveResource("config.yml", true);
            Logger.info(Translator.YML_CREATEFILE.Format(ConfigFile));
            try {
                ConfigFile = new YMLFile(AppConfig.storageFolder, AppConfig.configFileName);
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
        }


        if(Updater.versionCompare(ConfigFile.getString("Config Version"), AppConfig.configVersion)) {
            Logger.warning(Translator.CONFIG_OLDVERSION.Format(ConfigFile.getString("Config Version")));
        }

        AppConfig.LoadConfig(ConfigFile);
        Currency.LoadConfig(ConfigFile);
        Game.LoadConfig(ConfigFile);
        Permissions.LoadConfig(ConfigFile);

        Loaded = true;
    }
}
