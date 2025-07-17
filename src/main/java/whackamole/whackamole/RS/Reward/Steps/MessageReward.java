package whackamole.whackamole.RS.Reward.Steps;

import java.util.Map;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import whackamole.whackamole.Config;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.RS.Reward.ValidationException;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

public class MessageReward extends RewardStep {

    private enum MessageFormat {
        Invalid(""),
        String("String"),
        Json("Json");

        private String name;
        MessageFormat(String name) {
            this.name = name;
        }

        static protected MessageFormat Parse(String type) {
            for (var format : values()) {
                if (format.name.toLowerCase().equals(type.toLowerCase())) {
                    return format;
                }
            }
            return Invalid;
        }
    }

    private String Message;
    private MessageFormat Format;

    public MessageReward(Map<String, ?> settings) {
        this.Message = RewardsManager.getOrDefault(settings, "Message", "");
        this.Format = MessageFormat.Parse(RewardsManager.getOrDefault(settings, "MessageType", ""));
    }
    
    @Override
    public void Validate() throws ValidationException {
        if (this.Format == null)    throw new ValidationException(Translator.REWARDS_TYPE_INVALID_MESSAGETYPE);
        if (this.Message.isEmpty()) throw new ValidationException(Translator.REWARDS_TYPE_NOMESSAGESET);
    }

    @Override
    public void Execute(RewardExecutorContext context) {
        switch (this.Format) {
            case Invalid: break;
            case String: context.player.sendMessage(Config.AppConfig.PREFIX + Misc.Color(this.Message)); break;
            case Json:
                context.player.spigot().sendMessage(ChatMessageType.CHAT, 
                    new ComponentBuilder(Config.AppConfig.PREFIX)
                        .append(ComponentSerializer.parse(this.Message))
                        .create()
                );
        }
    }
}
