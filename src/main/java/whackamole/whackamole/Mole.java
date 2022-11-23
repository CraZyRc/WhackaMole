package whackamole.whackamole;

import java.util.EnumSet;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.signedness.qual.Unsigned;


public class Mole {
    
    private Logger logger = Logger.getInstance();
    private Config config = Config.getInstance();
    
    public MoleType type;
    public enum MoleType {
        Debug,
        Mole,
        Jackpot
    };

    public MoleState state = MoleState.MovingUp; 
    public enum MoleState {
        Hidden,
        MovingUp,
        MovingDown,
        Missed,
        Hit
    }
    
    private EnumSet<MoleState> movingState = EnumSet.of(MoleState.MovingUp, MoleState.MovingDown);


    public Entity mole;
    
    public int index = 0;

    public Mole(MoleType type, ArmorStand e) {
        this.type = type;
        this.mole = switch(type) {
            case Debug -> {
                e.setGravity(false);
                e.setInvulnerable(true);
                Mole.move(e, 1.5);

                yield e;
            }
            case Mole -> {
                e.setGravity(false);
                e.setInvulnerable(false);
                e.getEquipment().setHelmet(this.config.MOLE_SKULL);

                yield e;
            }
            case Jackpot -> {
                e.setGravity(false);
                e.setInvulnerable(false);
                e.getEquipment().setHelmet(this.config.MOLE_SKULL);
                
                yield e;
            }
        };
    }

    public boolean update() {
        switch (this.type) {
            case Debug -> {}
            case Mole, Jackpot -> {
                switch (this.state) {
                    case Hit, Missed -> {
                        this.state = MoleState.Hidden;
                        // ? Or Reset moles position
                        this.unload();
                    }
                    case MovingUp -> {
                        if(this.index <= 20)  {
                            this.move(0.05);
                            this.index++;
                        }
                        else this.state = MoleState.MovingDown;
                    }
                    case MovingDown -> {
                        if(this.index <= 40) {
                            this.move(-0.05);
                            this.index++;
                        }
                        else this.state = MoleState.Missed;
                    }
                    case Hidden -> {
                        this.index = 0;
                        return false;
                    }
                }
            } 
        }
        return true;
    }

    private void move(double y) {
        this.mole.teleport(this.mole.getLocation().add(0, y, 0));
    }
    private static void move(Entity e, double y) {
        e.teleport(e.getLocation().add(0, y, 0));
    }

    public boolean isMoving() {
        return this.movingState.contains(this.state);
    }

    public boolean equals(Entity e) {
        return this.mole == e;
    }

    public void unload() {
        this.mole.remove();
    }

    @Override
    protected void finalize() throws Throwable {
        this.logger.info("Deleting mole with type: " + this.type);
        this.unload();
    }
}
