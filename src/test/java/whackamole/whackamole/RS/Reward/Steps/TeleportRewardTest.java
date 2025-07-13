package whackamole.whackamole.RS.Reward.Steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.Reward.ValidationException;
import whackamole.whackamole.Utils.SafeBlocks;


@ExtendWith(MockitoExtension.class)
public class TeleportRewardTest {
    static public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();

    static MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class);
    Player playerMock = mock(Player.class);
    Location locationMock = mock(Location.class);
    Block blockMock = mock(Block.class);
    World worldMock = mock(World.class);

    @Test
    public void TeleportToCorrectLocation() throws ValidationException {
        bukkitMock.when(() -> Bukkit.getWorld(anyString())).thenReturn(worldMock);
        when(worldMock.getBlockAt(any(Location.class))).thenReturn(blockMock);
        var safeBlocksMock = mockStatic(SafeBlocks.class);
        safeBlocksMock.when(() -> SafeBlocks.getUnsafe(any(Material.class))).thenReturn(false);

        var reward = new TeleportReward(new HashMap<String, Object>() {{
            put("World", "Test World");
            put("X", 1d);
            put("Y", 2d);
            put("Z", 3d);
            put("Rotation","NORTH");
        }});

        reward.Validate();

        var context = new RewardExecutorContext();
        context.player = playerMock;
        context.plugin = null;

        when(playerMock.teleport(locationMock)).then(input -> {
            var loc = input.<Location>getArgument(0);
            if (loc == null) { 
                softly.fail("Teleport location is null"); 
                return true;
            }

            softly.then(loc.getWorld().getName()).isEqualTo(worldMock.getName());
            softly.then(loc.getX()).isEqualTo(1d);
            softly.then(loc.getY()).isEqualTo(2d);
            softly.then(loc.getZ()).isEqualTo(3d);

            return true;
        });

        reward.Execute(context);
    }
}
