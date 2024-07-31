package whackamole.whackamole.RS.Types;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import whackamole.whackamole.Main;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CurrencyType implements IRewardInteractType {
    private List<Entity> entities = new ArrayList<>();
    private Entity Interactable;
    private int quantity;
    private int rewardChance;
    private int Threshold;
    private Econ econ = new Econ();

    private CurrencyType(int threshold, int quantity, int rewardChance)
    {
        this.Threshold = threshold;
        this.quantity = quantity;
        this.rewardChance = rewardChance;
    }

    public static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) {
        var quantity = (int) Settings.get("Quantity");
        var rewardChance = (int) Settings.get("RewardChance");
        
        return new CurrencyType(threshold, quantity, rewardChance);
    }

    @Override
    public boolean Check() {
        if (this.quantity <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Quantity","Quantity"));
            return false;
        } else if (this.rewardChance > 100 || this.rewardChance <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_REWARDCHANCE);
            return false;
        } else return true;
    }

    @Override
    public int getRewardChance() { return this.rewardChance; }

    @Override
    public int getThreshold() { return this.Threshold; }

    @Override
    public int getTimer() { return 5; }

    @Override
    public void Execute(Player player) {
        this.econ.depositPlayer(player, this.quantity);
        RewardsManager.sendScoreToPlayer(player, this.quantity);
    }

    @Override
    public void displayType(Main main, Location loc) {
        NamespacedKey namespacedKey = new NamespacedKey(main, "CurrencyDisplay");

        World world = loc.getWorld();


        final ItemDisplay display = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
        display.setRotation(loc.getYaw(), 0);
        display.setItemStack(Misc.getSkull("ebda5f31937b2ff755271d97f01be84d52a407b36ca77451856162ac6cfbb34f"));
        display.setTransformation(new Transformation(new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f), new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f))); // Translation - leftrot - scale - rightrot
        display.setBillboard(Display.Billboard.FIXED);
        display.setCustomName(Misc.Color("&6Currency"));
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
        if (this.Interactable != null) {
            return this.Interactable;
        } else return null;
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
