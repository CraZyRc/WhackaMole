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
    public String moleHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIxMjUwM2Q2MWM0OWY3MDFmZWU4NjdkNzkzZjFkY2M1MjJlNGQ3YzVjNDFhNjhmMjk1MTU3OWYyNGU3Y2IyYSJ9fX0=";
    
    /**
    * The Jackpot head to be used
    */  
    public String jackpotHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTlkZGZiMDNjOGY3Zjc4MDA5YjgzNDRiNzgzMGY0YTg0MThmYTRiYzBlYjMzN2EzMzA1OGFiYjdhMDVlOTNlMSJ9fX0=";
    
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
     * The location where players that aren't playing the game should be teleported to when trying to enter the active grid
     */
    public Location teleportLocation;

    /**
     * The location where the top scores will be displayed in hologram style
     */
    public Location scoreLocation;

    /**
     * The aomunt of miliseconds the player has to wait before they can play the game again.
     */
    public Long Cooldown = 86400000L;
}
