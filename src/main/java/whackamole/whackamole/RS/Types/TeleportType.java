package whackamole.whackamole.RS.Types;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import whackamole.whackamole.Main;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TeleportType implements IRewardInteractType {
    private List<Entity> entities = new ArrayList<>();
    private Entity Interactable;
    private Location Loc;
    private World World;
    private double X;
    private double Y;
    private double Z;
    private int rewardChance;
    private int Threshold;

    private TeleportType(int threshold, String worldName, double X, double Y, double Z, int rewardChance)
    {
        this.Threshold = threshold;
        this.World = Bukkit.getWorld(worldName);
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.rewardChance = rewardChance;
    }

    public static IRewardType Load( int threshold, LinkedHashMap<String, ?> Settings) {
        var worldName =(String) Settings.get("World");
        var X = (double) Settings.get("X");
        var Y = (double) Settings.get("Y");
        var Z = (double) Settings.get("Z");
        var rewardChance = (int) Settings.get("RewardChance");

        return new TeleportType(threshold, worldName, X, Y, Z, rewardChance);
    }

    @Override
    public boolean Check() {
        this.Loc = new Location(World, X, Y, Z);

        if (World == null) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_STRING.Format("World"));
            return false;
        } else if (this.rewardChance > 100 || this.rewardChance <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_REWARDCHANCE);
            return false;
        }


        Location feet = this.Loc.clone();
        if (!feet.getBlock().getType().isTransparent() && !feet.add(0, 1, 0).getBlock().getType().isTransparent()) {
            Logger.error(Translator.REWARDS_TYPE_UNSAFETPLOCATION);
            return false; // block not transparent (will suffocate)
        }

        Location head = feet.add(0, 1, 0);

        if (!head.getBlock().getType().isTransparent()) {
            Logger.error(Translator.REWARDS_TYPE_UNSAFETPLOCATION);
            return false; // block not transparent (will suffocate)
        }

        return true;
    }

    @Override
    public int getRewardChance() {
        return this.rewardChance;
    }

    @Override
    public int getThreshold() { return this.Threshold; }

    @Override
    public int getTimer() { return 5; }

    @Override
    public void Execute(Player player) {
        player.teleport(this.Loc);
    }

    @Override
    public void displayType(Main main, Location loc) {
        NamespacedKey namespacedKey = new NamespacedKey(main, "TeleportDisplay");

        World world = loc.getWorld();


        final ItemDisplay display = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
        display.setRotation(loc.getYaw(), 0);
        display.setItemStack(Misc.getSkull("f41f1ef439f91069a43678d227ad458d663ec04363bce9c7c019c5679e8cf004"));
        display.setTransformation(new Transformation(new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f), new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f))); // Translation - leftrot - scale - rightrot
        display.setBillboard(Display.Billboard.FIXED);
        display.setCustomName(Misc.Color("&bTeleport"));
        display.setCustomNameVisible(true);
        display.setPersistent(true);
        display.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 1);

        final Interaction interaction = (Interaction) world.spawnEntity(loc.subtract(0, 0.48, 0), EntityType.INTERACTION);
        this.Interactable = interaction;
        interaction.setInteractionWidth(0.5F);
        interaction.setInteractionHeight(0.5F);
        interaction.setResponsive(true);
        interaction.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 1);


        BukkitScheduler schedular = Bukkit.getScheduler();
        schedular.runTaskLater(main, () -> {
            this.transformDisplay(display);
        }, 10L);

        entities.add(display);
        entities.add(interaction);

    }

    @Override
    public void Remove(Player player) {
        Location loc = null;
        for (Entity e : entities) {
            loc = e.getLocation();
            e.remove();
        }
        player.getWorld().spawnParticle(Particle.COMPOSTER , loc, 2);
        player.getWorld().playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
    }

    @Override
    public Entity getInteractable() {
        return this.Interactable;
    }

    private void transformDisplay(ItemDisplay display) {
        Transformation transformation = display.getTransformation();
        display.setInterpolationDelay(0);
        display.setInterpolationDuration(15);
        transformation.getTranslation().set(0f, 0f, 0f);
        transformation.getLeftRotation().set(0f, 1f, 0f, 0f);
        transformation.getScale().set(0.9f, 0.9f, 0.9f);
        transformation.getRightRotation().set(0f,1f,0f,0f);
        display.setTransformation(transformation);
    }
}
