package whackamole.whackamole.DB;

import org.bukkit.block.Block;
import whackamole.whackamole.Grid;

import whackamole.whackamole.DB.Model.Table;
import whackamole.whackamole.DB.Model.Column;

import java.util.ArrayList;
import java.util.List;

public class GridDB extends Table<GridRow> {

    public GridDB(SQLite sql) {
        super(sql, "Grid", new Column<?>[] {
            new Column<>("worldName", String.class).AllowNull(false),
            new Column<>("gameID", Integer.class).AllowNull(false),
            new Column<>("X", Integer.class).AllowNull(false),
            new Column<>("Y", Integer.class).AllowNull(false),
            new Column<>("Z", Integer.class).AllowNull(false),
        }, GridRow.class);
    }


    public List<GridRow> Select(int gameID) {
        return this.Select("gameID = ?", gameID);
    }
    
    public List<GridRow> Select(String worldName) {
        return this.Select("worldName = ?", worldName);
    }

    public List<GridRow> Insert(Grid grid, int gameID) {
        String worldName = grid.world.getName();
        List<GridRow> rowList = new ArrayList<>();
        for (Block block : grid.grid) {
            rowList.add(this.Insert(block, worldName, gameID));
        }
        return rowList;
    }

    public GridRow Insert(Block block, String worldName, int gameID) {
        var row = new GridRow();
        row.gameID = gameID;
        row.X = block.getX();
        row.Y = block.getY();
        row.Z = block.getZ();
        row.worldName = worldName;
        return this.Insert(row);
    }

    public void Delete(int gameID) {
        this.Delete("gameID = ?", gameID);
    }
    public void Delete(GridRow row) {
        this.Delete("worldName = ? AND gameID = ? AND X = ? AND Y = ? AND Z = ?", row.worldName, row.gameID, row.X, row.Y, row.Z);
    }
}
