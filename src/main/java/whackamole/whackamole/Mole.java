package whackamole.whackamole;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class Mole {
    private Logger logger = Logger.getInstance();
    private Config config = Config.getInstance();

    private GameHandler game;
    public Entity mole;
    
    public int index = 0;
    public boolean hit = false;

    public Mole(GameHandler game, Entity e) {
        this.game = game;
        this.mole = e;
    }

    public boolean Update() {
        Location up = this.mole.getLocation().add(0, 0.05, 0);
        Location down = this.mole.getLocation().add(0, -0.05, 0);
        if (game.gameLost) {
            this.mole.remove();
            return false;
        } else if (this.index <= 20) {
            this.mole.teleport(up);
        } else if (this.index > 20) {
            this.mole.teleport(down);
        } if (this.index >= 40) {
            this.mole.remove();
            game.moleMissed++;
            return false;
        } if (hit == true) {
            this.mole.remove();
            this.hit = false;
            return false;
        }
        this.index++;
        return true;
    }
}
