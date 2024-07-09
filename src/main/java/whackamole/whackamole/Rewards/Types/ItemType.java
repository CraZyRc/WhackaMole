package whackamole.whackamole.Rewards.Types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.YMLFile;

public class ItemType implements RewardType {
    private Material material;
    private int amount;
    private String nbt;

    @Override
    public void Load(YMLFile file, String Reward) {
        this.material = Material.matchMaterial(file.getString(Reward + ".Settings.Material"));
        this.amount = file.getInt(Reward + ".Settings.Amount");
        this.nbt = file.getString(Reward + ".Settings.NBT");
    }

    @Override
    public boolean Check() {
        if (material == null) {
            Logger.error("Invalid Material name set in the RewardsFile");
            return false;
        } else if (this.amount <= 0) {
            Logger.error("Invalid Amount set in the RewardsFile, Amount has to be bigger than 0");
            return false;
        } else return true;
    }

    @Override
    public void Execute(Player player) {
        if (this.Check()) {
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
    }
}
