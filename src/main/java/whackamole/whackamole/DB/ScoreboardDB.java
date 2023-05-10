package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Column;
import whackamole.whackamole.DB.Model.Table;

import java.util.List;
import java.util.UUID;

public class ScoreboardDB extends Table<ScoreboardRow> {
    public ScoreboardDB(SQLite sql) {
        super(sql, "Scoreboard", new Column<?>[] {
            new Column<>("ID", Integer.class).AllowNull(false).IsPrimaryKey(true).HasAutoIncrement(true),
            new Column<>("playerID", UUID.class).AllowNull(false),
            new Column<>("gameID", Integer.class).AllowNull(false),
            new Column<>("Score", Integer.class).AllowNull(false),
            new Column<>("molesHit", Integer.class).AllowNull(false),
            new Column<>("scoreStreak", Integer.class).AllowNull(false),
            new Column<>("Datetime", Long.class).BuildInDefault("CURRENT_TIMESTAMP"),
        }, ScoreboardRow.class);
    }

    public List<ScoreboardRow> Select(int gameID) {
        return this.Select("gameID = ?", gameID);
    }
    public List<ScoreboardRow> Select(UUID player) {
        return this.Select("playerID = ?", player);
    }
    public List<ScoreboardRow> Select(int gameID, UUID player) {
        return this.Select("gameID = ? AND playerID = ?", gameID, player);
    }

    public ScoreboardRow Insert(UUID player, int gameID, int score, int molesHit, int scoreStreak) {
        var row = new ScoreboardRow();
        row.gameID = gameID;
        row.playerID = player;
        row.Score = score;
        row.molesHit = molesHit;
        row.scoreStreak = scoreStreak;
        return this.Insert(row);
    }

    public void Delete(int ID) {
        var row = new ScoreboardRow();
        row.ID = ID;
        this.Delete(row);
    }
}
