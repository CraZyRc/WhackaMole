package whackamole.whackamole.RS.Types;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import whackamole.whackamole.Main;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

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
            Logger.error(Translator.REWARDS_TYPE_INVALIDSTRING.Format("Effect"));
            return false;
        } else if (this.duration <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALIDINT.Format("Effect","Effect"));
            return false;
        } else if (this.amplifier <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALIDINT.Format("Amplifier","Amplifier"));
            return false;
        } else {
            return true;
        }
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
