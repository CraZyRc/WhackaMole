package whackamole.whackamole.DB;

import org.bukkit.Location;

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
     * The Location where the player is teleported to when a game is running
     */
    public Location teleportLocation;

    /**
     * The Location where the Scoreboard is shown
     */
    public Location scoreLocation;

    /**
     * The Location where the current hit-streak-name is shown
     */
    public Location streakHoloLocation;

    /**
     * The direction the moles face
     */
    public String spawnDirection = "NORTH";

    /**
     * Determines if the jackpot is allowed to be spawned
     */
    public boolean hasJackpot = true;
    
    /**
    * The Mole head to be used
    */
    public String moleHead = "1b12503d61c49f701fee867d793f1dcc522e4d7c5c41a68f2951579f24e7cb2a";
    
    /**
    * The Jackpot head to be used
    */  
    public String jackpotHead = "40726284b8c12a1a3768bdc32185c155e19196dc080f04d5a67d50e492d563bb";
    
    /**
     * The Game ID
     */
    public int ID = -1;
    
    /**
     * The change that a jackpot will spawn
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
     * The percentage from 0 to 100 to decrease the mole speed and spawn speed
     */
    public double difficultyScale = 10;

    /**
     * The aomunt of miliseconds the player has to wait before they can play the game again.
     */
    public Long Cooldown = 86400000L;

    /**
     * The minecraft name of the song the player hears when a game starts
     */
    public String Music = "";
}
