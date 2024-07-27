package whackamole.whackamole.RS.Types;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import whackamole.whackamole.Config;
import whackamole.whackamole.Main;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

import java.util.LinkedHashMap;

public class MessageType implements IRewardType {
    private String message;
    private String messageType;
    public int rewardChance = 0;

    @Override
    public IRewardType Load(LinkedHashMap<String, ?> Settings) {
        this.messageType = (String) Settings.get("messageType");
        this.message = (String) Settings.get("Message");
        this.rewardChance = (int) Settings.get("RewardChance");
        return this;
    }

    @Override
    public boolean Check() {
        if (!messageType.equals("String") && !messageType.equals("Json")) {
            Logger.error(Translator.REWARDS_TYPE_INVALID_MESSAGETYPE);
            return false;
        } else if (this.message.isEmpty()) {
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
    public void Execute(Player player) {
        if (this.messageType.equals("String")) {
            player.sendMessage(Config.AppConfig.PREFIX + Misc.Color(this.message));
        } else if (this.messageType.equals("Json")) {
            BaseComponent[] list = ComponentSerializer.parse(this.message);
            BaseComponent[] sendList = new ComponentBuilder(Config.AppConfig.PREFIX).append(list).create();
            player.spigot().sendMessage(ChatMessageType.CHAT, sendList);
        }

    }

    @Override
    public void displayType(Main main, Location loc) {}

    @Override
    public void Remove(Player player) {}
}
