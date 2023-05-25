package whackamole.whackamole.DB.Model.Serializers;

import java.util.Optional;

import org.bukkit.Location;

import whackamole.whackamole.Logger;

public interface ISerializer<T extends Object> {

    /**
     * Returns the raw type the SQL saves
     * @return class of K
     */
    public Class<T> GetRawType();
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


    @SuppressWarnings("unchecked")
    public static<K> Optional<ISerializer<K>> GetSerializer(Class<K> from) {
        // * All Serializer are placed here
        if(from.isAssignableFrom(Location.class)) return Optional.of((ISerializer<K>) new LocationSerializer());

        // assert false : "Class: " +from.getName()+ " Is not found in ISerializer#GetSerializer(Class<K> formraw)";
        Logger.warning("Class: " +from.getName()+ " Is not found in ISerializer#GetSerializer(Class<K> formraw)");
        return Optional.empty();
    }
}
