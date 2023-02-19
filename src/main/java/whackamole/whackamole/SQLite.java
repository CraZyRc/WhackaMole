package whackamole.whackamole;

import org.bukkit.block.BlockFace;

import java.sql.*;
import java.util.UUID;

public class SQLite {
    private static SQLite Instance;
    private static String url = "jdbc:sqlite:" + Config.AppConfig.storageFolder+ "/Storage.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
        return conn;
    }

    public static SQLite getInstance() {
        if (SQLite.Instance == null) {
            SQLite.Instance = new SQLite();
        }
        return SQLite.Instance;
    }

    public static void createNewTables() {
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Cooldown (gameID INTEGER NOT NULL, playerID TEXT NOT NULL, endTimeStamp INTEGER, PRIMARY KEY(gameID, playerID))");
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Game (ID INTEGER NOT NULL UNIQUE, Name TEXT NOT NULL, worldName TEXT,spawnDirection TEXT DEFAULT 'NORTH', hasJackpot INTEGER DEFAULT 1, jackpotSpawnChance INTEGER DEFAULT 1, missCount INTEGER DEFAULT 3, scorePoints INTEGER DEFAULT 1, spawnTimer REAL DEFAULT 1, spawnChance REAL DEFAULT 100, moleSpeed REAL DEFAULT 2, difficultyScale REAL DEFAULT 10, difficultyScore INTEGER DEFAULT 1, Cooldown INTEGER DEFAULT 86400000, PRIMARY KEY(ID AUTOINCREMENT))");
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Grid (gameID INTEGER NOT NULL, X INTEGER NOT NULL, Y INTEGER NOT NULL, Z INTEGER NOT NULL)");
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Scoreboard ( ID INTEGER NOT NULL, playerID TEXT NOT NULL, gameID TEXT NOT NULL, Score INTEGER NOT NULL, Datetime TEXT DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(ID AUTOINCREMENT))");
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public void insertCooldown(int gameID, UUID playerID, Long endTimeStamp) {
        String sql = "INSERT INTO Cooldown(gameID, playerID, endTimeStamp) VALUES(?,?,?)";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gameID);
            pstmt.setString(2, playerID.toString());
            pstmt.setLong(3, endTimeStamp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public void deleteCooldown(int gameID, UUID Player) {
        String sql = "DELETE FROM Cooldown WHERE gameID = ? AND playerID = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gameID);
            pstmt.setString(2, Player.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }

    }

    public int insertGame(String Name, String worldName, BlockFace spawnDirection) {
        String sql = "INSERT INTO Game(Name, worldName, spawnDirection) VALUES(?,?,?)";
        String sq2 = "SELECT last_insert_rowid()";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, Name);
            pstmt.setString(2, worldName);
            pstmt.setString(3, spawnDirection.name());
            pstmt.executeUpdate();
            PreparedStatement pstmt2 = conn.prepareStatement(sq2);
            ResultSet result = pstmt2.executeQuery();
            return result.getInt("last_insert_rowid()");
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return 0;
        }
    }

    public ResultSet selectGames(String worldName) {
        String sql = "SELECT * FROM Game";

        if (!worldName.isEmpty()) {
            sql += " WHERE worldName = '" + worldName + "'";
        }
        try {
            Connection conn = connect();
            PreparedStatement pstmt2 = conn.prepareStatement(sql);
            ResultSet result = pstmt2.executeQuery();
            return result;
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return null;
        }
    }

    public void deleteGame(int gameID) {
        String sql = "DELETE FROM Game WHERE ID = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gameID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }

    }

    public void insertGrid(int gameID, int X, int Y, int Z) {
        String sql = "INSERT INTO Grid(gameID, X, Y, Z) VALUES(?,?,?,?)";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gameID);
            pstmt.setInt(2, X);
            pstmt.setInt(3, Y);
            pstmt.setInt(4, Z);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public void insertScoreboard(UUID playerID, String gameID, int Score, String Datetime) {
        String sql = "INSERT INTO Scoreboard(playerID, gameID, Score, Datetime) VALUES(?,?,?,?)";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playerID.toString());
            pstmt.setString(2, gameID);
            pstmt.setInt(3, Score);
            pstmt.setString(4, Datetime);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public void updateGame(String Setting, String Value, int ID) {
        String sql = "UPDATE Game SET " + Setting + " = ? WHERE ID = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, Value);
            pstmt.setInt(2, ID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public void updateGame(String Setting, int Value, int ID) {
        String sql = "UPDATE Game SET " + Setting + " = ? WHERE ID = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Value);
            pstmt.setInt(2, ID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public void updateGame(String Setting, double Value, int ID) {
        String sql = "UPDATE Game SET " + Setting + " = ? WHERE name = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, Value);
            pstmt.setInt(2, ID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public static void onLoad() {
        createNewTables();
    }
}
