package whackamole.whackamole.DB.Model.Serializers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateSerializer implements ISerializer<LocalDateTime> {
    @Override
    public String GetType() {
        return "BIGINT";
    }
    
    @Override
    
    public String Serialize(LocalDateTime object) { return String.format("%s", object.toEpochSecond(ZoneOffset.UTC)); }

    @Override
    public LocalDateTime Deserialize(ResultSet set, String name) throws SQLException {
        return LocalDateTime.ofEpochSecond(set.getInt(name), 0, ZoneOffset.UTC);
    }
}
