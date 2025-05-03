package whackamole.whackamole.RS.Types;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import whackamole.whackamole.Config;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MessageType implements IRewardInteractType {
    private boolean Enabled;
    private List<Entity> entities = new ArrayList<>();
    private Entity Interactable;
    private String Message;
    private String messageType;
    private int rewardChance;
    private int threshold;

    private MessageType(int threshold, String messageType, String message, int rewardChance, boolean enabled)
    {
        this.threshold      = threshold;
        this.messageType    = messageType;
        this.Message        = message;
        this.rewardChance   = rewardChance;
        this.Enabled        = enabled;
    }

    public static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) {
        var messageType = (String) Settings.get("MessageType");
        var message = (String) Settings.get("Message");
        var rewardChance = (int) Settings.get("RewardChance");
        var enabled = (boolean) Settings.get("DisplayAnimation");
        
        return new MessageType(threshold, messageType, message, rewardChance, enabled);
    }

    @Override
    public boolean Check() {
        if (!messageType.equals("String") && !messageType.equals("Json")) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_MESSAGETYPE);
            return false;
        } else if (this.Message == null || this.Message.isEmpty()) {
            Logger.error(Translator.REWARDS_TYPE_NOMESSAGESET);
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
    public int getThreshold() { return this.threshold; }

    @Override
    public boolean getEnabled() { return this.Enabled; }

    @Override
    public int getTimer() { return 5; }

    @Override
    public void Execute(RewardExecutorContext ctx) {
        if (this.Enabled) {
            this.displayType(ctx.plugin, ctx.location.clone());
        } else this.AfterExecute(ctx);
    }

    @Override
    public void TickExecute() {}

    @Override
    public void AfterExecute(RewardExecutorContext ctx) {
        if (this.messageType.equals("String")) {
            ctx.player.sendMessage(Config.AppConfig.PREFIX + Misc.Color(this.Message));
        } else if (this.messageType.equals("Json")) {
            BaseComponent[] list = ComponentSerializer.parse(this.Message);
            BaseComponent[] sendList = new ComponentBuilder(Config.AppConfig.PREFIX).append(list).create();
            ctx.player.spigot().sendMessage(ChatMessageType.CHAT, sendList);
        }
    }

    public void displayType(Plugin main, Location loc) {
        NamespacedKey namespacedKey = new NamespacedKey(main, "MessageDisplay");

        World world = loc.getWorld();


        final ItemDisplay display = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
        display.setRotation(loc.getYaw(), 0);
        display.setItemStack(Misc.getSkull("a988419dd5b386f698a96913db1d97c2418e16d416d7f439d48acd41e3a436ce"));
        display.setTransformation(new Transformation(new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f), new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f))); // Translation - leftrot - scale - rightrot
        display.setBillboard(Display.Billboard.FIXED);
        display.setCustomName(Misc.Color("&fMessage"));
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
