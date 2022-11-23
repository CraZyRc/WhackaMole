package whackamole.whackamole;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Logger {
    public static String Prefix;
    private static Logger Instance;

    private Logger() {
    }

    public static Logger getInstance() {
        if (Logger.Instance == null) {
            Logger.Instance = new Logger();
        }
        return Logger.Instance;
    }

    private void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Logger.Prefix + "] " + message);
    }

    public void info(String message) {
        this.sendConsoleMessage(ChatColor.WHITE + message);
    }

    public void warning(String message) {
        this.sendConsoleMessage(ChatColor.YELLOW + "WARNING: " + message);
    }

    public void error(String message) {
        this.sendConsoleMessage(ChatColor.DARK_RED + "ERROR: " + message);
    }

    public void success(String message) {
        this.sendConsoleMessage(ChatColor.GREEN + message);
    }

    public static Logger reload() {
        Logger.Instance = null;
        return Logger.getInstance();
    }
}
