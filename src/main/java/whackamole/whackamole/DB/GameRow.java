package whackamole.whackamole.DB;

import java.sql.ResultSet;
import java.sql.SQLException;

import whackamole.whackamole.Game;


public class GameRow implements Row {
    public String
        Name
    ,   worldName
    ,   spawnDirection;

    public int
        ID
    ,   hasJackpot
    ,   jackpotSpawnChance
    ,   missCount
    ,   scorePoints
    ,   difficultyScore
    ,   Cooldown;

    public double
        spawnTimer
    ,   spawnChance
    ,   moleSpeed
    ,   difficultyScale;

    public static GameRow fromSet(ResultSet set) throws SQLException {
        var item                = new GameRow();

        item.Name               = set.getNString("Name");
        item.worldName          = set.getNString("worldName");
        item.spawnDirection     = set.getNString("spawnDirection");
        item.ID                 = set.getInt("ID");
        item.hasJackpot         = set.getInt("hasJackpot");
        item.jackpotSpawnChance = set.getInt("jackpotSpawnChance");
        item.missCount          = set.getInt("missCount");
        item.scorePoints        = set.getInt("scorePoints");
        item.difficultyScore    = set.getInt("difficultyScore");
        item.Cooldown           = set.getInt("Cooldown");
        item.spawnTimer         = set.getLong("spawnTimer");
        item.spawnChance        = set.getLong("spawnChance");
        item.moleSpeed          = set.getLong("moleSpeed");
        item.difficultyScale    = set.getLong("difficultyScale");

        return item;
    }

    public Object[] insertSpread() {
        return new Object[] {
            this.Name
        ,   this.worldName
        ,   this.spawnDirection
        };
    }

    public static GameRow fromClass(Game game) {
        var item = new GameRow();
        var settings = game.getSettings();
        item.ID = game.ID;
        item.Name = game.name;
        item.worldName = settings.world.getName();
        item.spawnDirection = settings.spawnRotation.toString();
        item.hasJackpot = settings.Jackpot ? 1 : 0;
        item.jackpotSpawnChance = settings.jackpotSpawn;
        item.missCount = settings.maxMissed;
        item.scorePoints = settings.pointsPerKill;
        item.difficultyScore = settings.difficultyScore;
        // item.Cooldown = settings.Cooldown;
        // TODO: cooldown to double
        item.spawnTimer = settings.Interval;
        item.spawnChance = settings.spawnChance;
        item.moleSpeed = settings.moleSpeed;
        item.difficultyScale = settings.difficultyScale;

        return item;
    }
}
