package whackamole.whackamole.DB.Model.Serializers;

public interface ISerializer<T extends Object> {

    /**
     * Returns the SQL DB Type
     * @return a string containing the SQL Type
     */
    public String GetType();

    /**
     * Turn's the Java object to the Sql equivalent type
     * @param object
     * @return the SQL data
     */
    public String Serialize(T object);

    /**
     * Converts the Sql data into the Java object
     * @param data
     * @return the Java Object
     */
    public T Deserialize(String data);
}
