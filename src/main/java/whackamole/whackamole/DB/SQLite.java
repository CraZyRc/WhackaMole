package whackamole.whackamole.DB;

import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Logger;

import java.sql.*;

import org.codehaus.plexus.util.ExceptionUtils;
import org.jetbrains.annotations.Nullable;

public class SQLite {
    private String url = "jdbc:sqlite:" + Config.AppConfig.storageFolder+ "/Storage.db";
    
    public static CooldownDB Cooldown; 
    public static GameDB Game; 
    public static GridDB Grid; 
    public static HologramDB Hologram; 
    public static ScoreboardDB Scoreboard; 

    private SQLite() {}

    private static SQLite Instance;
    public static SQLite getInstance() {
        if (SQLite.Instance == null) {
            SQLite.Instance = new SQLite();
        }
        return SQLite.Instance;
    }
    
    public static void onLoad() {
        SQLite.Cooldown    = new CooldownDB(getInstance());
        SQLite.Game        = new GameDB(getInstance());
        SQLite.Grid        = new GridDB(getInstance());
        SQLite.Hologram    = new HologramDB(getInstance());
        SQLite.Scoreboard  = new ScoreboardDB(getInstance());
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
        
        try {
            if (this.connection != null && ! this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (Exception _e) {}
        
        this.connection = null;
    }

    private Connection connection;
    @Nullable
    private Connection getConnection() {
        if (this.connection == null) {
            try {
                this.connection = DriverManager.getConnection(url);
            } catch (SQLException e) {
                Logger.error(e.getMessage());
                Logger.error(e.getStackTrace().toString());
                assert false : e.getMessage();
            }
        }
        return this.connection;
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
