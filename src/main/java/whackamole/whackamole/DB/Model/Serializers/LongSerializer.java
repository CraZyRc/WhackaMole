package whackamole.whackamole.DB.Model.Serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LongSerializer implements ISerializer<Long> {

    @Override
    public String GetType() {
        return "INTEGER";
    }

    @Override
    public String Serialize(Long object) {
        return object.toString();
    }

    @Override 
    public Long Deserialize(ResultSet set, String name) throws SQLException {
        return Long.valueOf(set.getLong(name));
    }
}
