package whackamole.whackamole.RS.Types;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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

public class ItemType implements IRewardType {
    private List<Entity> entities = new ArrayList<>();
    private Material material;
    private int amount;
    public int rewardChance = 0;
    private String nbt;
    public int threshold;

    private ItemType(int threshold, String materialName, int amount, String nbt, int rewardChance)
    {
        this.threshold = threshold;
        this.material = Material.matchMaterial(materialName);
        this.amount = amount;
        this.nbt = nbt;
        this.rewardChance = rewardChance;
    }

    public static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) {
        var materialName = (String) Settings.get("Material");
        var amount = (int) Settings.get("Amount");
        var nbt = (String) Settings.get("NBT");
        var rewardChance = (int) Settings.get("RewardChance");
        return new ItemType(threshold, materialName, amount, nbt, rewardChance);
    }

    @Override
    public boolean Check() {
        if (material == null) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_STRING.Format("Material"));
            return false;
        } else if (this.amount <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Amount", "Amount"));
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
    public void Execute(Player player) {
        ItemStack Item = new ItemStack(this.material, this.amount);
        if (!this.nbt.isEmpty()) {
            String nbtTags = this.nbt.replace("[","").replace("]","");
            Item = Bukkit.getUnsafe().modifyItemStack(Item, this.material.getKey().getKey() + "[" + nbtTags + "]"); //[enchantments={levels:{looting:1}},unbreakable={},damage=31]
        }
        PlayerInventory inv = player.getInventory();
        if (inv.firstEmpty() != -1) {
            inv.setItem(inv.firstEmpty(), Item);
        }
    }

    @Override
    public void displayType(Main main, Location loc) {
        NamespacedKey namespacedKey = new NamespacedKey(main, "ItemDisplay");

        World world = loc.getWorld();

        final ItemDisplay display = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
        display.setRotation(loc.getYaw(), 0);
        display.setItemStack(Misc.getSkull("1ff041976a09dd053e3d1d4e611aac09594d74fc71a0ec4da0110416d317dba8"));
        display.setTransformation(new Transformation(new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f), new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f))); // Translation - leftrot - scale - rightrot
        display.setBillboard(Display.Billboard.FIXED);
        display.setCustomName(Misc.Color("&aItem"));
        display.setCustomNameVisible(true);
        display.setPersistent(true);
        display.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 1);

        final Interaction interaction = (Interaction) world.spawnEntity(loc.subtract(0, 0.48, 0), EntityType.INTERACTION);
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
