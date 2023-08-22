package whackamole.whackamole.DB.Model.Serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanSerializer implements ISerializer<Boolean> {

    @Override
    public String GetType() {
        return "INTEGER";
    }

    @Override
    public String Serialize(Boolean object) {
        return String.format("%s", object ? 1 : 0);
    }

    @Override
    public Boolean Deserialize(ResultSet set, String name) throws SQLException {
        return set.getInt(name) > 0;
    }
}
