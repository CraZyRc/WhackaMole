package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Column;
import whackamole.whackamole.DB.Model.Table;

import java.util.List;
import java.util.UUID;

public class CooldownDB extends Table<CooldownRow> {
    public CooldownDB(SQLite sql) {
        super(sql, "Cooldown", new Column<?>[] {
            new Column<Integer>("gameID", Integer.class).IsPrimaryKey(true).AllowNull(false),
            new Column<UUID>("playerID", UUID.class).IsPrimaryKey(true).AllowNull(false),
            new Column<Long>("endTimeStamp", Long.class),
        }, CooldownRow.class);
    }

    public List<CooldownRow> Select(int gameID) {
        return this.Select("gameID = ?", gameID);
    }
    
    public List<CooldownRow> Select(int gameID, UUID player) {
        return this.Select("gameID = ? AND playerID = ?", gameID, player);
    }

    public CooldownRow Insert(int gameID, UUID player, Long endTime) {
        var row = new CooldownRow();
        row.gameID = gameID;
        row.playerID = player;
        row.endTimeStamp = endTime;
        return this.Insert(row);
    }

    public void Delete(int gameID, UUID player) {
        var row = new CooldownRow();
        row.gameID = gameID;
        row.playerID = player;
        this.Delete(row);
    }
}
