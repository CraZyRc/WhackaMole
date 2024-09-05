package whackamole.whackamole.Utils;

import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.HashSet;
import java.util.Set;


public class SafeBlocks {
    public static final Set<Material> SAFE_MATERIALS = new HashSet<>();

    static {
        SAFE_MATERIALS.add(Material.AIR);
        SAFE_MATERIALS.add(Material.SHORT_GRASS);
        SAFE_MATERIALS.add(Material.TALL_GRASS);
        SAFE_MATERIALS.add(Material.GLOW_LICHEN);
        SAFE_MATERIALS.add(Material.ITEM_FRAME);
        SAFE_MATERIALS.add(Material.GLOW_ITEM_FRAME);
        SAFE_MATERIALS.add(Material.MOSS_CARPET);
        SAFE_MATERIALS.add(Material.DEAD_BUSH);
        SAFE_MATERIALS.add(Material.DANDELION);
        SAFE_MATERIALS.add(Material.POPPY);
        SAFE_MATERIALS.add(Material.BROWN_MUSHROOM);
        SAFE_MATERIALS.add(Material.RED_MUSHROOM);
        SAFE_MATERIALS.add(Material.TORCH);
        SAFE_MATERIALS.add(Material.FIRE);
        SAFE_MATERIALS.add(Material.REDSTONE_WIRE);
        SAFE_MATERIALS.add(Material.LADDER);
        SAFE_MATERIALS.add(Material.LEVER);
        SAFE_MATERIALS.add(Material.REDSTONE_TORCH);
        SAFE_MATERIALS.add(Material.SNOW);
        SAFE_MATERIALS.add(Material.PUMPKIN_STEM);
        SAFE_MATERIALS.add(Material.MELON_STEM);
        SAFE_MATERIALS.add(Material.VINE);
        SAFE_MATERIALS.add(Material.LILY_PAD);
        SAFE_MATERIALS.add(Material.NETHER_WART);
        SAFE_MATERIALS.add(Material.COCOA);
        SAFE_MATERIALS.add(Material.TRIPWIRE_HOOK);
        SAFE_MATERIALS.add(Material.TRIPWIRE);
        SAFE_MATERIALS.add(Material.FLOWER_POT);
        SAFE_MATERIALS.add(Material.CARROT);
        SAFE_MATERIALS.add(Material.POTATO);
        SAFE_MATERIALS.add(Material.ACTIVATOR_RAIL);
    }

    public static boolean getUnsafe(Material block) {
        return !SAFE_MATERIALS.contains(block)
                && !Tag.BUTTONS.isTagged(block)
                && !Tag.ALL_SIGNS.isTagged(block)
                && !Tag.ALL_HANGING_SIGNS.isTagged(block)
                && !Tag.WOOL_CARPETS.isTagged(block)
                && !Tag.TRAPDOORS.isTagged(block)
                && !Tag.SAPLINGS.isTagged(block)
                && !Tag.FLOWERS.isTagged(block)
                && !Tag.BANNERS.isTagged(block)
                && !Tag.CANDLES.isTagged(block)
                && !Tag.CAVE_VINES.isTagged(block)
                && !Tag.CORAL_PLANTS.isTagged(block)
                && !Tag.CROPS.isTagged(block)
                && !Tag.RAILS.isTagged(block)
                && !Tag.DOORS.isTagged(block)
                && !Tag.PRESSURE_PLATES.isTagged(block);
    }
}
