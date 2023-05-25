package whackamole.whackamole.DB.Model.Serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringSerializer implements ISerializer<String> {

    @Override
    public String GetType() {
        return "TEXT";
    }

    @Override
    public String Serialize(String object) {
        return String.format("'%s'", object);
    }


    @Override
    public String Deserialize(ResultSet set, String name) throws SQLException {
        return set.getString(name);
    }
}
