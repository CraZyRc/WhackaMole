package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Row;

import java.sql.Date;
import java.util.UUID;

public class ScoreboardRow extends Row {

    public UUID playerID;
    public Date Datetime;

    public int
        ID  
    ,   Score
    ,   molesHit
    ,   gameID
    ,   scoreStreak;
}
