package whackamole.whackamole.DB;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import whackamole.whackamole.TestBase;

public class SQLTestBase extends TestBase {
    
    final static File DBfile = new File("./test/Storage.db");
    final static SQLite SQL = SQLite.getInstance();
    final static GameDB gameDB = SQLite.getGameDB();
    final static GridDB gridDB = SQLite.getGridDB();
    final static CooldownDB cooldownDB = SQLite.getCooldownDB();
    final static ScoreboardDB scoreboardDB = SQLite.getScoreboardDB();

    @BeforeAll
    public static void setupSQL() {
        if (DBfile.exists()) {
            DBfile.delete();
        }
        SQL.setUrl("jdbc:sqlite:" + DBfile.getPath());

        gameDB.Create();
        gridDB.Create();
        cooldownDB.Create();
        scoreboardDB.Create();
    }

    @AfterAll
    public static void CleanupSQL() {
        // DBfile.delete();
    }
}
