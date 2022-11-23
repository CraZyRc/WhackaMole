package whackamole.whackamole;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public final class Config {

    private static Config Instance;
    public FileConfiguration configFile;
    public String PREFIX;
    public List<?> MOLEBLOCK;
    public List<?> SUBBLOCK;
    public String NO_PERM;
    public String PERM_ALL;
    public String PERM_CREATE;
    public String PERM_SETTINGS;
    public File gamesData = new File("./plugins/WhackaMole/Games/");

    private Config() {
    }

    private Config(FileConfiguration config) {
        this.configFile = config;
        this.setup();
    }

    public void setup() {
        this.PREFIX         = ChatColor.translateAlternateColorCodes('&', "&e&l[&6&lWAM&e&l] &f> ");
        this.MOLEBLOCK      = this.configFile.getList("Blocklist");
        this.SUBBLOCK       = this.configFile.getList("Sub-List");
        this.NO_PERM        = this.PREFIX + this.configFile.getString("No Permission");
        this.PERM_ALL       = "WAM." + this.configFile.getString("Every permission");
        this.PERM_CREATE    = "WAM." + this.configFile.getString("Command.Create");
        this.PERM_SETTINGS  = "WAM." + this.configFile.getString("Command.Settings");
    }

    public static Config getInstance(FileConfiguration config) {
        if (Config.Instance == null) {
            Config.Instance = new Config(config);
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
