package whackamole.whackamole.RS.Reward.Steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import whackamole.whackamole.RS.Reward.Steps.Types.MessageReward;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.Reward.ValidationException;


@ExtendWith(MockitoExtension.class)
public class MessageTest {
    static public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();

    @Test
    public void SendStringMessageToPlayer() throws ValidationException {
        
        var reward = new MessageReward(new HashMap<String, Object>() {{
            put("Message", "Test message");
            put("Format", "String");
        }});

        reward.Validate();
        
        var playerMock = mock(Player.class);
        var spigotMock = mock(Spigot.class);
        doNothing().when(playerMock).sendMessage(any(String.class));
        when(playerMock.spigot()).thenReturn(spigotMock);
        doNothing().when(spigotMock).sendMessage(any(ChatMessageType.class), any(BaseComponent.class));
        var context = new RewardExecutorContext();

        context.player = playerMock;
        
        reward.Execute(context);
    }
    @Test
    public void SendJsonMessageToPlayer() throws ValidationException {
        
        var reward = new MessageReward(new HashMap<String, Object>() {{
            put("Message", "[\"\",{\"text\":\"! JACKPOT ! \",\"bold\":true,\"color\":\"gold\"},{\"text\":\"well played !\"}]");
            put("Format", "Json");
        }});

        reward.Validate();
        
        var playerMock = mock(Player.class);
        var spigotMock = mock(Spigot.class);
        doNothing().when(playerMock).sendMessage(any(String.class));
        when(playerMock.spigot()).thenReturn(spigotMock);
        doNothing().when(spigotMock).sendMessage(any(ChatMessageType.class), any(BaseComponent.class));
        var context = new RewardExecutorContext();

        context.player = playerMock;
        
        reward.Execute(context);
    }
}
