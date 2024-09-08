package whackamole.whackamole.DB.Model;

import whackamole.whackamole.DB.SQLite;

public class TableCreator extends Table<TestTableRow> {

    static public Table<TestTableRow> CreateTable(SQLite sql, String tableName, Column<?>[] columns)
    {
        return new TableCreator(sql, tableName, columns, TestTableRow.class);
    }

    public TableCreator(SQLite SQL, String TableName, Column<?>[] ColumnNames, Class<TestTableRow> type) {
        super(SQL, TableName, ColumnNames, type);
    }
}
