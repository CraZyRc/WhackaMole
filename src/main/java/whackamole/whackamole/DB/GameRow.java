package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Row;

public class GameRow extends Row {
    public String
        Name
    ,   worldName
    ,   spawnDirection = "NORTH";

    public boolean hasJackpot = true;

    public int
        ID = -1
    ,   jackpotSpawnChance = 1
    ,   missCount = 3
    ,   scorePoints = 1
    ,   difficultyScore = 1;

    public double
        spawnTimer = 1
    ,   spawnChance = 100
    ,   moleSpeed = 2
    ,   difficultyScale = 10;
    
    public Long Cooldown = 86400000L;
}
