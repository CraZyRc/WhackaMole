package whackamole.whackamole.DB.Model;

import org.jetbrains.annotations.NotNull;

public class Column {
    private String Name = "";
    private Object rawType;

    // * Options
    private boolean IsPrimayKey;
    private boolean HasAutoIncrement;
    private boolean AllowNull;

    /**
     * Creates a Sql Table Column
     * @param Name Column Name
     * @param type Java expected Type
     */
    public Column(String Name, Object type) {
        this.Name = Name;
        this.rawType = type;
    }

    /**
     * Returns the column type from the rawType
     * @return the sql column type (Ex. {@code TEXT})
     */
    private String GetType() {
        if(Integer.class.isInstance(this.rawType))   return "INTERGER";
        if(String.class.isInstance(this.rawType))    return "TEXT";
        if(Double.class.isInstance(this.rawType))    return "REAL";
        if(Long.class.isInstance(this.rawType))      return "REAL";
        throw new UnsupportedOperationException("Unknown Type received: %d".formatted(this.rawType.getClass().getName()));
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
        if(this.HasAutoIncrement) out += " AUTOINCREMENT";
        return out;
    }

    /**
     * Returns the column name with column type
     * @return The column name with type (Ex. {@code 'name TEXT'})
     */
    protected String GetCreateString() {
        return this.Name + " " + this.GetType();
    }

    // * Options
    /**
     * Tel the column if it is a Primary key for the Table
     * @param isKey
     * @return The column
     */
    public Column IsPrimaryKey(boolean isKey) {
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
    public Column HasAutoIncrement(boolean HasKey) {
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
    public Column AllowNull(boolean AllowKey) {
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
}
