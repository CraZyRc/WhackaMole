package whackamole.whackamole.DB;

import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Logger;

import java.io.File;
import java.sql.*;

import org.codehaus.plexus.util.ExceptionUtils;
import org.jetbrains.annotations.Nullable;

public class SQLite {
    private static String url = "jdbc:sqlite:" + Config.AppConfig.storageFolder+ "/Storage.db";
    
    public static final CooldownDB Cooldown      = new CooldownDB(getInstance());
    public static final GameDB Game              = new GameDB(getInstance());
    public static final GridDB Grid              = new GridDB(getInstance());
    public static final HologramDB Hologram      = new HologramDB(getInstance());
    public static final ScoreboardDB Scoreboard  = new ScoreboardDB(getInstance());

    private SQLite() {}

    private static SQLite Instance;
    public static SQLite getInstance() {
        if (SQLite.Instance == null) {
            SQLite.Instance = new SQLite();
        }
        return SQLite.Instance;
    }
    
    public static void onLoad() {
        var dbFile = new File(Config.AppConfig.storageFolder + "/Storage.db");
        if(!dbFile.exists()) {
            SQLite.Hologram.Create();
            SQLite.Game.Create();
            SQLite.Grid.Create();
            SQLite.Cooldown.Create();
            SQLite.Scoreboard.Create();
        }

    }

    public String getUrl() {
        return SQLite.url;
    }

    public void setUrl(String url) {
        SQLite.url = url;
    }

    private static Connection connection;
    @Nullable
    private static Connection getConnection() {
        if (SQLite.connection == null) {
            try {
                SQLite.connection = DriverManager.getConnection(url);
            } catch (SQLException e) {
                Logger.error(e.getMessage());
                Logger.error(e.getStackTrace().toString());
                assert false : e.getMessage();
            }
        }
        return SQLite.connection;
    }

    private PreparedStatement getStatement(String query, Object... arguments) throws SQLException {
        var connection = getConnection();
        var stmt = connection.prepareStatement(query);
        for (int i = 0; i < arguments.length; i++) {
            stmt.setObject(i + 1, arguments[i]);
        }
        return stmt;
    }

    public void executeUpdate(String query) {
        this.executeUpdate(query, new Object[0]);
    }
    public void executeUpdate(String query, Object ... arguments) {
        try {
            var stmt = this.getStatement(query, arguments);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Failed to execute Query: (%s)\nFor Reason: %s\nWith StackTrace:\n%s".formatted(
                query,
                e.getMessage(),
                ExceptionUtils.getStackTrace(e)
            ));
            assert false : "Failed to execute Query: (%s)\nFor Reason: %s\nWith StackTrace:\n%s".formatted(
                    query,
                    e.getMessage(),
                    ExceptionUtils.getStackTrace(e));
        }
    }
    
    @Nullable
    public ResultSet executeQuery(String query) {
        return this.executeQuery(query, new Object[0]);
    }
    @Nullable
    public ResultSet executeQuery(String query, Object ... arguments) {
        try {
            var stmt = this.getStatement(query, arguments);
            return stmt.executeQuery();
        } catch (SQLException e) {
            Logger.error("Failed to execute Query: (%s)\nFor Reason: %s\nWith StackTrace:\n%s".formatted(
                    query,
                    e.getMessage(),
                    ExceptionUtils.getStackTrace(e)));
            assert false : "Failed to execute Query: (%s)\nFor Reason: %s\nWith StackTrace:\n%s".formatted(
                    query,
                    e.getMessage(),
                    ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
