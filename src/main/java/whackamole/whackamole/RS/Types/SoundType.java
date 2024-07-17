package whackamole.whackamole.RS.Types;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import whackamole.whackamole.Main;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

import java.util.LinkedHashMap;

public class SoundType implements RewardType {
    private Sound sound;
    private float volume;
    private float pitch;

    @Override
    public RewardType Load(LinkedHashMap Settings) {
        this.sound = Sound.valueOf((String) Settings.get("Sound"));
        this.volume = Float.valueOf((String) Settings.get("Volume"));
        this.pitch = Float.valueOf((String) Settings.get("Pitch"));
        return this;
    }

    @Override
    public boolean Check() {
        if (this.sound == null) {
            Logger.error(Translator.REWARDS_TYPE_INVALIDSTRING.Format("Sound"));
            return false;
        } else if (this.volume <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALIDINT.Format("Volume", "Volume"));
            return false;
        } else if (this.pitch <= 0) {
            Logger.error(Translator.REWARDS_TYPE_INVALIDINT.Format("Pitch", "Pitch"));
            return false;
        } else return true;
    }

    @Override
    public void Execute(Player player) {
        player.playSound(player,this.sound , this.volume, this.pitch);
    }

    @Override
    public void displayType(Main main, Location loc) {}

    @Override
    public void Remove(Player player) {}
}
