package whackamole.whackamole.RS.Reward.Steps;

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

import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.Reward.ValidationException;
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
            case Item: return;
            case Teleport: return;
            case Sound: return;
            case Effect: return;
            case Currency: return;
            case Animation: return;
            default: throw new ValidationException(Translator.REWARDS_INVALIDREWARDTYPE); // TODO: Translator message: Use Interact is not allowed for type X
        }
    }

    private ItemStack getSkull() {
        switch (this.type) {
            case Invalid:   return null;
            case Message:   return Misc.getSkull("a988419dd5b386f698a96913db1d97c2418e16d416d7f439d48acd41e3a436ce");
            case Item:      return Misc.getSkull("1ff041976a09dd053e3d1d4e611aac09594d74fc71a0ec4da0110416d317dba8");
            case Teleport:  return Misc.getSkull("f41f1ef439f91069a43678d227ad458d663ec04363bce9c7c019c5679e8cf004");
            case Sound:     return Misc.getSkull("e82b0b7c68e88800030e674522aab40396fa543072b949c0600e66c2ed352ff0");
            case Effect:    return Misc.getSkull("6d4ead1efe0cf776015bfd2c236ddf7c0d3309eb599b39d6ac10d7fa11a3e075");
            case Currency:  return Misc.getSkull("ebda5f31937b2ff755271d97f01be84d52a407b36ca77451856162ac6cfbb34f");
            case Animation: return Misc.getSkull("c5e313e30c53de176e7f3cfcc27827fd45e17d0c4b99c6c1fb52a70ab2939324");
        }
        return null;
    }

    private Display getDisplay(Location location) {
        if (this.key == null) return null;

        var display = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        display.setRotation(location.getYaw(), 0);
        display.setItemStack(this.getSkull());
        display.setTransformation(
            new Transformation(
                new Vector3f(0f, 0f, 0f),           // Translation
                new AxisAngle4f(0f, 0f, 0f, 1f),    // Left Rotation
                new Vector3f(0f, 0f, 0f),           // Scale
                new AxisAngle4f(0f, 0f, 0f, 1f)));  // Right Rotation
        
        display.setBillboard(Display.Billboard.FIXED);
        display.setCustomName(Misc.Color("&f" + this.type.toString()));
        display.setCustomNameVisible(true);
        display.setPersistent(true);
        display.getPersistentDataContainer().set(this.key, PersistentDataType.INTEGER, 1);

        return display;
    }

    private Interaction getInteraction(Location location) {
        if (this.key == null) return null;

        var interaction = (Interaction) location.getWorld().spawnEntity(location.clone().subtract(0, 0.48, 0), EntityType.INTERACTION);
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
