package whackamole.whackamole;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class Updater {
    private final JavaPlugin plugin;
    private final int resourceId;

    public Updater(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;

        checkVersion();
    }

    public void checkVersion() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                    Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    if(Updater.versionCompare(this.plugin.getDescription().getVersion(), scanner.next())) {
                        Logger.warning(Translator.MAIN_OLDVERSION);
                    }
                }
            } catch (IOException exception) {
                Logger.info(Translator.UPDATEFAIL.Format(exception.getMessage()));
            }
        });
    }

    private static int versionToNumber(String version) {
        int out = 0;

        if (version == null || version.isEmpty()) {
            return out;
        }
        
        String[] versionList = version.split("[.]", 0);
        for (var i = versionList.length -1 ; i >= 0; i--) {
            out += Integer.parseInt(versionList[i]) << (8* (versionList.length - i));
        }

        return out;

    }

    /**
     * Compares to version strings.
     * 
     * @param currentVersion
     * @param compareVersion
     * @return True when the compare version is newer, else False
     */
    public static boolean versionCompare(String currentVersion, String compareVersion) {
        int currentVersionNumber = Updater.versionToNumber(currentVersion);
        int compareVersionNumber = Updater.versionToNumber(compareVersion);
        
        return currentVersionNumber < compareVersionNumber;

    }
}
