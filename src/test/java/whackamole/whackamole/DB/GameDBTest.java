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
        gameDB.Insert(gameMock);
        softly.then(gameSettingsMock.ID).isEqualTo(1).as("Row not correctly inserted");
    }
    
    @Test
    @Order(2)
    public void GameUpdateSuccessfull() {
        gameSettingsMock.spawnTimer = 50;
        gameDB.Update(gameMock);

        var gameList = gameDB.Select(gameSettingsMock.ID);
        softly.then(gameList).as("Row with ID: (%s) is not found in table GameDB".formatted(gameMock.getID())).isNotEmpty();
        var gameRow = gameList.get(0);
        softly.then(gameRow.spawnTimer).as("Row is not Inserted").isEqualTo(50);
    }
}
