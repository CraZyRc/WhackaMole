package whackamole.whackamole.GS;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import whackamole.whackamole.DB.GameDB;
import whackamole.whackamole.DB.GameRow;
import whackamole.whackamole.DB.SQLite;

import static whackamole.whackamole.GS.Game.Directions;

public class Settings extends GameRow {
    public GameDB gameDB = SQLite.getGameDB();
    private Game game;

    public World world;
    public BlockFace spawnRotation;

    public Settings(Game game) {
        this.game = game;
    }

    public String getCooldown() {
        return game.cooldown.formatSetCooldown(this.Cooldown);
    }

    public void onLoad(GameRow a) {
        upCast(a, this);
        this.onLoad();
    }

    public void onLoad() {
        this.world = Bukkit.getWorld(this.worldName);
        this.spawnRotation = BlockFace.valueOf(this.spawnDirection);
    }

    public void Setup(String name, Player player) {
        this.Name = name;
        this.spawnRotation = Directions[Math.round(player.getLocation().getYaw() / 45) & 0x7];
        this.world = player.getWorld();
        this.Save();
    }

    public void Save() {
        this.worldName = this.world.getName();
        this.spawnDirection = this.spawnRotation.name();
        if (this.ID == -1)  gameDB.Insert(this);
        else                gameDB.Update(this);
    }

    public void Delete() {
        if (this.ID != -1) gameDB.Delete(this);
    }
}
