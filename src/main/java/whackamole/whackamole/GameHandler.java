package whackamole.whackamole;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GameHandler implements Listener {

    private boolean debug = false;
    private Logger logger = Logger.getInstance();
    private Config config = Config.getInstance();

    private File gameFile;
    private FileConfiguration gameConfig = new YamlConfiguration();    
    boolean gamesLoaded = false;

    public String gameName;
    private World world;
    public Grid grid;
    public Boolean cashHats = true;
    public Integer Interval = 20;
    public Integer pointsPerKill = 1;
    private World world;

    public GameHandler(String ConfigName) {
        this.gameName = ConfigName;
        this.createGameFile();
        this.getValues();
    }

    public GameHandler(String gameName, Grid grid) {
        this.gameName = gameName;
        this.grid = grid;
        // is een nieuwe game, sla settings en grid op in een nieuwe file
        this.createGameFile();
        this.setValues();
    }

    public boolean onGrid(Player player) {
        return this.grid.onGrid(player);
    }

    public boolean onGrid(Location loc) {
        return this.grid.onGrid(loc);
    }

    public void toggleArmorStands() {
        this.debug = !this.debug;
        if (this.debug) {
            this.grid.spawnArmorStands();
        } else {
            this.grid.removeArmorStands();
        }

    }

    // create gamefile
    public void createGameFile() {
        gameFile = new File(this.config.gamesData + this.gameName + ".yml");
        if (!gameFile.exists()) {
            this.logger.info("Creating Gamefiles...");
            gameFile.getParentFile().mkdirs();
            try {
                this.gameConfig.save(this.gameFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            gameConfig.load(gameFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            this.logger.error("GameFile failed to load");
            return;
        }
        this.logger.success("...Gamefile succesfully created");

    }


    public void setValues() {

        try {
            this.gameConfig.options().setHeader(Arrays.asList(
                    "###########################################################",
                    " ^------------------------------------------------------^ #",
                    " |                       GameFile                       | #",
                    " <------------------------------------------------------> #",
                    "###########################################################",
                    "NOTE: you can edit the Properties to change the game rules",
                    "NOTE: Do not touch the Field Data!"));
            this.gameConfig.set("Properties.Name", this.gameName);
            this.gameConfig.set("Properties.World", this.grid.world.getName());
            this.gameConfig.set("Properties.CashHats", this.cashHats);
            this.gameConfig.set("Properties.Interval", this.Interval);
            this.gameConfig.set("Properties.Points per Kill", this.pointsPerKill);
            this.gameConfig.set("Field Data", this.grid.Serialize());
            this.gameConfig.save(this.gameFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getValues() {
        this.gameName = (String) this.gameConfig.get("Properties.Name");
        this.world = Bukkit.getWorld((String) this.gameConfig.get("Properties.World"));
        this.grid = Grid.Deserialize(this.world, (List<List<Integer>>) this.gameConfig.getList("Field Data"));
        this.cashHats = this.gameConfig.getBoolean("properties." + "CashHats");
        this.Interval = this.gameConfig.getInt("properties." + "Interval");
        this.pointsPerKill = this.gameConfig.getInt("properties." + "Points per Kill");
    }
}
