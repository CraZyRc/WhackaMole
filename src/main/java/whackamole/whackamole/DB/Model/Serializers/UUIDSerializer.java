package whackamole.whackamole.DB.Model.Serializers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDSerializer implements ISerializer<UUID> {

    @Override
    public String GetType() {
        return "TEXT";
    }

    @Override
    public String Serialize(UUID object) {
        return String.format("'%s'", object);
    }

    @Override
    public UUID Deserialize(ResultSet set, String name) throws SQLException {
        return UUID.fromString(set.getString(name));
    }
}
