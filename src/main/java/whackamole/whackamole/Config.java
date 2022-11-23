package whackamole.whackamole;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public final class Config {

    private static Config Instance;
    public YMLFile configFile;
    public String PREFIX;
    public List<?> MOLEBLOCK;
    public List<?> SUBBLOCK;
    public String NO_PERM;
    public String PERM_ALL;
    public String PERM_CREATE;
    public String PERM_REMOVE;
    public String PERM_SETTINGS;
    public File gamesData = new File("./plugins/WhackaMole/Games/");

    private Config(Plugin main, String path) {
        main.saveResource("config.yml", false);
        this.configFile = new YMLFile(path);
        this.setup();
    }
    private Config() {}

    public void setup() {
        this.PREFIX         = ChatColor.translateAlternateColorCodes('&', "&e&l[&6&lWAM&e&l] &f> ");
        this.MOLEBLOCK      = this.configFile.getList("Blocklist");
        this.SUBBLOCK       = this.configFile.getList("Sub-List");
        this.NO_PERM        = this.PREFIX + this.configFile.getString("No Permission");
        this.PERM_ALL       = "WAM." + this.configFile.getString("Every permission");
        this.PERM_CREATE    = "WAM." + this.configFile.getString("Commands.Create");
        this.PERM_REMOVE    = "WAM." + this.configFile.getString("Commands.Remove");
        this.PERM_SETTINGS  = "WAM." + this.configFile.getString("Commands.Settings");
    }

    public static Config getInstance(Plugin main, String config) {
        if (Config.Instance == null) {
            Config.Instance = new Config(main, config);
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
}

class YMLFile {
    public FileConfiguration FileConfig;
    public File file;
    private Logger logger = Logger.getInstance();

    public YMLFile(File folder, String child) throws FileNotFoundException {
        if (!folder.isDirectory()) throw new FileNotFoundException("Did not pass a Folder file: %s".formatted(folder.getName()));
        this.FileConfig = new YamlConfiguration();
        this.file = new File(folder, child);
        this.load();
    }
    public YMLFile(String folder, String child) {
        this.FileConfig = new YamlConfiguration();
        this.file = new File(folder, child);
        this.load();
    }
    public YMLFile(String path) {
        this.FileConfig = new YamlConfiguration();
        this.file = new File(path);
        this.load();
    }
    public YMLFile(File file) {
        this.FileConfig = new YamlConfiguration();
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
            this.logger.success("Loaded YAML file: %s".formatted(this.file.getName()));
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