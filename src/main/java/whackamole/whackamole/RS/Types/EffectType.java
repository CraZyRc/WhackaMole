package whackamole.whackamole.RS.Types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import whackamole.whackamole.Main;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

import java.util.LinkedHashMap;

public class EffectType implements IRewardType {
    private PotionEffectType effect;
    private int duration;
    private int amplifier;
    public int rewardChance = 0;
    public int threshold;

    @SuppressWarnings("deprecation")
    private EffectType(int threshold, String effectName, int duration, int amplifier, int rewardChance)
    {
        this.threshold      = threshold;
        this.effect         = PotionEffectType.getByName(effectName);
        this.duration       = duration;
        this.amplifier      = amplifier;
        this.rewardChance   = rewardChance;
    }

    public static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) {
        var effectName = (String) Settings.get("Effect");
        var duration = (int) Settings.get("Duration");
        var amplifier = (int) Settings.get("Amplifier");
        var rewardChance = (int) Settings.get("RewardChance");

        return new EffectType(threshold, effectName, duration, amplifier, rewardChance);
    }

    @Override
    public boolean Check() {
        if (this.effect == null) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_STRING.Format("Effect"));
            return false;
        } else if (this.duration <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Effect","Effect"));
            return false;
        } else if (this.amplifier <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_INT.Format("Amplifier","Amplifier"));
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
        PotionEffect Effect = new PotionEffect(this.effect, this.duration, this.amplifier);
        player.addPotionEffect(Effect);
    }

    @Override
    public void displayType(Main main, Location loc) {}

    @Override
    public void Remove(Player player) {}


}
