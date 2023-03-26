package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Column;
import whackamole.whackamole.DB.Model.Table;

import java.util.UUID;

public class CooldownDB extends Table<CooldownRow> {
    public CooldownDB(SQLite sql) {
        super(sql, "Cooldown", new Column<?>[] {
            new Column<Integer>("gameID", Integer.class).IsPrimaryKey(true).AllowNull(true),
            new Column<UUID>("playerID", UUID.class).IsPrimaryKey(true).AllowNull(true),
            new Column<Long>("endtimeStamp", Long.class),
        }, CooldownRow.class);
    }

    public CooldownRow Insert(int gameID, UUID player, Long endTime) {
        var row = new CooldownRow();
        row.gameID = gameID;
        row.playerID = player;
        row.endTimeStamp = endTime;
        this.Insert(row);
        return row;
    }

    public void Delete(int gameID, UUID player) {
        // TODO: Not implemented Yet
    }
}
