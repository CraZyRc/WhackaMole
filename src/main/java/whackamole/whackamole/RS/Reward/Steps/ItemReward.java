package whackamole.whackamole.RS.Reward.Steps;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import whackamole.whackamole.RS.Reward.ValidationException;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.Utils.Translator;

import java.util.Map;

public class ItemReward extends RewardStep {
  private String materialName;
  private int amount;
  private String NBTString;

  private ItemStack item;

  public ItemReward(Map<String, ?> settings) {
    this.materialName = RewardsManager.getOrDefault(settings, "Material", "");
    this.amount = RewardsManager.getOrDefault(settings, "Amount", 0);
    this.NBTString = RewardsManager.getOrDefault(settings, "NBT", "");
  }

  @SuppressWarnings("deprecation")
  @Override
  public void Validate() throws ValidationException {
    Material material = org.bukkit.Material.matchMaterial(this.materialName);
    if (material == null) {
      throw new ValidationException(Translator.REWARDS_TYPE_INVALID_STRING.Format("Material"));
    }

    if (this.amount <= 0) {
      throw new ValidationException(Translator.REWARDS_TYPE_INVALID_INT.Format("Amount", "Amount"));
    }

    this.item = new ItemStack(material, this.amount);

    if (!this.NBTString.isEmpty()) {
      try {
        var nbtTags = this.NBTString.replace("[","").replace("]","");
        this.item = Bukkit.getUnsafe().modifyItemStack(this.item, material.getKey().getKey() + "[" + nbtTags + "]"); //[enchantments={levels:{looting:1}},unbreakable={},damage=31]
      } catch (Exception _error) {
        throw new ValidationException("Invalid NBT");
      }
    }
  }

  @Override
  public void Execute(RewardExecutorContext context) {
    PlayerInventory inv = context.player.getInventory();
    if (inv.firstEmpty() != -1) {
      inv.setItem(inv.firstEmpty(), this.item);
    }
  }
}
