package whackamole.whackamole.RS_new.Reward.Steps.Animation;

import java.util.List;

import org.bukkit.Location;

import whackamole.whackamole.RS_new.Reward.ValidationException;

public class Command {
    private Pixel[] pixels;
    
    public Command(List<Object> pixelData) throws ValidationException {
        this.pixels = new Pixel[pixelData.size()];

        for (int i = 0; i < pixelData.size(); i++) {
            this.pixels[i] = new Pixel((String) pixelData.get(i));
        }
    }

    public void render(Location center) {
        var angle = Math.toRadians(Location.normalizeYaw(360F - (center.getYaw()) + 180f));
        for (Pixel pixel : pixels) {
            pixel.render(center.clone(), angle);
        }
    }
}
