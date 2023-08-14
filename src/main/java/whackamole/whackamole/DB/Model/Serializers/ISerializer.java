package whackamole.whackamole.DB.Model.Serializers;

import java.time.LocalDateTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Location;

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
     * @param ResultSet set the current result set
     * @param String name the column name to get from the set
     * @return the Java Object
     */
    public  T Deserialize(ResultSet set, String name) throws SQLException;

    /**
     * Serializer Factory. It returns the Associated Serializer class from Class<K>
     * from
     * 
     * @param Class<K> the type to get the Serializer from.
     * @return the Associated Serializer or a Exception
     * @throws UnSupportedOperationException when no associated class if found for
     *                                       type
     */
    @SuppressWarnings("unchecked")
    public static<K> ISerializer<K> GetSerializer(Class<K> from) {
        // * All Serializer are placed here
        if(from.isAssignableFrom(Location.class))       return (ISerializer<K>) new LocationSerializer();
        if(from.isAssignableFrom(String.class))         return (ISerializer<K>) new StringSerializer();
        if(from.isAssignableFrom(Integer.class))        return (ISerializer<K>) new IntegerSerializer();
        if(from.isAssignableFrom(Boolean.class))        return (ISerializer<K>) new BooleanSerializer();
        if(from.isAssignableFrom(Double.class))         return (ISerializer<K>) new DoubleSerializer();
        if(from.isAssignableFrom(Long.class))           return (ISerializer<K>) new LongSerializer();
        if(from.isAssignableFrom(UUID.class))           return (ISerializer<K>) new UUIDSerializer();
        if(from.isAssignableFrom(LocalDateTime.class))  return (ISerializer<K>) new DateSerializer();

        throw new UnsupportedOperationException("Class: " +from.getName()+ " Is not found in ISerializer#GetSerializer(Class<K> form raw)");
    }
}
