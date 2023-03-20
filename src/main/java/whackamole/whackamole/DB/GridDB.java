package whackamole.whackamole.DB;

import org.bukkit.block.Block;
import whackamole.whackamole.Game;
import whackamole.whackamole.Grid;
import whackamole.whackamole.Logger;

import java.sql.SQLException;
import java.util.List;

public class GridDB implements Table {
    private SQLite sql;

    public GridDB(SQLite sql) {
        this.sql = sql;
    }

    public void Create() {
        var query = """
                CREATE TABLE IF NOT EXISTS Grid (
                    gameID INTEGER NOT NULL
                ,   X INTEGER NOT NULL
                ,   Y INTEGER NOT NULL
                ,   Z INTEGER NOT NULL
                )""";
        this.sql.executeUpdate(query);
    }

    public List<GridRow> Select() {
        var query = "SELECT * FROM Grid";
        return Row.SetToList(this.sql.executeQuery(query));
    }


    public void Insert(Grid grid, int gameID) {

        for (Block block : grid.grid) {
            this.Insert(GridRow.fromClass(block, gameID));
        }
    }

    public int Insert(Row row) {
        var query = """
            INSERT INTO Grid (
                    gameID
                ,   X
                ,   Y
                ,   Z
            ) VALUES 
            (?, ?, ?, ?)
        """;
        this.sql.executeUpdate(query, row.insertSpread());
        try {
            var result = this.sql.executeQuery("SELECT last_insert_rowid()");
            return result.getInt(1);
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            Logger.error(e.getStackTrace().toString());
            return -1;
        }
    }
    public void Update(Row row) {
        var query = """
                UPDATE Grid 
                SET X = ?
                ,   Y = ?
                ,   Z = ?
                WHERE gameID = ?
                """;
        this.sql.executeUpdate(query, row.updateSpread());
    }
}
