package whackamole.whackamole.DB;

import whackamole.whackamole.Config;
import whackamole.whackamole.Logger;

import java.io.File;
import java.sql.*;

public class SQLite {
    private static String url = "jdbc:sqlite:" + Config.AppConfig.storageFolder+ "/Storage.db";
    
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
            getGameDB().Create();
            getGridDB().Create();
            getCooldownDB().Create();
            getScoreboardDB().Create();
        }
    }

    private static Connection connection;
    private static Connection getConnection() {
        if (SQLite.connection == null) {
            try {
                SQLite.connection = DriverManager.getConnection(url);
            } catch (SQLException e) {
                Logger.error(e.getMessage());
                Logger.error(e.getStackTrace().toString());
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
            Logger.error(e.getMessage());
            Logger.error(e.getStackTrace().toString());
        }
    }
    
    public ResultSet executeQuery(String query) {
        return this.executeQuery(query, new Object[0]);
    }
    public ResultSet executeQuery(String query, Object ... arguments) {
        try {
            var stmt = this.getStatement(query, arguments);
            return stmt.executeQuery();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            Logger.error(e.getStackTrace().toString());
            return null;
        }
    }

    public static GameDB getGameDB() {
        return new GameDB(getInstance());
    }
    public static ScoreboardDB getScoreboardDB() { return new ScoreboardDB(getInstance()); }
    public static CooldownDB getCooldownDB() { return new CooldownDB(getInstance()); }
    public static GridDB getGridDB() { return new GridDB(getInstance()); }

}
