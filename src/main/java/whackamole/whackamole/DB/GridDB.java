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
            new Column<>("gameID", Integer.class).AllowNull(false),
            new Column<>("X", Integer.class).AllowNull(false),
            new Column<>("Y", Integer.class).AllowNull(false),
            new Column<>("Z", Integer.class).AllowNull(false),
        }, GridRow.class);
    }


    public List<GridRow> Select(int gameID) {
        return this.Select("gameID = %s".formatted(gameID));
    }

    public List<GridRow> Insert(Grid grid, int gameID) {
        List<GridRow> rowList = new ArrayList<>();
        for (Block block : grid.grid) {
            rowList.add(this.Insert(block, gameID));
        }
        return rowList;
    }

    public GridRow Insert(Block block, int gameID) {
        var row = new GridRow();
        row.gameID = gameID;
        row.X = block.getX();
        row.Y = block.getY();
        row.Z = block.getZ();
        return this.Insert(row);
    }
}
