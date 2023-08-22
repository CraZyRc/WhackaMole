package whackamole.whackamole.DB;

import whackamole.whackamole.DB.Model.Row;

import java.util.UUID;

public class CooldownRow extends Row {

    public UUID playerID;

    public int gameID;
    public long endTimeStamp;
}
