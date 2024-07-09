package whackamole.whackamole.Rewards.Types;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

public class MessageType implements RewardType {
    private String message;
    private String messageType;

    @Override
    public void Load(YMLFile file, String Reward) {
        this.messageType = file.getString(Reward + ".Settings.messageType");
        this.message = file.getString(Reward + ".Settings.message");
    }

    @Override
    public boolean Check() {
        if (!messageType.equals("String") || !messageType.equals("Json")) {
            Logger.error("Invalid MessageType set in the RewardsFile, this can only be Json or String"); // TODO: add Translator message
            return false;
        } else if (this.message.isEmpty()) {
            Logger.error("No Message has been set in the RewardsFile"); // TODO: add Translator message
            return false;
        } else return true;
    }

    @Override
    public void Execute(Player player) {
        if (this.Check()) {
            if (this.messageType.equals("String")) {
                player.sendMessage(Config.AppConfig.PREFIX + Translator.Color(this.message));
            } else if (this.messageType.equals("Json")) {
                BaseComponent[] list = ComponentSerializer.parse(this.message);
                BaseComponent[] sendList = new ComponentBuilder(Config.AppConfig.PREFIX).append(list).create();
                player.spigot().sendMessage(ChatMessageType.CHAT, sendList);
            }
        }

    }
}
