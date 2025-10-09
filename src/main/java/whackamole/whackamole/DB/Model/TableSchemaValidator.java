package whackamole.whackamole.DB.Model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import whackamole.whackamole.DB.SQLite;

public class TableSchemaValidator {
    static protected void ValidateSchema(SQLite sql, Table<?> table)
    {
        if (! DBHasTable(sql, table.GetName())) {
            table.Create();
        } else {
            ValidateColumnSchema(sql, table);
        }
    }

    static private void ValidateColumnSchema(SQLite sql, Table<?> table)
    {
        var DBColumns = getDBColumns(sql, table.GetName());
        try {
            var cols = Arrays.stream(table.getColumns()).collect(Collectors.toCollection(ArrayList::new));
            while (DBColumns.next()) {
                var name = DBColumns.getString("name");
                var col = getTableColumn(table, name);
                if (col == null) {
                    // * Remove column from DB
                    sql.executeUpdate("ALTER TABLE %s DROP column %s".formatted(table.GetName(), name));
                    return;
                }
                cols.removeIf((column) -> column.GetName().equals(name));
            }
            for(var col : cols) {
                // * Add missing columns
                sql.executeUpdate("ALTER TABLE %s ADD %s".formatted(table.GetName(), col.GetCreateString()));
            }
        } catch (Exception e) {}
    }

    static private boolean DBHasTable(SQLite sql, String tableName)
    {
        var data = sql.executeQuery("select count(name) from sqlite_schema where name = ?", tableName);
        try {
            return data.getInt("count(name)") == 1;
        } catch (Exception _e) {}
        return false;
    }

    static private ResultSet getDBColumns(SQLite sql, String tableName) {
        var data = sql.executeQuery("select * from pragma_table_info(?)", tableName);
        return data;
    }

    static private Column<?> getTableColumn(Table<?> table, String columnName) {
        for (var column : table.getColumns()) {
            if (column.GetName().equals(columnName)) {
                return column;
            }
        }
        return null;
    }
}
