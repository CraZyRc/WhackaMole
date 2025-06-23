package whackamole.whackamole.RS_new.Reward.Steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import whackamole.whackamole.RS_new.RewardExecutorContext;
import whackamole.whackamole.RS_new.Reward.ValidationException;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

public class InteractRewardStep extends RewardStep implements IRewardStepInteractable {

    private StepType type;
    private List<Entity> entities;
    private NamespacedKey key;
    private Interaction interactable;

    public InteractRewardStep(StepType type, Map<String, ?> settings) {
        this.type = type;
    }
    
    @Override
    public void Validate() throws ValidationException {
        switch (this.type) {
            case Message: return;
            default: throw new ValidationException(Translator.REWARDS_INVALIDREWARDTYPE); // TODO: Translator message: Use Interact is not allowed for type X
        }
    }

    private ItemStack getSkull() {
        switch (this.type) {
            case Invalid: return null;
            case Message: return Misc.getSkull("a988419dd5b386f698a96913db1d97c2418e16d416d7f439d48acd41e3a436ce");
        }
        return null;
    }

    private Display getDisplay(Location location) {
        if (this.key == null) return null;

        var display = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        display.setRotation(location.getYaw(), 0);
        display.setItemStack(this.getSkull());
        display.setTransformation(new Transformation(new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f), new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f))); // Translation - leftrot - scale - rightrot
        display.setBillboard(Display.Billboard.FIXED);
        display.setCustomName(Misc.Color("&fMessage"));
        display.setCustomNameVisible(true);
        display.setPersistent(true);
        display.getPersistentDataContainer().set(this.key, PersistentDataType.INTEGER, 1);

        return display;
    }

    private Interaction getInteraction(Location location) {
        if (this.key == null) return null;

        var interaction = (Interaction) location.getWorld().spawnEntity(location.subtract(0, 0.48, 0), EntityType.INTERACTION);
        interaction.setInteractionWidth(0.5F);
        interaction.setInteractionHeight(0.5F);
        interaction.setResponsive(true);
        interaction.getPersistentDataContainer().set(this.key, PersistentDataType.INTEGER, 1);
        
        return interaction;
    }
    
    @Override
    public int getTimer() {
        return 5;
    }

    @Override
    public Interaction getInteractable() {
        return this.interactable;
    }

    @Override
    public void Execute(RewardExecutorContext context) {
        this.entities = new ArrayList<>();
        this.key = new NamespacedKey(context.plugin, "MessageDisplay");

        this.interactable = this.getInteraction(context.location);
        entities.add(this.interactable);
    }

    @Override
    public void ExecuteTick(RewardExecutorContext context, long ticksPassed) {
        if (ticksPassed == 10L) {
            var display = this.getDisplay(context.location);
            entities.add(display);
            Transformation transformation = display.getTransformation();
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(15);
            transformation.getTranslation().set(0f, 0f, 0f);
            transformation.getLeftRotation().set(0f, 1f, 0f, 0f);
            transformation.getScale().set(0.9f, 0.9f, 0.9f);
            transformation.getRightRotation().set(0f, 1f, 0f, 0f);
            display.setTransformation(transformation);
        }
    }

    @Override
    public void ExecuteAfter(RewardExecutorContext context) {
        Location loc = null;
        for (Entity entity : this.entities) {
            loc = entity.getLocation();
            entity.remove();
        }

        context.player.getWorld().spawnParticle(Particle.COMPOSTER, loc, 2);
        context.player.getWorld().playSound(context.player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
    }
}
