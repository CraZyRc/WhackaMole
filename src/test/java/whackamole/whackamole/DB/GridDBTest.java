package whackamole.whackamole.DB;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class GridDBTest extends SQLTestBase {

    static GridRow row;

    @Test
    @Order(1)
    public void GridBlockInsertSuccessfull() {
        row = gridDB.Insert(gridMock, gameMock.ID).get(0);
        
        var rowList = gridDB.Select(gameMock.ID);
        softly.then(rowList).as("Row not correctly inserted").isNotEmpty();
    }
    
    @Test
    @Order(2)
    public void GridBlockDeleteSuccessfull() {
        return;
        // TODO: delete not impletmented yet!

        // scoreboardDB.Delete(row.gameID, row.playerID);

        // var rowList = scoreboardDB.Select(row.gameID, row.playerID);
        // softly.then(rowList).as("Row not correctly deleted").isEmpty();
    }

}
