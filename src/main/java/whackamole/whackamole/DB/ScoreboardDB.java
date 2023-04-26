package whackamole.whackamole.DB;


import whackamole.whackamole.Game;
import whackamole.whackamole.Logger;

import java.sql.SQLException;
import java.util.List;

public class ScoreboardDB implements Table {
    private SQLite sql;

    public ScoreboardDB(SQLite sql) {
        this.sql = sql;
    }

    public void Create() {
        var query = """
                CREATE TABLE IF NOT EXISTS Scoreboard (
                    ID INTEGER NOT NULL
                ,   playerID TEXT NOT NULL
                ,   gameID INTEGER NOT NULL
                ,   Score INTEGER NOT NULL
                ,   molesHit INTEGER NOT NULL
                ,   highestStreak INTEGER
                ,   Datetime REAL DEFAULT CURRENT_TIMESTAMP
                ,   PRIMARY KEY(ID AUTOINCREMENT)
                )""";
        this.sql.executeUpdate(query);
    }

    public List<ScoreboardRow> Select() {
        var query = "SELECT * FROM Scoreboard";
        return Row.SetToList(this.sql.executeQuery(query));
    }
    public List<ScoreboardRow> Select(int Score) {
        var query = "SELECT * FROM Scoreboard WHERE Score = ?";
        return Row.SetToList(this.sql.executeQuery(query, Score));
    }
    public List<ScoreboardRow> Select(Long Date) {
        var query = "SELECT * FROM Scoreboard WHERE Datetime = ?";
        return Row.SetToList(this.sql.executeQuery(query, Date));
    }

    public int Insert(Game.Scoreboard.Score Scoreboard, int gameID) {
        return this.Insert(ScoreboardRow.fromClass(Scoreboard, gameID));
    }

    public int Insert(Row row) {
        var query = """
            INSERT INTO Scoreboard (
                    playerID
                ,   gameID
                ,   Score
                ,   molesHit
                ,   highestStreak
                ,   Datetime
            ) VALUES 
            (?, ?, ?, ?, ?, ?)
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
                UPDATE Scoreboard 
                SET playerID = ?
                ,   gameID = ?
                ,   Score = ?
                ,   molesHit = ?
                ,   highestStreak = ?
                ,   Datetime = ?
                WHERE ID = ?
                """;
        this.sql.executeUpdate(query, row.updateSpread());
    }
}
