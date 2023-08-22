package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Row;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScoreboardRow extends Row {

    public UUID playerID;
    public LocalDateTime Datetime;

    public int
        ID  
    ,   Score
    ,   molesHit
    ,   gameID
    ,   scoreStreak;
}
