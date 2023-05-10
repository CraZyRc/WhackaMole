package whackamole.whackamole.DB;

import java.sql.ResultSet;
import java.sql.SQLException;

import whackamole.whackamole.Game;


public class GameRow implements Row {
    public String
        Name
    ,   worldName
    ,   spawnDirection
    ,   moleHead
    ,   jackpotHead;

    public int
        ID
    ,   hasJackpot
    ,   jackpotSpawnChance
    ,   missCount
    ,   scorePoints
    ,   difficultyScore;

    public double
    spawnTimer
    ,   spawnChance
    ,   moleSpeed
    ,   difficultyScale
    ,   Cooldown;

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
        item.spawnTimer         = set.getLong("spawnTimer");
        item.spawnChance        = set.getLong("spawnChance");
        item.moleSpeed          = set.getLong("moleSpeed");
        item.difficultyScale    = set.getLong("difficultyScale");
        item.difficultyScore    = set.getInt("difficultyScore");
        item.Cooldown           = set.getLong("Cooldown");
        item.moleHead           = set.getString("moleHead");
        item.jackpotHead        = set.getString("jackpotHead");

        return item;
    }

    public Object[] insertSpread() {
        return new Object[] {
            this.Name
        ,   this.worldName
        ,   this.spawnDirection
        };
    }

    public Object[] updateSpread() {
        return new Object[] {
                this.spawnDirection
                ,   this.hasJackpot
                ,   this.jackpotSpawnChance
                ,   this.missCount
                ,   this.scorePoints
                ,   this.spawnTimer
                ,   this.spawnChance
                ,   this.moleSpeed
                ,   this.difficultyScale
                ,   this.difficultyScore
                ,   this.Cooldown
                ,   this.moleHead
                ,   this.jackpotHead
                ,   this.ID
        };
    }

    public static GameRow fromClass(Game game) {
        var item = new GameRow();
        var settings = game.getSettings();
        item.ID                     = game.ID;
        item.Name                   = game.name;
        item.worldName              = settings.world.getName();
        item.spawnDirection         = settings.spawnRotation.toString();
        item.hasJackpot             = settings.Jackpot ? 1 : 0;
        item.jackpotSpawnChance     = settings.jackpotSpawn;
        item.missCount              = settings.maxMissed;
        item.scorePoints            = settings.pointsPerKill;
        item.difficultyScore        = settings.difficultyScore;
        item.Cooldown               = settings.Cooldown;
        item.moleHead               = settings.moleHead;
        item.jackpotHead            = settings.jackpotHead;
        item.spawnTimer             = settings.Interval;
        item.spawnChance            = settings.spawnChance;
        item.moleSpeed              = settings.moleSpeed;
        item.difficultyScale        = settings.difficultyScale;

        return item;
    }
}
