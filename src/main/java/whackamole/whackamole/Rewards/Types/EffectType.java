package whackamole.whackamole.Rewards.Types;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.YMLFile;

public class EffectType implements RewardType {
    private PotionEffectType effect;
    private int duration;
    private int amplifier;

    @Override
    public void Load(YMLFile file, String Reward) {
        this.effect = PotionEffectType.getByName((file.getString(Reward + ".Settings.Effect")));
        this.duration = file.getInt(Reward + ".Settings.Duration");
        this.amplifier = file.getInt(Reward + ".Settings.Amplifier");
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
