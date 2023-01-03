package whackamole.whackamole;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.*;
import java.lang.reflect.Field;
import org.bukkit.*;
import java.util.*;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public final class Config {

    private static Config Instance;
    private static Logger logger = Logger.getInstance();
    private Translator translator;
    private String MOLE_SKIN;
    private String JACKPOT_SKIN;
    public String language;
    public String configVersion;
    public String newConfigVersion = "1.3";
    public YMLFile configFile;
    public String PREFIX;
    public String CURRENCY_SING;
    public String CURRENCY_PLUR;
    public String SYMBOL;
    public int TICKETPRICE;
    public String ACTIONTEXT;
    public String HAMMERNAME;
    public Sound HITSOUND;
    public Sound MISSSOUND;
    public List<?> MOLEBLOCK;
    public String PERM_TICKET_USE;
    public String PERM_BUY;
    public String PERM_RELOAD;
    public String PERM_CREATE;
    public String PERM_REMOVE;
    public String PERM_SETTINGS;
    public String PERM_PLAY;
    public String ECONOMY;
    public String OBJECTIVE;
    public Double FiELD_MARGIN_X;
    public Double FiELD_MARGIN_Y;
    public int FIELD_MAX_SIZE;
    public File gamesData = new File("./plugins/WhackaMole/Games/");

    public ItemStack PLAYER_AXE; 
    public ItemStack MOLE_SKULL; 
    public ItemStack JACKPOT_SKULL;
    public ItemStack TICKET;

    private Config(Plugin main) {
        if (!new File("./plugins/WhackaMole/config.yml").exists()) main.saveResource("config.yml", false);
        if (!gamesData.exists()) gamesData.mkdirs();
        this.configFile = new YMLFile("./plugins/WhackaMole/config.yml");
        this.setup();


    }
    private Config() {}

    public void onEnable() {
        this.translator = Translator.getInstance();
        this.translator.configLoad(this.language);
        this.loadNBTData();
        if (!this.configVersion.equals(newConfigVersion)) {
            this.logger.warning(this.translator.Format(this.translator.CONFIG_OLDVERSION));
        }
    }

    public void setup() {
        this.PREFIX             = ChatColor.translateAlternateColorCodes('&', "&e&l[&6&lWAM&e&l] &f> ");
        this.language           = this.configFile.getString("Language");
        this.configVersion      = this.configFile.getString("Config Version");
        this.ECONOMY            = this.configFile.getString("Economy");
        this.OBJECTIVE          = this.configFile.getString("Scoreboard Objective");
        this.CURRENCY_SING      = this.configFile.getString("Singular Currency");
        this.CURRENCY_PLUR      = this.configFile.getString("Plural Currency");
        this.SYMBOL             = this.configFile.getString("Currency Symbol");
        this.TICKETPRICE        = this.configFile.getInt("Ticket Price");
        this.HITSOUND           = this.configFile.getSound("HitSound");
        this.MISSSOUND          = this.configFile.getSound("MissedSound");
        this.MOLEBLOCK          = this.configFile.getList("Blocklist");
        this.ACTIONTEXT         = this.configFile.getString("Actionbar Message");
        this.MOLE_SKIN          = this.configFile.getString("Mole Skin");
        this.JACKPOT_SKIN       = this.configFile.getString("Jackpot Skin");
        this.HAMMERNAME         = Translator.Color(this.configFile.getString("Hammer Name"));
        this.PERM_TICKET_USE    = "WAM." + this.configFile.getString("Commands.Use Reset Ticket");
        this.PERM_BUY           = "WAM." + this.configFile.getString("Commands.Buy");
        this.PERM_RELOAD        = "WAM." + this.configFile.getString("Commands.Reload");
        this.PERM_CREATE        = "WAM." + this.configFile.getString("Commands.Create");
        this.PERM_REMOVE        = "WAM." + this.configFile.getString("Commands.Remove");
        this.PERM_PLAY          = "WAM." + this.configFile.getString("Commands.Play");
        this.PERM_SETTINGS      = "WAM." + this.configFile.getString("Commands.Settings");

        this.FIELD_MAX_SIZE     = this.configFile.getInt("Max playfield");
        this.FiELD_MARGIN_X     = this.configFile.getDouble("Field extension.width");
        this.FiELD_MARGIN_Y     = this.configFile.getDouble("Field extension.height");
    }



    public static Config getInstance(Plugin main) {
        if (Config.Instance == null) {
            Config.Instance = new Config(main);
            return Config.Instance;
        }
        return Config.Instance;
    }

    public static Config getInstance() {
        if (Config.Instance == null) {
            Config.Instance = new Config();
            return Config.Instance;
        }
        return Config.Instance;
    }

    public static Config reload(Main main) {
        Config.Instance = null;
        return Config.getInstance(main);
    }

    private void loadNBTData() {
        ItemStack axe = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        ((Damageable) axeMeta).setDamage(31);
        axeMeta.setUnbreakable(true);
        axeMeta.addEnchant(Enchantment.LURE, 1, true);
        axeMeta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE);
        axeMeta.setDisplayName(this.HAMMERNAME);
        axe.setItemMeta(axeMeta);

        ItemStack ticket = new ItemStack(Material.MAP);
        ItemMeta ticketInfo = ticket.getItemMeta();
        ticketInfo.addEnchant(Enchantment.LURE, 1, true);
        ticketInfo.setDisplayName(this.translator.CONFIG_TICKET_NAME);
        ticketInfo.setLore(
                Arrays.asList(
                        this.translator.CONFIG_TICKET_LORE1,
                        this.translator.CONFIG_TICKET_LORE2,
                        this.translator.CONFIG_TICKET_LORE3));
        ticketInfo.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        ticket.setItemMeta(ticketInfo);

        this.PLAYER_AXE = axe;
        this.TICKET = ticket;
        this.MOLE_SKULL = this.getSkull(this.MOLE_SKIN);
        this.JACKPOT_SKULL = this.getSkull(this.JACKPOT_SKIN);


    }

    private ItemStack getSkull(String url) {
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

class YMLFile {
    public FileConfiguration FileConfig = new YamlConfiguration();
    public File file;
    private final Logger logger = Logger.getInstance();
    private Translator translator = Translator.getInstance();

    public YMLFile(File folder, String child) throws FileNotFoundException {
        if (!folder.isDirectory()) throw new FileNotFoundException(this.translator.Format(this.translator.YML_NOTFOUNDEXCEPTION, folder));
        this.file = new File(folder, child);
        this.load();
    }

    public YMLFile(String path) {
        this.file = new File(path);
        this.load();
    }
    public YMLFile(File file) {
        this.file = file;
        this.load();
    }

    public String getString(String path) {
        return this.FileConfig.getString(path);
    }
    public boolean getBoolean(String path) {
        return this.FileConfig.getBoolean(path);
    }
    public int getInt(String path) {
        return this.FileConfig.getInt(path);
    }
    public double getDouble(String path) {
        return this.FileConfig.getDouble(path);
    }
    public List<?> getList(String path) {
        return this.FileConfig.getList(path);
    }
    public Sound getSound(String path) { return Sound.valueOf(this.FileConfig.getString(path));}


    public void set (String path, Object value) {
        this.FileConfig.set(path, value);
    }

    public void save() {
        try {
            this.FileConfig.save(this.file);
            this.logger.success(this.translator.Format(this.translator.YML_SAVEDFILE, this.file));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void load() {
        try {
            this.FileConfig.load(this.file);
        } catch (Exception e) {
            this.createFile();
        }
    }
    public void remove() {
        try {
            this.file.delete();
            this.logger.success(this.translator.Format(this.translator.YML_DELETEDFILE, this.file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createFile() {
        try {
            this.logger.success(this.translator.Format(this.translator.YML_CREATEFILE, this.file));
            if (this.file.getParentFile().mkdirs()) {
                this.load();
            }
        } catch (Exception e) {
            this.logger.error(this.translator.Format(this.translator.YML_CREATEFAIL, this.file));
            e.printStackTrace();
        }
    }

}