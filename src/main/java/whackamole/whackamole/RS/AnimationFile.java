package whackamole.whackamole.RS;

import org.bukkit.plugin.Plugin;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;
import whackamole.whackamole.Utils.YMLFile;

import java.io.*;


public class AnimationFile {
    private static final String[] exampleAnimations = new String[] {"Crown", "Butterfly_wings", "Star", "Dragon_wings", "Smile"};


    public static void loadFiles(Plugin main) {
        File animationFolder = new File(Config.AppConfig.storageFolder + "/animations");
        YMLFile animationFile;


        // * Animations Folder
        if (!animationFolder.exists()) {
            try {
                animationFolder.mkdirs();
                Logger.info("animationfolder created");
            } catch (Exception e) {
                Logger.info("ERROR: Cannot create the Animation folder. Permission denied!");
                return;
            }
        }

        // * Create animation files from resource files
        for (String animation : exampleAnimations) {
            try {
                animationFile = new YMLFile(animationFolder + "/" + animation + ".yml");

                if (animationFile.created) {
                    Logger.info(Translator.YML_CREATEFILE.Format(animationFile));
                    main.saveResource("animations/" + animation + ".yml", true);
                }
            } catch (Exception e) {
                Logger.error(e.getMessage());
                return;
            }
        }
    }
}
