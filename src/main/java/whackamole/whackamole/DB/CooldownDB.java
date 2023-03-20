package whackamole.whackamole.DB;

import org.bukkit.entity.Player;
import whackamole.whackamole.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CooldownDB implements Table {
    private SQLite sql;

    public CooldownDB(SQLite sql) {
        this.sql = sql;
    }

    public void Create() {
        var query = """
                CREATE TABLE IF NOT EXISTS Cooldown (
                gameID INTEGER NOT NULL
                ,   playerID TEXT NOT NULL
                ,   endTimeStamp REAL
                ,   PRIMARY KEY(gameID, playerID)
                )""";
        this.sql.executeUpdate(query);
    }

    public List<CooldownRow> Select() {
        var query = "SELECT * FROM Cooldown";
        return Row.SetToList(this.sql.executeQuery(query));
    }

    public int Insert(int gameID, UUID player, Long endTime) {
        return this.Insert(CooldownRow.fromClass(gameID, player, endTime));
    }

    public int Insert(Row row) {
        var query = """
            INSERT INTO Cooldown (
                    gameID
                ,   playerID
                ,   endTimeStamp
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
    public void Update(Row row) {
        var query = """
                UPDATE Cooldown 
                SET playerID = ?
                ,   endTimeStamp = ?
                WHERE gameID = ?
                """;
        this.sql.executeUpdate(query, row.updateSpread());
    }

    public void Delete(int gameID, UUID player) {
        var query = """
                DELETE FROM Cooldown
                WHERE gameID = ?
                AND playerID = ?
                """;
        this.sql.executeUpdate(query, gameID, player);
    }
}
