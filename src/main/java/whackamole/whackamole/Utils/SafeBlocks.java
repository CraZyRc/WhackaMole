package whackamole.whackamole.Utils;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class SafeBlocks {
    public static final Set<Material> SAFE_MATERIALS = new HashSet<>();

    static { // TODO: Check legacy materials
        SAFE_MATERIALS.add(Material.AIR);
        SAFE_MATERIALS.add(Material.LEGACY_SAPLING);
        SAFE_MATERIALS.add(Material.POWERED_RAIL);
        SAFE_MATERIALS.add(Material.DETECTOR_RAIL);
        SAFE_MATERIALS.add(Material.LEGACY_LONG_GRASS);
        SAFE_MATERIALS.add(Material.DEAD_BUSH);
        SAFE_MATERIALS.add(Material.DANDELION);
        SAFE_MATERIALS.add(Material.POPPY);
        SAFE_MATERIALS.add(Material.BROWN_MUSHROOM);
        SAFE_MATERIALS.add(Material.RED_MUSHROOM);
        SAFE_MATERIALS.add(Material.TORCH);
        SAFE_MATERIALS.add(Material.FIRE);
        SAFE_MATERIALS.add(Material.REDSTONE_WIRE);
        SAFE_MATERIALS.add(Material.LEGACY_CROPS);
        SAFE_MATERIALS.add(Material.LADDER);
        SAFE_MATERIALS.add(Material.LEGACY_RAILS);
        SAFE_MATERIALS.add(Material.LEVER);
        SAFE_MATERIALS.add(Material.REDSTONE_TORCH);
        SAFE_MATERIALS.add(Material.STONE_BUTTON);
        SAFE_MATERIALS.add(Material.SNOW);
        SAFE_MATERIALS.add(Material.SUGAR_CANE);
        SAFE_MATERIALS.add(Material.LEGACY_PORTAL);
        SAFE_MATERIALS.add(Material.LEGACY_DIODE_BLOCK_OFF);
        SAFE_MATERIALS.add(Material.LEGACY_DIODE_BLOCK_ON);
        SAFE_MATERIALS.add(Material.PUMPKIN_STEM);
        SAFE_MATERIALS.add(Material.MELON_STEM);
        SAFE_MATERIALS.add(Material.VINE);
        SAFE_MATERIALS.add(Material.LEGACY_WATER_LILY);
        SAFE_MATERIALS.add(Material.NETHER_WART);
        SAFE_MATERIALS.add(Material.LEGACY_ENDER_PORTAL);
        SAFE_MATERIALS.add(Material.COCOA);
        SAFE_MATERIALS.add(Material.TRIPWIRE_HOOK);
        SAFE_MATERIALS.add(Material.TRIPWIRE);
        SAFE_MATERIALS.add(Material.FLOWER_POT);
        SAFE_MATERIALS.add(Material.CARROT);
        SAFE_MATERIALS.add(Material.POTATO);
        SAFE_MATERIALS.add(Material.LEGACY_WOOD_BUTTON);
        SAFE_MATERIALS.add(Material.LEGACY_SKULL);
        SAFE_MATERIALS.add(Material.LEGACY_REDSTONE_COMPARATOR_OFF);
        SAFE_MATERIALS.add(Material.LEGACY_REDSTONE_COMPARATOR_ON);
        SAFE_MATERIALS.add(Material.ACTIVATOR_RAIL);
        SAFE_MATERIALS.add(Material.LEGACY_CARPET);
        SAFE_MATERIALS.add(Material.LEGACY_DOUBLE_PLANT);
        SAFE_MATERIALS.add(Material.LEGACY_SEEDS);
        SAFE_MATERIALS.add(Material.LEGACY_SIGN_POST);
        SAFE_MATERIALS.add(Material.LEGACY_WOODEN_DOOR);
        SAFE_MATERIALS.add(Material.LEGACY_WALL_SIGN);
        SAFE_MATERIALS.add(Material.LEGACY_STONE_PLATE);
        SAFE_MATERIALS.add(Material.LEGACY_IRON_DOOR_BLOCK);
        SAFE_MATERIALS.add(Material.LEGACY_WOOD_PLATE);
        SAFE_MATERIALS.add(Material.LEGACY_FENCE_GATE);
    }

    public static boolean getSafe(Material block) {
        Logger.info(SAFE_MATERIALS.contains(block) + "");
        return SAFE_MATERIALS.contains(block);
    }
}
