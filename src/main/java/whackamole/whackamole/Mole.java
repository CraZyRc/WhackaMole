package whackamole.whackamole;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class Mole {
    private Logger logger = Logger.getInstance();
    private Config config = Config.getInstance();
    public Entity mole;
    public int index = 0;

    public Mole(Entity e) {
        this.mole = e;
    }

    public boolean Update() {
        Location up = this.mole.getLocation().add(0, 0.05, 0);
        Location down = this.mole.getLocation().add(0, -0.05, 0);
        if (this.index <= 20) {
            this.mole.teleport(up);
        } else if (this.index > 20) {
            this.mole.teleport(down);
        }
        if (this.index >= 40) {
            this.mole.remove();
            return false;
        }
        this.index++;
        return true;
    }
}
