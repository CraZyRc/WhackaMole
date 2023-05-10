package whackamole.whackamole.DB;

import java.sql.SQLException;
import java.util.List;

import whackamole.whackamole.Game;
import whackamole.whackamole.Logger;

public class GameDB implements Table {
    private SQLite sql;

    public GameDB(SQLite sql) {
        this.sql = sql;
    }

    public void Create() {
        var query = """
                CREATE TABLE IF NOT EXISTS Game (
                    ID INTEGER NOT NULL UNIQUE
                ,   Name TEXT NOT NULL
                ,   worldName TEXT
                ,   spawnDirection TEXT DEFAULT 'NORTH'
                ,   hasJackpot INTEGER DEFAULT 1
                ,   jackpotSpawnChance INTEGER DEFAULT 1
                ,   missCount INTEGER DEFAULT 3
                ,   scorePoints INTEGER DEFAULT 1
                ,   spawnTimer REAL DEFAULT 1
                ,   spawnChance REAL DEFAULT 100
                ,   moleSpeed REAL DEFAULT 2
                ,   difficultyScale REAL DEFAULT 10
                ,   difficultyScore INTEGER DEFAULT 1
                ,   Cooldown INTEGER DEFAULT 86400000
                ,   moleHead TEXT DEFAULT 'eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIxMjUwM2Q2MWM0OWY3MDFmZWU4NjdkNzkzZjFkY2M1MjJlNGQ3YzVjNDFhNjhmMjk1MTU3OWYyNGU3Y2IyYSJ9fX0='
                ,   jackpotHead TEXT DEFAULT 'eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTlkZGZiMDNjOGY3Zjc4MDA5YjgzNDRiNzgzMGY0YTg0MThmYTRiYzBlYjMzN2EzMzA1OGFiYjdhMDVlOTNlMSJ9fX0='
                ,   PRIMARY KEY(ID AUTOINCREMENT)
                )""";
        this.sql.executeUpdate(query);
    }

    public List<GameRow> Select() {
        var query = "SELECT * FROM Game";
        return Row.SetToList(this.sql.executeQuery(query));
    }
    public List<GameRow> Select(String worldName) {
        var query = "SELECT * FROM Game WHERE worldName = ?";
        return Row.SetToList(this.sql.executeQuery(query, worldName));
    }

    public int Insert(Game game) {
        return this.Insert(GameRow.fromClass(game));
    }

    public int Insert(Row row) {
        var query = """
            INSERT INTO Game (
                    Name
                ,   worldName
                ,   spawnDirection
            ) VALUES 
            (?, ?, ?)
        """;
        this.sql.executeUpdate(query, row.insertSpread());
        try {
            var result = this.sql.executeQuery("SELECT last_insert_rowid()");
            return result.getInt(1);
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            Logger.error(e.getStackTrace().toString());
            return -1;
        }
    }
    public void Update(Game game) {
        this.Update(GameRow.fromClass(game));
    }
    public void Update(Row row) {
        var query = """
                UPDATE Game 
                SET spawnDirection      = ?
                ,   hasJackpot          = ?
                ,   jackpotSpawnChance  = ?
                ,   missCount           = ?
                ,   scorePoints         = ?
                ,   spawnTimer          = ?
                ,   spawnChance         = ?
                ,   moleSpeed           = ?
                ,   difficultyScale     = ?
                ,   difficultyScore     = ?
                ,   Cooldown            = ?
                ,   moleHead            = ?
                ,   jackpotHead         = ?
                WHERE ID = ?
                """;
        this.sql.executeUpdate(query, row.updateSpread());
    }
}
