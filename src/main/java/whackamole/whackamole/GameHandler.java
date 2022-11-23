package whackamole.whackamole;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class GameHandler implements Listener {

    private boolean debug = false;
    private Logger logger = Logger.getInstance();
    private Config config = Config.getInstance();
    private YMLFile gameConfig;    

    private World world;
    private Grid grid;

    public String gameName;
    public Boolean cashHats = true;
    public Integer Interval = 20;
    public Integer pointsPerKill = 1;
    private World world;

    public GameHandler(YMLFile configFile) throws Exception {
        this.gameConfig = configFile;
        this.loadGame();
    }
    public GameHandler(File configFile) throws Exception {
        this.gameConfig = new YMLFile(configFile);
        this.loadGame();
    }

    public GameHandler(String gameName, Grid grid) throws FileNotFoundException {
        this.gameName = gameName;
        this.gameConfig = new YMLFile(this.config.gamesData, gameName + ".yml");
        this.grid = grid;
        this.saveGame();
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

    public void saveGame() {

        this.gameConfig.FileConfig.options().setHeader(Arrays.asList(
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
        this.gameConfig.set("Field Data.World", this.grid.world.getName());
        this.gameConfig.set("Field Data.Grid", this.grid.Serialize());
        this.gameConfig.save();
    }

    public void loadGame() {
        this.world  = Bukkit.getWorld(this.gameConfig.getString("Field Data.World"));
        this.grid   = Grid.Deserialize(this.world, (List<List<Integer>>) this.gameConfig.getList("Field Data.Grid"));

        this.gameName       = this.gameConfig.getString("Properties.Name");
        this.cashHats       = this.gameConfig.getBoolean("properties." + "CashHats");
        this.Interval       = this.gameConfig.getInt("properties." + "Interval");
        this.pointsPerKill  = this.gameConfig.getInt("properties." + "Points per Kill");
    }

    public void deleteSave() {
        this.gameConfig.remove();
    }
}
