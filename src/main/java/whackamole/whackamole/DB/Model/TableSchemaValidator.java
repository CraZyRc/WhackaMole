package whackamole.whackamole.DB.Model;

import java.sql.ResultSet;

import whackamole.whackamole.DB.SQLite;

public class TableSchemaValidator {
    static protected void ValidateSchema(Table<?> table)
    {
        var sql = SQLite.getInstance();
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
            while (DBColumns.next()) {
                var name = DBColumns.getString("name");
                var col = getTableColumn(table, name);
                if (col == null) {
                    // * Add column to DB
                    return;
                }
                var type = DBColumns.getString("type");
                var isNotNull = DBColumns.getInt("notnull") == 1;
                // var defaultValue = DBColumns.getString("dflt_value");
                var isPrimaryKey = DBColumns.getInt("pk") == 1;

                if (! col.getType().equals(type)) {
                    // * Update DB Type
                }

                // AllowNull is reversed from isNotNull
                if ( col.AllowNull() == isNotNull) {
                    // * Update DB NotNull
                }

                // if ( col.Default())
                // * Default validation on hold. Type conversion needs to be considered

                if ( col.IsPrimaryKey() != isPrimaryKey) {
                    // * Update DB PrimaryKey
                }
            }
        } catch (Exception e) {}
    }

    static private boolean DBHasTable(SQLite sql, String tableName)
    {
        var data = sql.executeQuery("select count(name) from sqlite_schema where name = ?", tableName);
        try {
            if (data.first()) {
                return true;
            }
        } catch (Exception _e) {}
        return false;
    }

    static private ResultSet getDBColumns(SQLite sql, String tableName) {
        var data = sql.executeQuery("pragma table_info(?)", tableName);
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
