package whackamole.whackamole.DB;

import whackamole.whackamole.Game;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScoreboardRow implements Row {

    public String
            playerID;
    public Long
    Datetime;

    public int
    ID
            ,   Score
            ,   molesHit
            ,   highestStreak
            ,   gameID;


    public static ScoreboardRow fromSet(ResultSet set) throws SQLException {
        var item                = new ScoreboardRow();

        item.playerID           = set.getNString("playerID");
        item.gameID             = set.getInt("gameID");
        item.Datetime           = set.getLong("Datetime");
        item.ID                 = set.getInt("ID");
        item.Score              = set.getInt("Score");
        item.molesHit           = set.getInt("molesHit");
        item.highestStreak      = set.getInt("highestStreak");

        return item;
    }

    public Object[] insertSpread() {
        return new Object[] {
                this.playerID
                ,   this.gameID
                ,   this.Score
                ,   this.molesHit
                ,   this.highestStreak
                ,   this.Datetime
        };
    }

    public Object[] updateSpread() {
        return new Object[] {
                this.playerID
                ,   this.gameID
                ,   this.Score
                ,   this.molesHit
                ,   this.highestStreak
                ,   this.Datetime
                ,   this.ID
        };
    }

    public static ScoreboardRow fromClass(Game.Scoreboard.Score Score, int gameID) {
        var item = new ScoreboardRow();
        item.playerID = String.valueOf(Score.player.getUniqueId());
        item.gameID = gameID;
        item.Score = Score.score;
        item.molesHit = Score.molesHit;
        item.highestStreak = Score.highestStreak;
        item.Datetime = Score.timestamp;

        return item;
    }
}
