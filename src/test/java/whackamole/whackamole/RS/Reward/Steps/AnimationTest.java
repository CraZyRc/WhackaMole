package whackamole.whackamole.RS.Reward.Steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import whackamole.whackamole.Config;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.Reward.ValidationException;


@ExtendWith(MockitoExtension.class)
public class AnimationTest {
    static public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();

    @Test
    public void PlayAnimationForPlayer() throws ValidationException {
        
        var reward = new AnimationReward(new HashMap<String, Object>() {{
            put("Animation", "bounds_check");
            put("Duration", 10);
        }});

        var originialStorageLocation = Config.AppConfig.storageFolder;
        Config.AppConfig.storageFolder = "./src/test/resources";

        reward.Validate();
        
        var locationMock = mock(Location.class);
        var worldMock = mock(World.class);
        when(locationMock.getYaw()).thenReturn(0f);
        when(locationMock.add(any(Vector.class))).thenReturn(locationMock);
        when(locationMock.clone()).thenReturn(locationMock);
        when(locationMock.getWorld()).thenReturn(worldMock);

        var context = new RewardExecutorContext();
        context.location = locationMock;
        
        reward.Execute(context);

        Config.AppConfig.storageFolder = originialStorageLocation;
    }
}
