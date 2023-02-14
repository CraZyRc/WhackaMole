package whackamole.whackamole;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Config {
    private static YMLFile ConfigFile;
    public static boolean Loaded = false;

    public static class AppConfig {
        public static Locale Language = new Locale("en", "US");
        public final static String configVersion = "1.4";
        public final static String configFileName = "config.yml";

        public static String storageFolder,
                PREFIX = ChatColor.translateAlternateColorCodes('&', "&e&l[&6&lWAM&e&l] &f> ");

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
        public static String ACTIONTEXT, HAMMERNAME = "Hammer";

        public static int FIELD_MAX_SIZE;
        public static Double FiELD_MARGIN_X, FiELD_MARGIN_Y;

        public static Sound HITSOUND, MISSSOUND;

        public static List<?> MOLEBLOCK;

        public static ItemStack PLAYER_AXE, TICKET, MOLE_SKULL, JACKPOT_SKULL;

        private static void LoadConfig(YMLFile configFile) {
            ACTIONTEXT = configFile.getString("Actionbar Message");
            HAMMERNAME = Color(configFile.getString("Hammer Name"));

            FIELD_MAX_SIZE = configFile.getInt("Max playfield");
            FiELD_MARGIN_X = configFile.getDouble("Field extension.width");
            FiELD_MARGIN_Y = configFile.getDouble("Field extension.height");

            HITSOUND = configFile.getSound("HitSound");
            MISSSOUND = configFile.getSound("MissedSound");

            MOLEBLOCK = configFile.getList("Blocklist");

            MOLE_SKULL = getSkull(configFile.getString("Mole Skin"));
            JACKPOT_SKULL = getSkull(configFile.getString("Jackpot Skin"));

            PLAYER_AXE = new ItemStack(Material.GOLDEN_AXE);
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
        public static String PERM_TICKET_USE, PERM_BUY, PERM_RELOAD, PERM_CREATE, PERM_REMOVE, PERM_SETTINGS, PERM_PLAY;

        private static void LoadConfig(YMLFile configFile) {
            PERM_TICKET_USE = "WAM." + configFile.getString("Commands.Use Reset Ticket");
            PERM_BUY = "WAM." + configFile.getString("Commands.Buy");
            PERM_RELOAD = "WAM." + configFile.getString("Commands.Reload");
            PERM_CREATE = "WAM." + configFile.getString("Commands.Create");
            PERM_REMOVE = "WAM." + configFile.getString("Commands.Remove");
            PERM_PLAY = "WAM." + configFile.getString("Commands.Play");
            PERM_SETTINGS = "WAM." + configFile.getString("Commands.Settings");
        }
    }

    private Config() {
    }

    public static void onLoad(Plugin Main) {
        AppConfig.storageFolder = Main.getDataFolder().getAbsolutePath();
        try {
            ConfigFile = new YMLFile(AppConfig.storageFolder, AppConfig.configFileName);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

        if (ConfigFile.created) {
            Main.saveResource("config.yml", true);
            Logger.info(Translator.YML_CREATEFILE.Format(ConfigFile));
        }

        if (!AppConfig.configVersion.equals(ConfigFile.getString("Config Version"))) {
            Logger.warning(Translator.CONFIG_OLDVERSION.Format(ConfigFile.getString("Config Version")));
        }

        AppConfig.LoadConfig(ConfigFile);
        Currency.LoadConfig(ConfigFile);
        Game.LoadConfig(ConfigFile);
        Permissions.LoadConfig(ConfigFile);

        Loaded = true;
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

    private static ItemStack getSkull(String url) {
        ItemStack moleHead = new ItemStack(Material.PLAYER_HEAD);
        if (url.isEmpty())
            return moleHead;
        SkullMeta moleMeta = (SkullMeta) moleHead.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", url));
        try {
            Field profileField = moleMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(moleMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        moleHead.setItemMeta(moleMeta);
        return moleHead;
    }
}
