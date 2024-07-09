package whackamole.whackamole.Rewards.Types;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.YMLFile;

public class SoundType implements RewardType {
    private Sound sound;
    private float volume;
    private float pitch;

    @Override
    public void Load(YMLFile file, String Reward) {
        this.sound = file.getSound(Reward + ".Settings.Sound");
        this.volume = file.getFloat(Reward + ".Settings.Volume");
        this.pitch = file.getFloat(Reward + ".Settings.Pitch");
    }

    @Override
    public boolean Check() {
        if (this.sound == null) {
            Logger.error("Invalid Sound name set in the RewardsFile"); // TODO: add Translator message
            return false;
        } else if (this.volume <= 0) {
            Logger.error("Invalid Volume has been set in the RewardsFile, Volume has to be bigger than 0"); // TODO: add Translator message
            return false;
        } else if (this.pitch <= 0) {
            Logger.error("Invalid Pitch has been set in the RewardsFile, Pitch has to be bigger than 0"); // TODO: add Translator message
            return false;
        } else return true;
    }

    @Override
    public void Execute(Player player) {
        if (this.Check()) {
            player.playSound(player,this.sound , this.volume, this.pitch);
        }
    }
}
