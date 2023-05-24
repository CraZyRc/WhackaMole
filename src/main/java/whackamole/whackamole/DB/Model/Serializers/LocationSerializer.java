package whackamole.whackamole.DB.Model.Serializers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer implements ISerializer<Location> {

    @Override
    public String GetType() {
        return "NVARCHAR";
    }

    @Override
    public String Serialize(Location object) {
        var data = FromLocation(object);
        return String.format("'%s'", data);
    }

    @Override
    public Location Deserialize(String data) {
        var loc = FromString(data);
        return loc;
    }

    private Location FromString(String data) {
        if (data == "NULL") {
            return null;
        }

        Map<String, Object> object = new HashMap<>();
        var items = data.split(",");
        assert items.length == 6 : "SQL Data is not complete: " + data;

        var world = Bukkit.getWorld(items[0]);
        object.put("x",     items[1]);
        object.put("y",     items[2]);
        object.put("z",     items[3]);
        object.put("yaw",   items[4]);
        object.put("pitch", items[5]);
        var loc = Location.deserialize(object);
        loc.setWorld(world);

        return loc;
    }

    private static String FromLocation(Location loc) {
        if(loc == null) {
            return "NULL";
        }
        var builder = new StringBuilder();

        builder.append(loc.getWorld().getName());
        builder.append(',');
        builder.append(loc.getX());
        builder.append(',');
        builder.append(loc.getY());
        builder.append(',');
        builder.append(loc.getZ());
        builder.append(',');
        builder.append(loc.getYaw());
        builder.append(',');
        builder.append(loc.getPitch());

        return builder.toString();
    }

    
}
