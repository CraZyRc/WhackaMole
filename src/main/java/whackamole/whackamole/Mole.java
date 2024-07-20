package whackamole.whackamole;

import java.util.EnumSet;
import java.util.UUID;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.GS.Settings;
import whackamole.whackamole.Utils.Misc;

public class Mole {
    public MoleType type;

    public enum MoleType {
        Null,
        Mole,
        Jackpot
    }

    public MoleState state = MoleState.MovingUp;

    public enum MoleState {
        Hidden,
        MovingUp,
        MovingDown,
        Missed,
        Hit
    }

    private final EnumSet<MoleState> movingState = EnumSet.of(MoleState.MovingUp, MoleState.MovingDown);

    public Entity mole;
    private double minY;
    private double maxY;
    private double moveSpeed;

    public Mole(MoleType type, ArmorStand e, double moveSpeed, Settings settings) {
        this.moveSpeed = moveSpeed;
        this.maxY = e.getLocation().getY() + 1;
        this.minY = e.getLocation().getY();

        this.type = type;
        this.mole = switch (type) {
            case Null -> e;
            case Mole -> {
                e.addScoreboardTag("Mole_new");
                e.setGravity(false);
                e.setInvisible(true);
                e.getEquipment().setHelmet(Misc.getSkull(settings.moleHead));
                e.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);

                yield e;
            }
            case Jackpot -> {
                e.addScoreboardTag("Mole_new");
                e.setGravity(false);
                e.setInvisible(true);
                e.getEquipment().setHelmet(Misc.getSkull(settings.jackpotHead));
                e.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);

                yield e;
            }
        };
    }

    public boolean update() {
        switch (this.type) {
            case Null -> {
            }
            case Mole, Jackpot -> {
                switch (this.state) {
                    case Hit, Missed -> {
                        this.state = MoleState.Hidden;
                        this.unload();
                    }
                    case MovingUp -> {
                        if (this.mole.getLocation().getY() < this.maxY) {
                            this.move(this.moveSpeed);
                        } else
                            this.state = MoleState.MovingDown;
                    }
                    case MovingDown -> {
                        if (this.mole.getLocation().getY() > this.minY) {
                            this.move(-this.moveSpeed);
                        } else
                            this.state = MoleState.Missed;

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

    public boolean isMoving() {
        return this.movingState.contains(this.state);
    }

    public boolean equals(Entity e) {
        return this.mole == e;
    }

    public void unload() {
        try {
            this.mole.remove();
        } catch (Exception e) {
        }
    }

}
