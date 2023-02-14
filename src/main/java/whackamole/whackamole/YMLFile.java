package whackamole.whackamole;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YMLFile {
    public FileConfiguration FileConfig = new YamlConfiguration();
    public File file;
    public boolean created = false;

    public YMLFile(String folderString, String child) throws FileNotFoundException {
        this(new File(folderString, child));
    }

    public YMLFile(File folder, String child) throws FileNotFoundException {
        if (!folder.isDirectory())
            throw new FileNotFoundException(Translator.Format(Translator.YML_NOTFOUNDEXCEPTION, folder));
        this.file = new File(folder, child);
        this.load();
    }

    public YMLFile(String path) {
        this(new File(path));
    }

    public YMLFile(File file) {
        this.file = file;
        this.load();
    }

    public String getString(String path) {
        return this.FileConfig.getString(path);
    }

    public boolean getBoolean(String path) {
        return this.FileConfig.getBoolean(path);
    }

    public int getInt(String path) {
        return this.FileConfig.getInt(path);
    }

    public double getDouble(String path) {
        return this.FileConfig.getDouble(path);
    }

    public List<?> getList(String path) {
        return this.FileConfig.getList(path);
    }

    public Sound getSound(String path) {
        return Sound.valueOf(this.FileConfig.getString(path));
    }

    public void set(String path, Object value) {
        this.FileConfig.set(path, value);
    }

    public void save() throws IOException {
        this.FileConfig.save(this.file);
        Logger.success(Translator.YML_SAVEDFILE.Format(this.file));
    }

    public void load() {
        try {
            this.FileConfig.load(this.file);
        } catch (Exception e) {
            this.createFile();
        }
    }

    public void remove() {
        this.file.delete();
        Logger.success(Translator.YML_DELETEDFILE.Format(this.file));
    }

    public void createFile() {
        try {
            if (this.file.getParentFile().mkdirs()) {
                this.load();
            }
        } catch (Exception e) {
            Logger.error(Translator.YML_CREATEFAIL.Format(this.file));
            e.printStackTrace();
        }
    }

}
