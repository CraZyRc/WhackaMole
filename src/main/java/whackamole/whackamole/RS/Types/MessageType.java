package whackamole.whackamole.RS.Types;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

import java.util.LinkedHashMap;

public class MessageType implements IRewardType {
    private String Message;
    private String messageType;
    private int rewardChance;
    private int threshold;

    private MessageType(int threshold, String messageType, String message, int rewardChance)
    {
        this.threshold = threshold;
        this.messageType = messageType;
        this.Message = message;
        this.rewardChance = rewardChance;
    }

    public static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) {
        var messageType = (String) Settings.get("messageType");
        var message = (String) Settings.get("Message");
        var rewardChance = (int) Settings.get("RewardChance");
        
        return new MessageType(threshold, messageType, message, rewardChance);
    }

    @Override
    public boolean Check() {
        if (!messageType.equals("String") && !messageType.equals("Json")) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_MESSAGETYPE);
            return false;
        } else if (this.Message == null || this.Message.isEmpty()) {
            Logger.error(Translator.REWARDS_TYPE_NOMESSAGESET);
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
    public int getThreshold() { return this.threshold; }

    @Override
    public void Execute(Player player) {
        if (this.messageType.equals("String")) {
            player.sendMessage(Config.AppConfig.PREFIX + Misc.Color(this.Message));
        } else if (this.messageType.equals("Json")) {
            BaseComponent[] list = ComponentSerializer.parse(this.Message);
            BaseComponent[] sendList = new ComponentBuilder(Config.AppConfig.PREFIX).append(list).create();
            player.spigot().sendMessage(ChatMessageType.CHAT, sendList);
        }

    }
}
