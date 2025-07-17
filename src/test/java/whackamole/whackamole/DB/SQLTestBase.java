package whackamole.whackamole.DB;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import whackamole.whackamole.helpers.TestBase;

public class SQLTestBase extends TestBase {
    
    final static File DBfile = new File("./test/Storage.db");
    final static SQLite SQL = SQLite.getInstance();
    static GameDB gameDB; 
    static GridDB gridDB;
    static CooldownDB cooldownDB;
    static ScoreboardDB scoreboardDB;
    static HologramDB hologramDB;

    @BeforeAll
    public static void setupSQL() {
        if (DBfile.exists()) {
            DBfile.delete();
        }
        SQL.setUrl("jdbc:sqlite:" + DBfile.getPath());
        SQLite.onLoad();

        gameDB = SQLite.Game;
        gridDB = SQLite.Grid;
        cooldownDB = SQLite.Cooldown;
        scoreboardDB = SQLite.Scoreboard;
        hologramDB = SQLite.Hologram;

        gameDB.Create();
        gridDB.Create();
        cooldownDB.Create();
        scoreboardDB.Create();
        hologramDB.Create();
    }

    @AfterAll
    public static void CleanupSQL() {
        DBfile.delete();
    }
}
