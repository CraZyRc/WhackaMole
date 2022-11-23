package whackamole.whackamole;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Logger {
    public static String Prefix;
    private static Logger instance;

    private Logger() {
    }

    public static Logger getInstance() {
        if (Logger.instance == null) {
            Logger.instance = new Logger();
        }
        return Logger.instance;
    }

    private void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Logger.Prefix + "] " + message);
    }

    public void info(String message) {
        this.sendConsoleMessage(ChatColor.WHITE + message);
    }

    public void warning(String message) {
        this.sendConsoleMessage(ChatColor.YELLOW + message);
    }

    public void error(String message) {
        this.sendConsoleMessage(ChatColor.DARK_RED + message);
    }

    public void success(String message) {
        this.sendConsoleMessage(ChatColor.GREEN + message);
    }
}
