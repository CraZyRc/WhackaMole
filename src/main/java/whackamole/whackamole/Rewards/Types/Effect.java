package whackamole.whackamole.Rewards.Types;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import whackamole.whackamole.Utils.Logger;

public class Effect {

    public void setEffect(Player player, String Enchantment, int Duration) {
        PotionEffectType Type = PotionEffectType.getByName(Enchantment);

        if (Type == null) {
            Logger.error("Invalid potion effect"); // TODO: add Translator message
            return;
        }

        PotionEffect Effect = new PotionEffect(Type, Duration, 1);
        player.addPotionEffect(Effect);
    }
}
