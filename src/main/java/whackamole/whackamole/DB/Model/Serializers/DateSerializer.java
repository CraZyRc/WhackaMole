package whackamole.whackamole.DB.Model.Serializers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DateSerializer implements ISerializer<Date> {

    @Override
    public String GetType() {
        return "TEXT";
    }

    @Override
    public String Serialize(Date object) { return object.toString(); }

    @Override
    public Date Deserialize(ResultSet set, String name) throws SQLException {
        return set.getDate(name);
    }
}
