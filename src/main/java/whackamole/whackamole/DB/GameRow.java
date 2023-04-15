package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Row;

public class GameRow extends Row {
    public String
        Name
    ,   worldName
    ,   spawnDirection;

    public boolean hasJackpot;

    public int
        ID
    ,   jackpotSpawnChance
    ,   missCount
    ,   scorePoints
    ,   difficultyScore;

    public double
        spawnTimer
    ,   spawnChance
    ,   moleSpeed
    ,   difficultyScale;
    
    public Long Cooldown;
}
