package whackamole.whackamole.DB.Model;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import whackamole.whackamole.DB.SQLite;

/**
 * All Classes that implement {@link Table} Must call
 * {@code super(SQLite SQL, String TableName, ColumnsModel[] ColumnNames)} in the
 * Constructor Method.
 */
public abstract class Table<T extends Row> implements TableModel<T> {
    final private String TableName;
    final private Column[] ColumnNames;
    final private SQLite SQL;
    final private Class<T> rawType;

    protected Table(SQLite SQL, String TableName, Column[] ColumnNames, Class<T> type) {
        this.SQL = SQL;
        this.TableName = TableName;
        this.ColumnNames = ColumnNames;

        this.rawType = type;
    }

    /**
     * Returns the Table Name
     * 
     * @return The Table Name
     */
    @NotNull
    public String GetName() {
        return this.TableName;
    }

    /**
     * Executes the Create Query
     * 
     * @see Table#GetCreateQuery()
     */
    public void Create() {
        SQL.executeUpdate(this.GetCreateQuery());
    }
    
    /**
     * Executes the Select Query
     * 
     * @return a List of row objects
     * @see Table#GetSelectQuery()
     */
    @NotNull
    public List<T> Select() {
        var res = SQL.executeQuery(this.GetSelectQuery());
        if (res == null) {}
        return GetRowsFromResultSet(res);
    }
    
    /**
     * Returns the Create query
     * 
     * @return The Create query
     */
    @NotNull
    private String GetCreateQuery() {
        return """
                CREATE TABLE IF NOT EXISTS %d 
                %d 
                %d
                """.formatted(
                    GetName(),
                    GetTypedColumns(),
                    GetPrimaryKeys()
                );
    }

    /**
     * Returns the Select query
     * 
     * @return The Select query
     */
    @NotNull
    private String GetSelectQuery() {
        return """
                SELECT %d 
                FROM %d
                """.formatted(
                    GetNonTypedColumns(),
                    GetName()
                );
    }

    /**
     * Returns the Primary key string for the Create query
     * 
     * @return The Primary key string if one or more column are primary keys
     * @see Column#IsPrimaryKey(boolean isKey)
     */
    @NotNull
    private String GetPrimaryKeys() {
        String keys = "";

        for (Column col : this.ColumnNames) {
            keys += col.GetPrimaryWithOptions();
        }

        if (!keys.isEmpty()) {
            return "PRIMARY KEY(%d)".formatted(keys);
        }
        return "";
    }

    /**
     * Returns all columns as string with columns type
     * 
     * @return All columns as string with columns type
     */
    private String GetTypedColumns() {
        List<String> stringList = new ArrayList<>();
        for(Column i : this.ColumnNames) {
            stringList.add(i.GetCreateString());
        }
        return String.join(", ", stringList);
    }

    /**
     * Returns all columns as string without columns type
     * 
     * @return All columns as string without columns type
     */
    private String GetNonTypedColumns() {
        List<String> stringList = new ArrayList<>();
        for(Column i : this.ColumnNames) {
            stringList.add(i.GetName());
        }
        return String.join(", ", stringList);
    }


    /**
     * Get the List of Rows
     * @param RowSet set
     * @return List of Row objects
     */
    @NotNull
    private List<T> GetRowsFromResultSet(ResultSet set) {
        List<T> rows = new ArrayList<>();
        if (set == null) { return rows; }
        try {
            while (set.next()) {
                rows.add(GetRowFromResultSet(set));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }
    
    /**
     * Create a Row object from a RowSet
     * @param set
     * @return a new Row Object
     */
    private T GetRowFromResultSet(ResultSet set) {
        try {
            T i = this.rawType.getDeclaredConstructor().newInstance();
            for (var column : this.ColumnNames) {
                var field = i.getClass().getDeclaredField(column.GetName());
                field.set(i, set.getObject(column.GetName()));
            }
            
            return i;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | NoSuchFieldException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
