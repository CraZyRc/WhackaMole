package whackamole.whackamole.DB.Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public class Column<T> {
    private String Name = "";
    private Class<T> rawType;

    // * Options
    private boolean IsPrimayKey;
    private boolean HasAutoIncrement;
    private boolean AllowNull;
    private boolean IsUnique;

    /**
     * Creates a Sql Table Column
     * @param Name Column Name
     * @param type Java expected Type
     */
    public Column(String Name, Class<T> type) {
        this.Name = Name;
        this.rawType = type;
    }

    /**
     * Returns the column type from the rawType
     * @return the sql column type (Ex. {@code TEXT})
     */
    private String GetType() {
        String out = "";
        if(this.rawType.isAssignableFrom(Integer.class))    out += "INTEGER";
        if(this.rawType.isAssignableFrom(String.class))     out += "TEXT";
        if(this.rawType.isAssignableFrom(UUID.class))       out += "TEXT";
        if(this.rawType.isAssignableFrom(Double.class))     out += "REAL";
        if(this.rawType.isAssignableFrom(Long.class))       out += "REAL";
        assert ! out.isEmpty() : "Unable to identify Column Type: (%s) for method Column#GetType()".formatted(this.rawType);

        if(!this.AllowNull)         out += " NOT NULL";
        if(this.IsUnique)           out += " UNIQUE";
        return out;
    }

    /**
     * Returns the Column name
     * @return the column name
     */
    @NotNull
    protected String GetName() {
        return this.Name;
    }

    /**
     * Returns the Primary key + any options
     * @return The Primary key with all options or a empty string if Column is not a Primary key.
     * 
     */
    @NotNull
    protected String GetPrimaryWithOptions() {
        if (! this.IsPrimayKey) return "";

        String out = this.Name;
        if(this.HasAutoIncrement)   out += " AUTOINCREMENT";
        return out;
    }

    /**
     * Returns the column name with column type
     * @return The column name with type (Ex. {@code 'name TEXT'})
     */
    protected String GetCreateString() {
        return this.Name + " " + this.GetType();
    }

    /**
     * Casts RowSet Column to column type of T
     * @param set
     * @return Casted RowSet to T
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    protected T ResultSetToRow(ResultSet set) throws SQLException {
        Object V = set.getObject(Name);
        if(V.equals("null")) return null;
        
        if(this.rawType.isAssignableFrom(Integer.class))    return (T) Integer.valueOf(set.getInt(Name));
        if(this.rawType.isAssignableFrom(String.class))     return (T) String.valueOf(set.getString(Name));
        if(this.rawType.isAssignableFrom(Double.class))     return (T) Double.valueOf(set.getDouble(Name));
        if(this.rawType.isAssignableFrom(UUID.class))       return (T) UUID.fromString(set.getString(Name));
        if(this.rawType.isAssignableFrom(Long.class))       return (T) Long.valueOf(set.getLong(Name));
        assert false : "Unable to identify Column Type: (%s) for method Column#ResultSetToRow(ResultSet set)".formatted(this.rawType);

        return null;
    }

    // * Options
    /**
     * Tel the column if it is a Primary key for the Table
     * @param isKey
     * @return The column
     */
    public Column<T> IsPrimaryKey(boolean isKey) {
        this.IsPrimayKey = isKey;
        return this;
    }

    /**
     * Get the Primary key option
     * 
     * @return The primary key option
     */
    protected boolean IsPrimaryKey() {
        return this.IsPrimayKey;
    }

    /**
     * Set the AutoIncrement option
     * 
     * @param HasKey
     * @return The column
     */
    public Column<T> HasAutoIncrement(boolean HasKey) {
        this.HasAutoIncrement = HasKey;
        return this;
    }

    /**
     * Get the autoIncrement option
     * 
     * @return The is autoincrement option
     */
    protected boolean HasAutoIncrement() {
        return this.HasAutoIncrement;
    }
    
    /**
     * Set the AutoIncrement option
     * 
     * @param HasKey
     * @return The column
     */
    public Column<T> AllowNull(boolean AllowKey) {
        this.AllowNull = AllowKey;
        return this;
    }

    /**
     * Get the autoIncrement option
     * 
     * @return The is autoincrement option
     */
    protected boolean AllowNull() {
        return this.AllowNull;
    }
    
    /**
     * Set the Unique option
     * 
     * @param IsKey
     * @return The column
     */
    public Column<T> IsUnique(boolean IsKey) {
        this.IsUnique = IsKey;
        return this;
    }

    /**
     * Get the Unique option
     * 
     * @return The is Unique option
     */
    protected boolean IsUnique() {
        return this.IsUnique;
    }
}
