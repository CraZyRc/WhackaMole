package whackamole.whackamole;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;


public final class Logger {
    public static String Prefix;

    private Logger() {
    }

    public static void onLoad(Plugin Main) {
        Prefix = Main.getDescription().getPrefix();
    }

    private static void sendConsoleMessage(String message) {
        try {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Logger.Prefix + "] " + message);
        } catch (Exception e) { 
            // * Primarily for testing purposes, or when it fails to load in Bukkit
            System.out.println(message);
        }
    }

    public static void info(String message) {
        sendConsoleMessage(ChatColor.WHITE + message);
    }

    public static void warning(String message) {
        sendConsoleMessage(Translator.LOGGER_WARNING.Format(message));
    }

    public static void error(String message) {
        sendConsoleMessage(Translator.LOGGER_ERROR.Format(message));
    }

    public static void success(String message) {
        sendConsoleMessage(ChatColor.GREEN + message);
    }

    public static void info(Translator message) {
        sendConsoleMessage(ChatColor.WHITE + message.toString());
    }

    public static void warning(Translator message) {
        sendConsoleMessage(Translator.LOGGER_WARNING.Format(message.toString()));
    }

    public static void error(Translator message) {
        sendConsoleMessage(Translator.LOGGER_ERROR.Format(message.toString()));
    }

    public static void success(Translator message) {
        sendConsoleMessage(ChatColor.GREEN + message.toString());
    }
}
