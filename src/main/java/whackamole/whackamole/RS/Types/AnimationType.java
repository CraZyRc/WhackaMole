package whackamole.whackamole.RS.Types;


import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import whackamole.whackamole.Config;
import whackamole.whackamole.RS.AnimationCommand;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AnimationType implements IRewardInteractType { // TODO: Fix this class, currently doesn't work :)
    private boolean Enabled;
    private List<Entity> entities = new ArrayList<>();
    private Entity Interactable;
    private Particle Particle = org.bukkit.Particle.DUST;
    private List<AnimationCommand> Commands = new ArrayList<>();
    private YMLFile animationFile;
    private String Animation;
    private Location Loc;
    private int Duration;
    private int rewardChance;
    private int Threshold;


    private AnimationType(int threshold, int rewardChance, String animation, int duration, boolean enabled) {
        this.Threshold          = threshold;
        this.rewardChance       = rewardChance;
        this.Animation          = animation;
        this.Duration           = duration;
        this.animationFile      = new YMLFile(Config.AppConfig.storageFolder + "/animations/" + animation + ".yml");
        this.Enabled            = enabled;
    }

    public static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) {
        var rewardChance = (int) Settings.get("RewardChance");
        var animation = (String) Settings.get("Animation");
        var duration = (int) Settings.get("Duration");
        var enabled = (boolean) Settings.get("DisplayAnimation");

        return new AnimationType(threshold, rewardChance, animation, duration, enabled);
    }

    @Override
    public boolean Check() {
        if (this.Animation == null || this.Animation.isEmpty()) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_STRING.Format("Animation"));
            return false;
        } else if ( this.Duration <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Duration", "Duration"));
            return false;
        }  else if (this.rewardChance > 100 || this.rewardChance <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_REWARDCHANCE);
            return false;
        } else if (this.animationFile == null || this.animationFile.getList("Animation").isEmpty()) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_ANIMATIONFILE.Format(this.Animation));
            return false;
        } else {
            for (Object commandString : this.animationFile.getList("Animation")) {
                if (!AnimationCommand.Check((String) commandString, this.Animation)) {
                    return false;
                }
                this.Commands.add(new AnimationCommand((String) commandString));
            }
            return true;
        }
    }

    @Override
    public int getRewardChance() { return this.rewardChance; }

    @Override
    public int getThreshold() { return this.Threshold; }

    @Override
    public boolean getEnabled() { return this.Enabled; }

    @Override
    public int getTimer() { return this.Duration; }

    @Override
    public void Execute(RewardExecutorContext ctx) {
        if (this.Enabled) {
            this.displayType(ctx.plugin, ctx.location.clone());
        } else this.AfterExecute(ctx);
    }
    
    @Override
    public void TickExecute() {
        var angle = Math.toRadians(Location.normalizeYaw(360F - (this.Loc.getYaw()) + 180f));
        for (var CMD : this.Commands) {
            var particleV = new Vector(CMD.Delta1, CMD.Delta2, CMD.Delta3);
    
            particleV.rotateAroundY(angle);
            Location loc = this.Loc.clone().add(particleV);
            this.Loc.getWorld().spawnParticle(this.Particle, loc, CMD.Count, CMD.offSetX, CMD.offSetY, CMD.offSetZ, CMD.Speed, new Particle.DustOptions(Color.fromRGB(Math.round(CMD.colorRed * 255.0F), Math.round(CMD.colorGreen * 255.0F), Math.round(CMD.colorBlue * 255.0F)), CMD.Scale));
        }
    }

    @Override
    public void AfterExecute(RewardExecutorContext ctx) { this.Loc = ctx.location.clone(); }

    public void displayType(Plugin main, Location loc) {
        NamespacedKey namespacedKey = new NamespacedKey(main, "CurrencyDisplay");

        World world = loc.getWorld();


        final ItemDisplay display = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
        display.setRotation(loc.getYaw(), 0);
        display.setItemStack(Misc.getSkull("c5e313e30c53de176e7f3cfcc27827fd45e17d0c4b99c6c1fb52a70ab2939324"));
        display.setTransformation(new Transformation(new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f), new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f))); // Translation - leftrot - scale - rightrot
        display.setBillboard(Display.Billboard.FIXED);
        display.setCustomName(Misc.Color("&4Animation"));
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
