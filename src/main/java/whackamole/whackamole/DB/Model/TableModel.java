package whackamole.whackamole.DB.Model;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface TableModel<T extends RowModel> {
    
    /**
     * Returns the Table Name
     * 
     * @return The Table Name
     */
    @NotNull
    String GetName();

    /**
     * Executes the Create Query
     * 
     * @see Table#GetCreateQuery()
     */
    void Create();

    /**
     * Executes the Select Query
     * 
     * @return a List of row objects
     * @see Table#GetSelectQuery()
     */
    @NotNull
    List<T> Select();
    /**
     * Executes the Select Query with a where statement
     * 
     * @return a List of row objects
     * @see Table#GetSelectQuery(String WhereStatement)
     */
    @NotNull
    List<T> Select(String whereStatement, Object... whereValues);

    /**
     * Execute Insert query
     * 
     * @see Table#GetInsertQuery(Row row)
     */
    T Insert(T row);

    /**
     * Execute Update query
     * 
     * @see Table#GetUpdateQuery()
     */
    void Update(T row);
    /**
     * Execute Update query with Where statement
     * 
     * @see Table#GetUpdateQuery(T row, String WhereStatement)
     */
    void Update(T row, String whereStatement, Object... whereValues);

    /**
     * Executes the Delete query
     * 
     * @see Table#GetDeleteQuery()
     */
    void Delete(T row);
    /**
     * Executes the Delete query with Where statement
     * 
     * @see Table#GetDeleteQuery(String WhereStatement)
     */
    void Delete(String whereStatement, Object... whereValues);

}
