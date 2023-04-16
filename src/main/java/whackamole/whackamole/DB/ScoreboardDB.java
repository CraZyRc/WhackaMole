package whackamole.whackamole.DB;


import whackamole.whackamole.Game;
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
            new Column<>("Datetime", Long.class).BuildInDefault("CURRENT_TIMESTAMP"),
        }, ScoreboardRow.class);
    }

    public List<ScoreboardRow> Select(int Score) {
        return this.Select("Score = %s".formatted(Score));
    }

    public List<ScoreboardRow> Select(UUID player) {
        return this.Select("playerID = '%s'".formatted(player));
    }

    public ScoreboardRow Insert(Game.Scoreboard.Score score, int gameID) {
        var row = new ScoreboardRow();
        row.gameID = gameID;
        row.playerID = score.player.getUniqueId();
        row.Score = score.score;
        return this.Insert(row);
    }
}
