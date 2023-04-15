package whackamole.whackamole.DB;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class GameDBTest extends SQLTestBase {


    @Test
    @Order(1)
    public void NewGameInsertSuccessfull() {
        var gameRow = gameDB.Insert(gameMock);
        gameMock.ID = gameRow.ID;
        softly.then(gameMock.ID).isNotEqualTo(0).as("Row not correctly inserted");
    }
    
    @Test
    @Order(2)
    public void GameUpdateSuccessfull() {
        gameSettingsMock.Interval = 50;
        gameDB.Update(gameMock);

        var gameList = gameDB.Select(gameMock.ID);
        assert ! gameList.isEmpty() : "Row with ID: (%s) is not found in table GameDB".formatted(gameMock.ID);
        var gameRow = gameList.get(0);
        softly.then(gameRow.spawnTimer).as("Row is not Inserted").isEqualTo(50);
    }

}
