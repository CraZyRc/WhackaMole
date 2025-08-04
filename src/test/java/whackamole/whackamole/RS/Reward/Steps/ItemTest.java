package whackamole.whackamole.RS.Reward.Steps;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

import java.util.HashMap;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import whackamole.whackamole.RS.Reward.Steps.Types.ItemReward;
import whackamole.whackamole.RS.RewardExecutorContext;
import whackamole.whackamole.RS.Reward.ValidationException;


@ExtendWith(MockitoExtension.class)
public class ItemTest {
    static public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();

    @Test
    public void ItemGivenToPlayer() throws ValidationException {
        
        var reward = new ItemReward(new HashMap<String, Object>() {{
            put("Amount", 10);
            put("Material", "DIAMON_AXE");
        }});

        var materialMock = mock(Material.class);
        mockStatic(Material.class).when(() -> Material.matchMaterial(any(String.class))).thenReturn(materialMock);
        
        reward.Validate();
        
        var playerMock = mock(Player.class);
        var inventoryMock = mock(PlayerInventory.class);
        when(playerMock.getInventory()).thenReturn(inventoryMock);
        when(inventoryMock.firstEmpty()).thenReturn(1);
        doNothing().when(inventoryMock).setItem(any(Integer.class), any(ItemStack.class));

        var context = new RewardExecutorContext();
        context.player = playerMock;
        
        reward.Execute(context);
    }
}
