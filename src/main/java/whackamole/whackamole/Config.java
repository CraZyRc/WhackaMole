package whackamole.whackamole;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

// import net.minecraft.world.level.material.Material;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.meta.SkullMeta;
import java.lang.reflect.Field;
import org.bukkit.*;
import java.util.*;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Config {

    private static Config Instance;
    public YMLFile configFile;
    public String PREFIX;
    public String CURRENCY_SING;
    public String CURRENCY_PLUR;
    public String SYMBOL;
    public int TICKETPRICE;
    public String ACTIONTEXT;
    public String HAMMERNAME;
    public List<?> MOLEBLOCK;
    public List<?> SUBBLOCK;
    public String PERM_TICKET_USE;
    public String PERM_BUY;
    public String PERM_RELOAD;
    public String PERM_CREATE;
    public String PERM_REMOVE;
    public String PERM_SETTINGS;
    public String ECONOMY;
    public String OBJECTIVE;
    public Double FiELD_MARGIN_X;
    public Double FiELD_MARGIN_Y;
    public File gamesData = new File("./plugins/WhackaMole/Games/");

    public ItemStack PLAYER_AXE; 
    public ItemStack MOLE_SKULL; 

    private Config(Plugin main) {
        if(!new File("./plugins/WhackaMole/config.yml").exists())
            main.saveResource("config.yml", false);
        if (!gamesData.exists()) {
            gamesData.mkdirs();
        }

        this.configFile = new YMLFile("./plugins/WhackaMole/config.yml");
        this.setup();
        this.loadNBTData();
    }
    private Config() {}

    public void setup() {
<<<<<<< HEAD
        this.PREFIX         = ChatColor.translateAlternateColorCodes('&', "&e&l[&6&lWAM&e&l] &f> ");
        this.ECONOMY        = this.configFile.getString("Economy");
        this.OBJECTIVE      = this.configFile.getString("Scoreboard Objective");
        this.CURRENCY       = this.configFile.getString("Server Currency");
        this.SYMBOL         = this.configFile.getString("Currency Symbol");
        this.TICKETPRICE    = this.configFile.getInt("Ticket Price");
        this.MOLEBLOCK      = this.configFile.getList("Blocklist");
        this.SUBBLOCK       = this.configFile.getList("Sub-List");
        this.ACTIONTEXT     = this.configFile.getString("Actionbar Message");
        this.HAMMERNAME     = Config.color(this.configFile.getString("Hammer Name"));
        this.NO_PERM        = this.PREFIX + Config.color(this.configFile.getString("No Permission"));
        this.PERM_ALL       = "WAM." + this.configFile.getString("Every Permission");
        this.PERM_RELOAD    = "WAM." + this.configFile.getString("Commands.Reload");
        this.PERM_CREATE    = "WAM." + this.configFile.getString("Commands.Create");
        this.PERM_REMOVE    = "WAM." + this.configFile.getString("Commands.Remove");
        this.PERM_SETTINGS  = "WAM." + this.configFile.getString("Commands.Settings");

        this.FiELD_MARGIN_X = this.configFile.getDouble("Field.Margin.X");
        this.FiELD_MARGIN_Y = this.configFile.getDouble("Field.Margin.Y");
=======
        this.PREFIX             = ChatColor.translateAlternateColorCodes('&', "&e&l[&6&lWAM&e&l] &f> ");
        this.ECONOMY            = this.configFile.getString("Economy");
        this.OBJECTIVE          = this.configFile.getString("Scoreboard Objective");
        this.CURRENCY_SING      = this.configFile.getString("Singular Currency");
        this.CURRENCY_PLUR      = this.configFile.getString("Plural Currency");
        this.SYMBOL             = this.configFile.getString("Currency Symbol");
        this.TICKETPRICE        = this.configFile.getInt("Ticket Price");
        this.MOLEBLOCK          = this.configFile.getList("Blocklist");
        this.SUBBLOCK           = this.configFile.getList("Sub-List");
        this.ACTIONTEXT         = this.configFile.getString("Actionbar Message");
        this.HAMMERNAME         = Config.color(this.configFile.getString("Hammer Name"));
        this.PERM_TICKET_USE    = "WAM." + this.configFile.getString("Commands.Use Reset Ticket");
        this.PERM_BUY           = "WAM." + this.configFile.getString("Commands.Buy");
        this.PERM_RELOAD        = "WAM." + this.configFile.getString("Commands.Reload");
        this.PERM_CREATE        = "WAM." + this.configFile.getString("Commands.Create");
        this.PERM_REMOVE        = "WAM." + this.configFile.getString("Commands.Remove");
        this.PERM_SETTINGS      = "WAM." + this.configFile.getString("Commands.Settings");
>>>>>>> 0d00087 (Commands:)
    }

    public static String color(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&" + c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    
    private void loadNBTData() {
        ItemStack axe = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        ((Damageable) axeMeta).setDamage(31);
        axeMeta.setUnbreakable(true);
        axeMeta.addEnchant(Enchantment.LURE, 1, true);
        axeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        axeMeta.setDisplayName(this.HAMMERNAME);
        axe.setItemMeta(axeMeta);

        this.PLAYER_AXE = axe;
        this.MOLE_SKULL = this.getSkull(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIxMjUwM2Q2MWM0OWY3MDFmZWU4NjdkNzkzZjFkY2M1MjJlNGQ3YzVjNDFhNjhmMjk1MTU3OWYyNGU3Y2IyYSJ9fX0=");
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
}

class YMLFile {
    public FileConfiguration FileConfig = new YamlConfiguration();
    public File file;
    private Logger logger = Logger.getInstance();

    public YMLFile(File folder, String child) throws FileNotFoundException {
        if (!folder.isDirectory()) throw new FileNotFoundException("Did not pass a Folder file: %s".formatted(folder.getName()));
        this.file = new File(folder, child);
        this.load();
    }
    public YMLFile(String folder, String child) {
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

    public Object get(String path) {
        return this.FileConfig.get(path);
    }
    public String getString(String path) {
        return this.FileConfig.getString(path);
    }
    public Boolean getBoolean(String path) {
        return this.FileConfig.getBoolean(path);
    }
    public Integer getInt(String path) {
        return this.FileConfig.getInt(path);
    }
    public Double getDouble(String path) {
        return this.FileConfig.getDouble(path);
    }
    public Long getLong(String path) {
        return this.FileConfig.getLong(path);
    }
    public List<?> getList(String path) {
        return this.FileConfig.getList(path);
    }


    public void set (String path, Object value) {
        this.FileConfig.set(path, value);
    }

    public void save() {
        try {
            this.FileConfig.save(this.file);
            this.logger.success("Saved File: %s".formatted(this.file.getName()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void load() {
        try {
            this.FileConfig.load(this.file);
            this.logger.success("...%s Successfully loaded!".formatted(file.getName()));
        } catch (Exception e) {
            this.createFile();
        }
    }
    public void remove() {
        try {
            String fileName = this.file.getName();
            this.file.delete();
            this.logger.success("deleted file: %s".formatted(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createFile() {
        try {
            this.logger.success("Creating Folder/file: %s".formatted(this.file.getName()));
            if (this.file.getParentFile().mkdirs()) {
                this.load();
            }
        } catch (Exception e) {
            this.logger.error("Failed to create Folder/File: %s".formatted(this.file.getName()));
            e.printStackTrace();
        }
    }

}