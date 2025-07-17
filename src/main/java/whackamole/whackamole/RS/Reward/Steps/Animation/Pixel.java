package whackamole.whackamole.RS.Reward.Steps.Animation;


import java.util.Arrays;
import java.util.NoSuchElementException;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import whackamole.whackamole.RS.Reward.ValidationException;

class Pixel {
    private static Particle particle = Particle.DUST;
    private Color color;
    private Float scale;
    private Vector delta;
    private Vector offset;
    private Float speed;
    private int count;

    protected Pixel(String frameString) throws ValidationException {
        if (! frameString.contains(" ")) {
            throw new ValidationException("Command must not contain empty rows");
        }
        var components = Arrays.asList(frameString.split(" ")).iterator();
        try {
            this.color  = this.parseColor(components.next(), components.next(), components.next());
            this.scale  = Float.valueOf(components.next());
            this.delta  = this.parseVec(components.next(), components.next(), components.next());
            this.offset = this.parseVec(components.next(), components.next(), components.next());
            this.speed  = Float.valueOf(components.next());
            this.count  = Integer.valueOf(components.next()); 
        } catch(NoSuchElementException e) {
            throw new ValidationException("Command is missing items");
        } catch (NumberFormatException e) {
            throw new ValidationException("Command format is invalid");
        }
    }

    protected void render(Location center, double angle) {
        var location = center.add(this.delta.clone().rotateAroundY(angle));
        var options = new Particle.DustOptions(color, this.scale);
        location.getWorld().spawnParticle(particle, 
                                            location, 
                                            this.count,
                                            this.offset.getX(),
                                            this.offset.getY(),
                                            this.offset.getZ(),
                                            this.speed,
                                            options
                                            ); 
    }

    Vector parseVec(String a, String b, String c) throws NumberFormatException {
        var x = Float.valueOf(a);
        var y = Float.valueOf(b);
        var z = Float.valueOf(c);

        return new Vector(x, y, z);
    }

    Color parseColor(String x, String y, String z) {
        var r = Float.valueOf(x);
        var g = Float.valueOf(y);
        var b = Float.valueOf(z);

        return Color.fromRGB(Math.round(r*255.0F), Math.round(g*255.0F), Math.round(b*255.0F));
    }
}
