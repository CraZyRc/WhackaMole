package whackamole.whackamole.DB;

import org.bukkit.Location;
import whackamole.whackamole.DB.Model.Column;
import whackamole.whackamole.DB.Model.Table;

import java.util.List;

public class HologramDB extends Table<HologramRow> {
  protected HologramDB(SQLite sql) {
    super(sql, "Hologram", new Column<?>[]{
            new Column<>("gameID", Integer.class).AllowNull(false),
            new Column<>("holoID", Integer.class).IsPrimaryKey(true).AllowNull(false),
            new Column<>("Type", String.class).AllowNull(false),
            new Column<>("Location", Location.class).AllowNull(false),
            new Column<>("topCount", Integer.class).Default(3),
            new Column<>("showID", Boolean.class)
    }, HologramRow.class);
  }

  public List<HologramRow> Select(int gameID, int holoID) {
    return this.Select("gameID = ? AND holoID = ?", gameID, holoID);
  }

  public List<HologramRow> Select(int gameID) {
    return this.Select("gameID = ?", gameID);
  }

  public HologramRow Insert(int gameID, int holoID, String type, Location loc, int topCount, boolean showID) {
    var row = new HologramRow();
    row.holoID = holoID;
    row.gameID = gameID;
    row.Type = type;
    row.Location = loc;
    row.topCount = topCount;
    row.showID = showID;

    return this.Insert(row);
  }

  public void update(HologramRow row) {
    this.Update(row);
  }

  public void Delete(int holoID) {
    var row = new HologramRow();
    row.holoID = holoID;
    this.Delete(row);
  }
}

