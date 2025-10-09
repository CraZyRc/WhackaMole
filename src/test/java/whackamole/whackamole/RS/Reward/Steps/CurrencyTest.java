package whackamole.whackamole.RS.Reward.Steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.HashMap;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import whackamole.whackamole.RS.Reward.Steps.Types.CurrencyReward;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.RS.Reward.ValidationException;


@ExtendWith(MockitoExtension.class)
public class CurrencyTest {
    static public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();

    @Test
    public void CurrenyAmountGivenToPlayer() throws ValidationException {
        
        var reward = new CurrencyReward(new HashMap<String, Object>() {{
            put("Quantity", 10);
        }});
        
        reward.Validate();
        
        var playerMock = mock(Player.class);
        var context = new RewardExecutorContext();
        context.player = playerMock;

        mockStatic(RewardsManager.class).when(() -> RewardsManager.sendScoreToPlayer(any(Player.class), anyInt())).then(input -> {
            softly.then(input.<Integer>getArgument(0)).isEqualTo(10);
            return null;
        });
        
        
        reward.Execute(context);
    }
}
