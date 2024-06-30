package whackamole.whackamole.Rewards.Types;

import org.bukkit.entity.Player;
import whackamole.whackamole.Utils.Logger;

public class Sound {

    private static void setSound(Player player, String sound) {
        org.bukkit.Sound s = org.bukkit.Sound.valueOf(sound);
        try {
            player.playSound(player, s, 1.0F, 1.0F);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }
}
