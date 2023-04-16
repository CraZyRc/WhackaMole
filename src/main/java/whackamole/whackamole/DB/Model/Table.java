package whackamole.whackamole.DB.Model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import whackamole.whackamole.DB.SQLite;

/**
 * All Classes that implement {@link Table} Must call
 * {@code super(SQLite SQL, String TableName, ColumnsModel[] ColumnNames)} in the
 * Constructor Method.
 */
public abstract class Table<T extends Row> implements TableModel<T> {
    final private String TableName;
    final private Column<?>[] ColumnNames;
    final private SQLite SQL;
    final private Class<T> rawType;

    private boolean hasPrimaryKey = false;

    protected Table(SQLite SQL, String TableName, Column<?>[] ColumnNames, Class<T> type) {
        this.SQL = SQL;
        this.TableName = TableName;
        this.ColumnNames = ColumnNames;

        this.rawType = type;

        for(var i : ColumnNames) {
            if (i.IsPrimaryKey()) {
                this.hasPrimaryKey = true;
                break;
            }
        }

        assert this.ColumnsValidation() : "Columns are not fully valid";
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
     * Executes the Delete query
     * 
     * @see Table#GetDeleteQuery()
     */
    public void Delete(T row) {
        SQL.executeUpdate(this.GetDeleteQuery(row));
    }
    
    /**
     * Executes the Delete query
     * 
     * @see Table#GetDeleteQuery()
     */
    public void Delete(String whereStatement, Object... wherevalues) {
        SQL.executeUpdate(this.GetDeleteQuery(whereStatement), wherevalues);
    }
    
    /**
     * Execute Update query
     * 
     * @see Table#GetUpdateQuery()
     */
    public void Update(T row) {
        var query = this.GetUpdateQuery(row);
        if (query.isEmpty()) return;
        SQL.executeUpdate(query);
    }
    
    /**
     * Execute Update query with Where statement
     * 
     * @see Table#GetUpdateQuery(T row, String WhereStatement)
     */
    public void Update(T row, String WhereStatement, Object... whereValues) {
        var query = this.GetUpdateQuery(row, WhereStatement);
        if (query.isEmpty()) return;
        SQL.executeUpdate(query, whereValues);
    }
    
    /**
     * Execute Insert query
     * 
     * @see Table#GetInsertQuery()
     */
    public T Insert(T row) {
        var query = this.GetInsertQuery(row);
        if (query.isEmpty()) return row;
        
        SQL.executeUpdate(query);
        this.TrySetLastRowId(row);
        return row;
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
        return GetRowsFromResultSet(res);
    }
    /**
     * Executes the Select Query with a where statement
     * 
     * @return a List of row objects
     * @see Table#GetSelectQuery(String WhereStatement)
     */
    @NotNull
    public List<T> Select(String WhereStatement, Object... whereValues) {
        var res = SQL.executeQuery(this.GetSelectQuery(WhereStatement), whereValues);
        return GetRowsFromResultSet(res);
    }
    
    /**
     * Returns the Create query
     * 
     * @return The Create query
     */
    @NotNull
    private String GetCreateQuery() {
        String query = "CREATE TABLE IF NOT EXISTS " + this.GetName();
        query += "( " + this.GetTypedColumns();
        String prime = this.GetPrimaryKeys();
        if (prime != null) {
            query += ", " + prime;
        }
        query += " );";
        return query;
    }
    
    /**
     * Returns the Delete query
     * 
     * @return The Delete query
     */
    @NotNull
    private String GetDeleteQuery(T row) {
        assert this.hasPrimaryKey : "This Table has no primary key, can't delete from this table";
        // * DELETE FROM {TableName} WHERE {key}
        String query = "DELETE FROM %s WHERE %s".formatted(this.GetName(), GetUpdateWhereString(row)) ;
        return query;
    }
    
    /**
     * Returns the Delete query with where statement
     * 
     * @return The Delete query with where statement
     */
    @NotNull
    private String GetDeleteQuery(String WhereStatement) {
        // * DELETE FROM {TableName} WHERE {key}
        String query = "DELETE FROM %s WHERE %s".formatted(this.GetName(), WhereStatement) ;
        return query;
    }
    
    /**
     * Returns the Create query
     * 
     * @return The Create query
     */
    private String GetUpdateQuery(T row) {
        assert this.hasPrimaryKey : "This Table has no primary key, can't delete from this table";
        // * UPDATE {TABLE} SET {col} = {val} WHERE {col} = {val}
        var updateString = this.GetUpdateSetString(row);
        var WhereString = this.GetUpdateWhereString(row);
        if (updateString.isEmpty()) return "";
        return "UPDATE %s SET %s WHERE %s".formatted(this.GetName(), updateString, WhereString);
    }
    
    
    /**
     * Returns the Create query
     * 
     * @return The Create query
     */
    private String GetUpdateQuery(T row, String WhereStatement) {
        // * UPDATE {TABLE} SET {col} = {val} WHERE {col} = {val}
        var updateString = this.GetUpdateSetString(row);
        var WhereString = WhereStatement;
        if (updateString.isEmpty()) return "";
        return "UPDATE %s SET %s WHERE %s".formatted(this.GetName(), updateString, WhereString);
    }
    
    /**
     * Returns the Insert query
     * 
     * @return The Insert query
     */
    private String GetInsertQuery(T row) {
        // * INSERT INTO {TABLE} ({columns}) VALUEs ({values}) 
        var insertString = this.GetInsertSetString(row);
        if (insertString.isEmpty()) return "";
        return "INSERT INTO " + this.GetName() + " " + insertString;
    }

    /**
     * Returns the Select query
     * 
     * @return The Select query
     */
    @NotNull
    private String GetSelectQuery() {
        return """
                SELECT %s
                FROM %s
                """.formatted(
                    GetNonTypedColumns(),
                    GetName()
                );
    }
    /**
     * Returns the Select query wtih a where statement
     * 
     * @return The Select query
     */
    @NotNull
    private String GetSelectQuery(String WhereStatement) {
        return """
                SELECT %s
                FROM %s
                WHERE %s
                """.formatted(
                    GetNonTypedColumns(),
                    GetName(),
                    WhereStatement
                );
    }

    /**
     * Returns the Primary key string for the Create query
     * 
     * @return The Primary key string if one or more column are primary keys
     * @see Column#IsPrimaryKey(boolean isKey)
     */
    @Nullable
    private String GetPrimaryKeys() {
        List<String> keys = new ArrayList<>();

        for (Column<?> col : this.ColumnNames) {
            if(col.IsPrimaryKey()) keys.add(col.GetPrimaryWithOptions());
        }

        if (!keys.isEmpty()) {
            return "PRIMARY KEY(%s)".formatted(String.join(", ", keys));
        }
        return null;
    }

    /**
     * Returns all columns as string with columns type
     * 
     * @return All columns as string with columns type
     */
    private String GetTypedColumns() {
        List<String> stringList = new ArrayList<>();
        for(Column<?> i : this.ColumnNames) {
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
        for(Column<?> i : this.ColumnNames) {
            stringList.add(i.GetName());
        }
        return String.join(", ", stringList);
    }
    
    /**
     * Returns all update set collumns with where clauses
     * 
     * @return update columns as string with update where  
     */
    private String GetUpdateWhereString(T row) {
        List<String> whereList = new ArrayList<>();

        if (! this.hasPrimaryKey) return "";

        for(Column<?> column : this.ColumnNames) {
            try {
                var field = this.findDeclaredField(row.getClass(), column.GetName());
                if (field == null) {
                    throw new NoSuchFieldException("Field %s is not found".formatted(column.GetName()));
                }

                if(column.IsPrimaryKey()) whereList.add("%s = %s".formatted(column.GetName(), column.ValueToQueryValue(field.get(row))));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                assert false : e.getMessage();
            }
        }
        return String.join(" AND ", whereList);
    }
    
    /**
     * Returns all update set collumns with where clauses
     * 
     * @return update columns as string with update where  
     */
    private String GetUpdateSetString(T row) {
        List<String> setList = new ArrayList<>();
        for(Column<?> column : this.ColumnNames) {
            try {
                var field = this.findDeclaredField(row.getClass(), column.GetName());
                if (field == null) {
                    throw new NoSuchFieldException("Field %s is not found".formatted(column.GetName()));
                }

                if(!column.IsPrimaryKey())
                    setList.add("%s = %s".formatted(column.GetName(), column.ValueToQueryValue(field.get(row))));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                assert false : e.getMessage();
            }
        }
        if (setList.size() == 0) return "";
        return String.join(", ", setList);
    }
    
    /**
     * Return Insert partial query
     * 
     * @return Partial Insert query  
     */
    private String GetInsertSetString(T row) {
        List<String> columnList = new ArrayList<>();
        List<String> valuesList = new ArrayList<>();

        for(Column<?> column : this.ColumnNames) {
            try {
                if(column.HasAutoIncrement()) continue;

                var field = this.findDeclaredField(row.getClass(), column.GetName());
                if (field == null) {
                    throw new NoSuchFieldException("Field %s is not found".formatted(column.GetName()));
                }

                var queryValue = column.ValueToQueryValue(field.get(row));
                if (queryValue.equals("NULL")) continue;

                // TODO: skip inserting null elements if null is allowed
                columnList.add(column.GetName());
                valuesList.add(queryValue);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                assert false : e.getMessage();
            }
        }
        String out = "";
        if (columnList.size() == 0) return "";
        out += "(%s) VALUES (%s)".formatted(
            String.join(", ", columnList),
            String.join(", ", valuesList)
        );
        return out;
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
                T row = this.GetRowFromResultSet(set);
                if(row != null) {
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            assert false : e.getMessage();
        }
        return rows;
    }
    
    /**
     * Create a Row object from a RowSet
     * @param set
     * @return a new Row Object
     */
    @Nullable
    private T GetRowFromResultSet(ResultSet set) {
        try {
            T i = this.rawType.getDeclaredConstructor().newInstance();
            for (var column : this.ColumnNames) {
                var field = this.findDeclaredField(i.getClass(), column.GetName());
                if (field == null) {
                    throw new NoSuchFieldException("Field %s is not found".formatted(column.GetName()));
                }
                
                var res = column.ResultSetToRow(set);
                if (res == null) continue;
                field.set(i, res);
            }
            
            return i;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | NoSuchFieldException | SQLException e) {
            e.printStackTrace();
            assert false : e.getMessage();
        }
        return null;
    }

    /**
     * Create a Row object from a RowSet
     * @param set
     * @return a new Row Object
     */
    private void TrySetLastRowId(T row) {
        try {
            Column<?> autoIColumn = null;
            for (var i : this.ColumnNames) {
                if (i.HasAutoIncrement()) {
                    autoIColumn= i;
                    break;
                }
            }

            if (autoIColumn != null) {
                var lastrow = SQL.executeQuery("SELECT last_insert_rowid()");
                var field = this.findDeclaredField(row.getClass(), autoIColumn.GetName());
                if (field == null) return;
                field.set(row, lastrow.getObject(1));
            }
        } catch (IllegalAccessException | IllegalArgumentException | SQLException e) {
            e.printStackTrace();
            assert false : e.getMessage();
        }
    }

    /**
     * Only ran when assetion is enabled
     * U can enable assertion by adding VM argument -ea
     * 
     * @return
     */
    private boolean ColumnsValidation() {
        int autoincrements = 0;

        for (var col : this.ColumnNames) {
            if(col.IsPrimaryKey()) {
                if(col.HasAutoIncrement()) autoincrements ++;
                assert autoincrements < 2 : "Only 1 Primary key is allowed to have the AutoIncrement set! (%s)".formatted(col.GetName());
            } else {
                assert ! col.HasAutoIncrement() : "HasAutoIncrement is only allowed on Primary keys! (%s)".formatted(col.GetName());
            }
        }

        try {
            T i = this.rawType.getDeclaredConstructor().newInstance();
            for (var column : this.ColumnNames) {
                var field = this.findDeclaredField(i.getClass(), column.GetName());
                if (field == null) {
                    assert false : "Table Columns Didn't match Row class. On Column: (%s)".formatted(column.GetName());
                }
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            assert false : e.getMessage();
            return false;
        }

        return true;
    }

    private Field findDeclaredField(Class<?> type, String field) {
        try {
            return type.getDeclaredField(field);
        }
        catch (Exception e) {
            if (type.getSuperclass() != null) {
                return findDeclaredField(type.getSuperclass(), field);
            }
            assert false : e.getMessage();
            return null;
        }
    }
}
