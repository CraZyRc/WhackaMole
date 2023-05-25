package whackamole.whackamole.DB.Model.Serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerSerializer implements ISerializer<Integer> {

    @Override
    public String GetType() {
        return "INTEGER";
    }

    @Override
    public String Serialize(Integer object) {
        return String.format("%s", object);
    }

    @Override
    public Integer Deserialize(ResultSet set, String name) throws SQLException {
        return set.getInt(name);
    }


}
