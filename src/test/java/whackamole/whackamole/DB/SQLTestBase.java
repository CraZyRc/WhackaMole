package whackamole.whackamole.DB;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import whackamole.whackamole.TestBase;

public class SQLTestBase extends TestBase {
    
    final static SQLite SQL = SQLite.getInstance();
    final static GameDB gameDB = SQLite.getGameDB();
    final static CooldownDB cooldownDB = SQLite.getCooldownDB();

    @BeforeAll
    public static void setupSQL() {
        var DBfile = new File("./test/Storage.db");
        if (DBfile.exists()) {
            DBfile.delete();
        }
        SQL.setUrl("jdbc:sqlite:" + DBfile.getPath());

        gameDB.Create();
        cooldownDB.Create();
    }

    @AfterAll
    public static void CleanupSQL() {
        var DBfile = new File("./test/Storage.db");
        DBfile.delete();
    }
}
