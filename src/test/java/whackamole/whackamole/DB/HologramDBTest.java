package whackamole.whackamole.DB;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class HologramDBTest extends SQLTestBase {

    static HologramRow row;

    @Test
    @Order(1)
    public void HologramInsertSuccessfull() {
        row = hologramDB.Insert(1, 1, "Test", locationMock, 3);
        
        var rowList = hologramDB.Select(row.holoID);
        softly.then(rowList).as("Row not correctly inserted").isNotEmpty();
    }
    
    @Test
    @Order(2)
    public void HologramDeleteSuccessfull() {
        hologramDB.Delete(row.holoID);

        var rowList = hologramDB.Select(row.gameID, row.holoID);
        softly.then(rowList).as("Row not correctly deleted").isEmpty();
    }

}
