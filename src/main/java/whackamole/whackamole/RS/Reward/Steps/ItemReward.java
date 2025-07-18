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
  private Material material;
  private int amount;
  private String NBT;

  public ItemReward(Map<String, ?> settings) {
    this.materialName = RewardsManager.getOrDefault(settings, "Material", "");
    this.amount = RewardsManager.getOrDefault(settings, "Amount", 0);
    this.NBT = RewardsManager.getOrDefault(settings, "NBT", "");
  }

  @Override
  public void Validate() throws ValidationException {
    try {
      this.material = org.bukkit.Material.matchMaterial(this.materialName);
    } catch (Exception ex) {
      throw new ValidationException(Translator.REWARDS_TYPE_INVALID_STRING.Format("Material"));
    }

    if (this.amount <= 0) {
      throw new ValidationException(Translator.REWARDS_TYPE_INVALID_INT.Format("Amount", "Amount"));
    }
  }

  @Override
  public void Execute(RewardExecutorContext context) {
    ItemStack Item = new ItemStack(this.material, this.amount);
    if (!this.NBT.isEmpty()) {
      String nbtTags = this.NBT.replace("[","").replace("]","");
      Item = Bukkit.getUnsafe().modifyItemStack(Item, this.material.getKey().getKey() + "[" + nbtTags + "]"); //[enchantments={levels:{looting:1}},unbreakable={},damage=31]
    }
    PlayerInventory inv = context.player.getInventory();
    if (inv.firstEmpty() != -1) {
      inv.setItem(inv.firstEmpty(), Item);
    }
  }
}
