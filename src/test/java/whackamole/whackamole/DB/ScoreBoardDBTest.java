package whackamole.whackamole.DB;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class ScoreBoardDBTest extends SQLTestBase {

    static ScoreboardRow row;

    @Test
    @Order(1)
    public void ScoreInsertSuccessfull() {
        row = scoreboardDB.Insert(playerMock.getUniqueId(), gameSettingsMock.ID, 10, 11, 5, LocalDateTime.now());
        softly.then(row.ID).as("row ID not updated correctly").isNotEqualTo(0);
        
        var rowList = scoreboardDB.Select(row.playerID);
        softly.then(rowList).as("Row not correctly inserted").isNotEmpty();
    }
    
    @Test
    @Order(2)
    public void ScoreDeleteSuccessfull() {
        scoreboardDB.Delete(row.ID);

        var rowList = scoreboardDB.Select(row.gameID, row.playerID);
        softly.then(rowList).as("Row not correctly deleted").isEmpty();
    }
}
