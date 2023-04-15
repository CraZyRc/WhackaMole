package whackamole.whackamole.DB;

import java.util.List;

import org.bukkit.World;

import whackamole.whackamole.Game;
import whackamole.whackamole.DB.Model.Column;
import whackamole.whackamole.DB.Model.Table;

public class GameDB extends Table<GameRow> {
    public GameDB(SQLite sql) {
        super(sql, "GameDB", new Column<?>[] {
            new Column<>("ID",                  Integer.class).IsPrimaryKey(true).IsUnique(true).AllowNull(false).HasAutoIncrement(true),
            new Column<>("Name",                String.class).AllowNull(false),
            new Column<>("worldName",           String.class),
            new Column<>("spawnDirection",      String.class).Default("NORTH"),
            new Column<>("hasJackpot",          Boolean.class).Default(true),
            new Column<>("jackpotSpawnChance",  Integer.class).Default(1),
            new Column<>("missCount",           Integer.class).Default(3),
            new Column<>("scorePoints",         Integer.class).Default(1),
            new Column<>("spawnTimer",          Double.class).Default((double) 1),
            new Column<>("spawnChance",         Double.class).Default((double) 100),
            new Column<>("moleSpeed",           Double.class).Default((double) 2),
            new Column<>("difficultyScale",     Double.class).Default((double) 10),
            new Column<>("difficultyScore",     Integer.class).Default(1),
            new Column<>("Cooldown",            Long.class).Default(86400000L),
        }, GameRow.class);
    }

    public List<GameRow> Select(World world) {
        return this.Select("worldName = '%s'".formatted(world.getName()));
    }

    public List<GameRow> Select(int GameID) {
        return this.Select("ID = '%s'".formatted(GameID));
    }

    public GameRow Insert(Game game) {
        var row = new GameRow();
        var settings = game.getSettings();
        row.Name = game.name;
        row.worldName = settings.world.getName();
        row.spawnDirection = settings.spawnRotation.name();
        return this.Insert(row);
    }

    public void Update(Game game) {
        var row = new GameRow();
        var settings = game.getSettings();
        row.ID = game.ID;
        row.Name = game.name;
        row.worldName = settings.world.getName();
        row.spawnDirection = settings.spawnRotation.name();
        row.hasJackpot = settings.Jackpot;
        row.jackpotSpawnChance = settings.jackpotSpawn;
        row.missCount = settings.maxMissed;
        row.scorePoints = settings.pointsPerKill;
        row.spawnTimer = settings.Interval;
        row.spawnChance = settings.spawnChance;
        row.moleSpeed = settings.moleSpeed;
        row.difficultyScale = settings.difficultyScale;
        row.difficultyScore = settings.difficultyScore;
        row.Cooldown = settings.Cooldown;
        
        this.Update(row);
    }

}
