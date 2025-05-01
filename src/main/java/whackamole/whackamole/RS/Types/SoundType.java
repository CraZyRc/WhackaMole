package whackamole.whackamole.RS.Types;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SoundType implements IRewardInteractType {
    private List<Entity> entities = new ArrayList<>();
    private Entity Interactable;
    private Sound Sound;
    private float Volume;
    private float Pitch;
    private int rewardChance;
    private int Threshold;

    private SoundType(int threshold, String soundName, float volume, float pitch, int rewardChance)
    {
        this.Threshold = threshold;
        this.Sound = org.bukkit.Sound.valueOf(soundName);
        this.Volume = volume;
        this.Pitch = pitch;
        this.rewardChance = rewardChance;
    }

    public static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) {
        var soundName = (String) Settings.get("Sound");
        var volume = Float.valueOf((String) Settings.get("Volume"));
        var pitch = Float.valueOf((String) Settings.get("Pitch"));
        var rewardChance = (int) Settings.get("RewardChance");
        
        return new SoundType(threshold, soundName, volume, pitch, rewardChance);
    }

    @Override
    public boolean Check() {
        if (this.Sound == null) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_STRING.Format("Sound"));
            return false;
        } else if (this.Volume <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Volume", "Volume"));
            return false;
        } else if (this.Pitch <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Pitch", "Pitch"));
            return false;
        } else if (this.rewardChance > 100 || this.rewardChance <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_REWARDCHANCE);
            return false;
        } else return true;
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
    public void Execute(RewardExecutorContext ctx) {
        this.displayType(ctx.plugin, ctx.location.clone());
    }

    @Override
    public void TickExecute() {}

    @Override
    public void AfterExecute(RewardExecutorContext ctx) {
        ctx.player.playSound(ctx.player, this.Sound, this.Volume, this.Pitch);
    }

    public void displayType(Plugin main, Location loc) {
        NamespacedKey namespacedKey = new NamespacedKey(main, "SoundDisplay");

        World world = loc.getWorld();


        final ItemDisplay display = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
        display.setRotation(loc.getYaw(), 0);
        display.setItemStack(Misc.getSkull("e82b0b7c68e88800030e674522aab40396fa543072b949c0600e66c2ed352ff0"));
        display.setTransformation(new Transformation(new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f), new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f))); // Translation - leftrot - scale - rightrot
        display.setBillboard(Display.Billboard.FIXED);
        display.setCustomName(Misc.Color("&eSound"));
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
