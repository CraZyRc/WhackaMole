package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Row;

public class GameRow extends Row {
    /**
     * The game name
     */
    public String Name;
    
    /**
     * The name of the world the game is in
     */
    public String worldName;

    /**
     * The direction the moles face
     */
    public String spawnDirection = "NORTH";

    /**
     * Determines if the jackpot is allowed to be spawned
     */
    public boolean hasJackpot = true;

    /**
     * The Game ID
     */
    public int ID = -1;
    
    /**
     * The the change that a jackpot will spawn
     * Requires `hasJackpot` to be `true`
     */
    public int jackpotSpawnChance = 1;
    
    /**
     * The amount of moles allowed to be missed in a game
     */
    public int missCount = 3;
    
    /**
     * The amount of point given for each mole hit
     */
    public int scorePoints = 1;
    
    /**
     * The Amount of moles to be hit before a difficulty increase is aplied
     */
    public int difficultyScore = 1;

    /**
     * The amount in seconds between each mole spawn attempt
     */
    public double spawnTimer = 1;
    
    /**
     * The chance to spawn a mole on a scale between 0 and 100
     */
    public double spawnChance = 100;
    
    /**
     * The move speed of the moles, the lower the number the faster the mole
     */
    public double moleSpeed = 2;
    
    /**
     * The percentage fomr 0 to 100 to decrease the mole speed and spawn speed
     */
    public double difficultyScale = 25;

    /**
     * The aomunt of miliseconds the player has to wait before they can play the game again.
     */
    public Long Cooldown = 86400000L;
}
