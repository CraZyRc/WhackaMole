package whackamole.whackamole.DB;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CooldownRow implements Row {

    public String
        playerID;

    public int
            gameID;
    public long
            endTimeStamp;


    public static CooldownRow fromSet(ResultSet set) throws SQLException {
        var item                = new CooldownRow();

        item.gameID             = set.getInt("gameID");
        item.playerID           = set.getNString("playerID");
        item.endTimeStamp       = set.getLong("endTimeStamp");

        return item;
    }

    public Object[] insertSpread() {
        return new Object[] {
                this.gameID
                ,   this.playerID
                ,   this.endTimeStamp
        };
    }

    public Object[] updateSpread() {
        return new Object[] {
                this.gameID
                ,   this.playerID
                ,   this.endTimeStamp
        };
    }

    public static CooldownRow fromClass(int gameID, UUID player, long endTime) {
        var item = new CooldownRow();
        item.gameID = gameID;
        item.playerID = String.valueOf(player);
        item.endTimeStamp = endTime;

        return item;
    }
}
