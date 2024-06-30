package whackamole.whackamole.Rewards.Types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Item {

    public void setItem(Player player, String itemName, int amount, String nbt) {
        PlayerInventory inv = player.getInventory();
        ItemStack Item = new ItemStack(Material.valueOf(itemName));
        // TODO: add NBT

        Item.setAmount(amount);

        if (inv.firstEmpty() != -1) {
            inv.setItem(inv.firstEmpty(), Item);
        }
    }
}
