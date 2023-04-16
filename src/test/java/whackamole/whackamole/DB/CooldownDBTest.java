package whackamole.whackamole.DB;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class CooldownDBTest extends SQLTestBase {

    static CooldownRow cooldownRow;

    @Test
    @Order(1)
    public void cooldownInsertSuccessfull() {
        cooldownRow = cooldownDB.Insert(gameSettingsMock.ID, playerMock.getUniqueId(), 5000L);
        
        var rowList = cooldownDB.Select(cooldownRow.gameID, cooldownRow.playerID);
        softly.then(rowList).as("Row not correctly inserted").isNotEmpty();
    }
    
    @Test
    @Order(2)
    public void CooldownDeleteSuccessfull() {
        return;
        // TODO: delete not impletmented yet!

        // cooldownDB.Delete(cooldownRow.gameID, cooldownRow.playerID);

        // var rowList = cooldownDB.Select(cooldownRow.gameID, cooldownRow.playerID);
        // softly.then(rowList).as("Row not correctly deleted").isEmpty();
    }

}
