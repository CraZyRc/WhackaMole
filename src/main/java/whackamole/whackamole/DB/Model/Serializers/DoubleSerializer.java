package whackamole.whackamole.DB.Model.Serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleSerializer implements ISerializer<Double> {

    @Override
    public String GetType() {
        return "REAL";
    }

    @Override
    public String Serialize(Double object) {
        return String.format("%s", object);
    }

    @Override
    public Double Deserialize(ResultSet set, String name) throws SQLException {
        return set.getDouble(name);
    }
}
