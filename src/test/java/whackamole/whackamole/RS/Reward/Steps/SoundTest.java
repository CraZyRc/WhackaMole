package whackamole.whackamole.RS.Reward.Steps;

import static org.mockito.Mockito.mock;

import java.util.HashMap;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import whackamole.whackamole.RS.Reward.Steps.Types.SoundReward;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.Reward.ValidationException;


@ExtendWith(MockitoExtension.class)
public class SoundTest {
    static public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();

    @Test
    public void PlaySoundforPlayer() throws ValidationException {
        
        var reward = new SoundReward(new HashMap<String, Object>() {{
            put("Sound", "ENTITY_GENERIC_EXPLODE");
            put("Volume", 100f);
            put("Pitch", 100f);
        }});

        reward.Validate();
        
        var playerMock = mock(Player.class);
        var context = new RewardExecutorContext();

        context.player = playerMock;
        
        reward.Execute(context);
    }
}
