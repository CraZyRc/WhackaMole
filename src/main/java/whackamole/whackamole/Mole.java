package whackamole.whackamole;

import java.util.EnumSet;

import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.signedness.qual.Unsigned;


public class Mole {

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
    private double maxY;
    private double minY;
    private double moveSpeed;


    public Mole(MoleType type, ArmorStand e, double moveSpeed) {
        this.moveSpeed = moveSpeed;
        this.maxY = e.getLocation().getY() + 1;
        this.minY = e.getLocation().getY();

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
                e.setInvisible(true);
                e.getEquipment().setHelmet(this.config.MOLE_SKULL);

                yield e;
            }
            case Jackpot -> {
                e.setGravity(false);
                e.setInvulnerable(false);
                e.setInvisible(true);
                e.getEquipment().setHelmet(this.config.JACKPOT_SKULL);

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
                        if(this.mole.getLocation().getY() < this.maxY)  {
                            this.move(this.moveSpeed);
                        }
                        else this.state = MoleState.MovingDown;
                    }
                    case MovingDown -> {
                        if(this.mole.getLocation().getY() > this.minY) {
                            this.move(-this.moveSpeed);
                        }
                        else this.state = MoleState.Missed;

                    }
                    case Hidden -> {
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

    public void unload() { try { this.mole.remove();} catch (Exception e) {}}

}
