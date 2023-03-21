package whackamole.whackamole.DB;

import org.bukkit.block.Block;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GridRow implements Row {

    public int
            gameID
            ,   X
            ,   Y
            ,   Z;


    public static GridRow fromSet(ResultSet set) throws SQLException {
        var item                = new GridRow();

        item.gameID             = set.getInt("gameID");
        item.X                  = set.getInt("X");
        item.Y                  = set.getInt("Y");
        item.Z                  = set.getInt("Z");

        return item;
    }

    public Object[] insertSpread() {
        return new Object[] {
                this.gameID
                ,   this.X
                ,   this.Y
                ,   this.Z
        };
    }

    public Object[] updateSpread() {
        return new Object[] {
                this.gameID
                ,   this.X
                ,   this.Y
                ,   this.Z
        };
    }

    public static GridRow fromClass(Block block, int gameID) {
        var item = new GridRow();
        item.gameID = gameID;
        item.X = block.getLocation().getBlockX();
        item.Y = block.getLocation().getBlockY();
        item.Z = block.getLocation().getBlockZ();

        return item;
    }
}
