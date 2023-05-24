package whackamole.whackamole.DB;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import whackamole.whackamole.DB.Model.Row;

import java.util.HashMap;

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
    public void SelectGameSuccessfull() {
        var gameList = gameDB.Select(gameSettingsMock.ID);

        softly.then(gameList).as("Row with ID: (%s) is not found in table GameDB".formatted(gameSettingsMock.ID)).isNotEmpty();
        var gameRow = gameList.get(0);

        softly.then(gameRow.TeleportLocation.serialize()).as("Game Row TeleportLocation not correctly parsed").isEqualTo(new HashMap<String, Object>() {
            {
                put("world", worldMock.getName());
                put("x", 10.0);
                put("y", 20.0);
                put("z", 30.0);
                put("yaw", 0.0f);
                put("pitch", 2.0f);
            }
        });
    }

    @Test
    @Order(3)
    public void GameUpdateSuccessfull() {
        gameSettingsMock.spawnTimer = 50;
        gameDB.Update(gameMock);

        var gameList = gameDB.Select(gameSettingsMock.ID);
        softly.then(gameList).as("Row with ID: (%s) is not found in table GameDB".formatted(gameSettingsMock.ID)).isNotEmpty();
        var gameRow = gameList.get(0);
        softly.then(gameRow.spawnTimer).as("Row is not Inserted").isEqualTo(50);
    }

    @Test
    @Order(3)
    public void GameDeleteSuccessfull() {
        gameDB.Delete(gameSettingsMock.ID);

        var gameList = gameDB.Select(gameSettingsMock.ID);
        softly.then(gameList).as("Row not correctly deleted").isEmpty();
    }

    @Test
    public void RowUpCastSuccessfull() throws Exception {
        GameRow gameRow = new GameRow();
        gameRow.ID = 1;
        
        Row testRow = gameRow;
        GameRow outRow = new GameRow();
        
        Row.upCast(testRow, outRow);
        softly.then(outRow.ID).as("Row not correctly Up Casted").isEqualTo(1);
    }
}
