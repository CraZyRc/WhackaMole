package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Row;

import java.util.UUID;

public class ScoreboardRow extends Row {

    public UUID playerID;
    public Long datetime;

    public int
        ID  
    ,   Score
    ,   molesHit
    ,   gameID
    ,   scoreStreak;
}
