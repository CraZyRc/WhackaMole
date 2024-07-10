package whackamole.whackamole.RS.Types;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import whackamole.whackamole.Utils.Logger;

import java.util.LinkedHashMap;

public class EffectType implements RewardType {
    private PotionEffectType effect;
    private int duration;
    private int amplifier;

    @Override
    public RewardType Load(LinkedHashMap Settings) {
        this.effect = PotionEffectType.getByName((String) Settings.get("Effect"));
        this.duration = (int) Settings.get("Duration");
        this.amplifier = (int) Settings.get("Amplifier");
        return this;
    }

    @Override
    public boolean Check() {
        if (this.effect == null) {
            Logger.error("Invalid Effect set in the RewardsFile"); // TODO: add Translator message
            return false;
        } else if (this.duration <= 0) {
            Logger.error("Invalid Duration set in the RewardsFile, Duration has to be bigger than 0"); // TODO: add Translator message
            return false;
        } else if (this.amplifier <= 0) {
            Logger.error("Invalid Amplifier set in the RewardsFile, Amplifier has to be bigger than 0"); // TODO: add Translator message
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void Execute(Player player) {
        if (this.Check()) {
            PotionEffect Effect = new PotionEffect(this.effect, this.duration, this.amplifier);
            player.addPotionEffect(Effect);
        }
    }
}
