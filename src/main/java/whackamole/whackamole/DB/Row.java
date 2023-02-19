package whackamole.whackamole.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import whackamole.whackamole.Logger;

public interface Row {


    Object[] insertSpread();

    static <T extends Row> T fromClass(Class<?> item) {
        return null;
    }

    static <T extends Row> T fromSet(ResultSet set) throws SQLException  {
        throw new SQLException("Needs to be overridden");
    }

    static <T extends Row> List<T> SetToList(ResultSet set) {
        List<T> rowList = new ArrayList<>();
        try {
            while (set.next()) {
                rowList.add(fromSet(set));
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }

        return rowList;
    }
}
