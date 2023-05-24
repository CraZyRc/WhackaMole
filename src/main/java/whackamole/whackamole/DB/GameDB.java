package whackamole.whackamole.DB;

import java.util.List;

import org.bukkit.World;

import whackamole.whackamole.Game;
import whackamole.whackamole.DB.Model.Column;
import whackamole.whackamole.DB.Model.Table;
import whackamole.whackamole.DB.Model.Serializers.LocationSerializer;

public class GameDB extends Table<GameRow> {
    public GameDB(SQLite sql) {
        super(sql, "Game", new Column<?>[] {
            new Column<>("ID",                  Integer.class).IsPrimaryKey(true).IsUnique(true).AllowNull(false).HasAutoIncrement(true),
            new Column<>("Name",                String.class).AllowNull(false),
            new Column<>("worldName",           String.class),
            new Column<>("TeleportLocation",    LocationSerializer.class),
            new Column<>("ScoreLocation",       LocationSerializer.class),
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
            new Column<>("moleHead",            String.class).Default("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIxMjUwM2Q2MWM0OWY3MDFmZWU4NjdkNzkzZjFkY2M1MjJlNGQ3YzVjNDFhNjhmMjk1MTU3OWYyNGU3Y2IyYSJ9fX0="),
            new Column<>("jackpotHead",         String.class).Default("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTlkZGZiMDNjOGY3Zjc4MDA5YjgzNDRiNzgzMGY0YTg0MThmYTRiYzBlYjMzN2EzMzA1OGFiYjdhMDVlOTNlMSJ9fX0="),
        }, GameRow.class);
    }
    
    public List<GameRow> Select(World world) {
        return this.Select("worldName = ?", world.getName());
    }
    
    public List<GameRow> Select(int GameID) {
        return this.Select("ID = ?", GameID);
    }

    public GameRow Insert(Game game) {
        var row = game.getSettings();
        return this.Insert(row);
    }

    public void Update(Game game) {
        var row = game.getSettings();
        this.Update(row);
    }
  
    public void Delete(int gameID) {
        var row = new GameRow();
        row.ID = gameID;
        this.Delete(row);
    }
}
