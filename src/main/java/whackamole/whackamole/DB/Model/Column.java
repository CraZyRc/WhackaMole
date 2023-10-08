package whackamole.whackamole.DB.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;

import whackamole.whackamole.DB.Model.Serializers.ISerializer;

public class Column<T> {
    private final String Name;
    
    private final ISerializer<T> Serializable;

    // * Options
    private boolean IsPrimayKey = false;
    private boolean HasAutoIncrement = false;
    private boolean AllowNull = true;
    private boolean IsUnique = false;
    private T DefaultValue;
    private String BuildInDefault = "";

    /**
     * Creates a Sql Table Column
     * @param Name Column Name
     * @param type Java expected Type
     */
    public Column(String Name, Class<T> type) {
        this.Name = Name;
        this.Serializable = ISerializer.GetSerializer(type);
    }

    /**
     * Returns the column type from the rawType
     * @return the sql column type (Ex. {@code TEXT})
     */
    private String GetType() {
        String out = "";
        out += Serializable.GetType();

        if(!this.AllowNull)                         out += " NOT NULL";
        if(this.IsUnique)                           out += " UNIQUE";
        if(this.DefaultValue != null)               out += " DEFAULT %s".formatted(this.ValueToQueryValue(this.DefaultValue));
        else if (! this.BuildInDefault.isEmpty())   out += " DEFAULT %s".formatted(this.BuildInDefault);
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
     * Casts Java value to query value
     * @param set
     * @return query value string
     */
    @SuppressWarnings("unchecked")
    protected String ValueToQueryValue(Object object) {
        if (object == null) return "NULL";
        return Serializable.Serialize((T) object);
    }
    
    /**
     * Casts RowSet Column to column type of T
     * @param set
     * @return Casted RowSet to T
     * @throws SQLException
     */
    protected T ResultSetToRow(ResultSet set) throws SQLException {
        Object V = set.getObject(Name);
        if(V == null) return null;
        return Serializable.Deserialize(set, Name);
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

    /**
     * Sets the default value for the collumn
     * 
     * @param value
     * @return The column
     */
    public Column<T> Default(T value) {
        this.DefaultValue = value;
        return this;
    }

    /**
     * Gets teh default value for the collumn
     * 
     * @return The default value for the collumn or null
     */
    protected T Default() {
        return this.DefaultValue;
    }

    /**
     * Sets a SQL default value for a column ex: 'CURRENT_TIMESTAMP'
     * 
     * @param value
     * @return The column
     */
    public Column<T> BuildInDefault(String value) {
        this.BuildInDefault = value;
        return this;
    }

    /**
     * Gets the default value for the collumn
     * 
     * @return The default value for the collumn or null
     */
    protected String BuildInDefault() {
        return this.BuildInDefault;
    }

    /**
     * Get if the column has a default defined
     * @return if the default is present for this column
     */
    protected boolean HasDefault() {
        return this.DefaultValue != null || ! this.BuildInDefault.isEmpty();
    }

}
