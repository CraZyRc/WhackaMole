package whackamole.whackamole.DB;

import org.bukkit.Location;
import whackamole.whackamole.DB.Model.Row;

public class HologramRow extends Row {
  /**
   * The hologram ID
   */
  public int holoID;

  /**
   * the game ID
   */
  public int gameID;

  /**
   * the Type of topscore (Score/Streak/molesHit
   */
  public String Type;

  /**
   * Location where the Hologram is located
   */
  public Location Location;

  /**
   * Number of top score (3 means the top 3, 5 means the top 5)
   */
  public int topCount;

  /**
   * Boolean value if HoloID is visible
   */
  public boolean showID;
}
