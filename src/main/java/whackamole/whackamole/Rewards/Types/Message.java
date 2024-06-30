package whackamole.whackamole.Rewards.Types;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

public class Message {

    public void setMessage(Player player, String Message, String Type) {
        if (Type.equals("String")) {
            player.sendMessage(Config.AppConfig.PREFIX + Translator.Color(Message));
        } else if (Type.equals("Json")) {
            BaseComponent[] list = ComponentSerializer.parse(Message);
            BaseComponent[] sendList = new ComponentBuilder(Config.AppConfig.PREFIX).append(list).create();
            player.spigot().sendMessage(ChatMessageType.CHAT, sendList);
        } else Logger.error("cannot send playermessage, invalid message type in rewards file");
    }
}
