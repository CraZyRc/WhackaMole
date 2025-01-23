package whackamole.whackamole.CD.Commands;

import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Commands.Settings.*;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Translator;

public class SettingsCMD extends SubCommand {

  protected String GetName() {
    return Translator.COMMANDS_SETTINGS.Format();
  }

  @Override
  protected @Nullable String Permission() { return Config.Permissions.PERM_SETTINGS; }

  protected SubCommand[] SubCommands() {
    return new SubCommand[] {
            new SettingsGetCMD(),
            new SettingsSetCMD(),
            new HoloCMD(),
            new PositionsCMD()
    };
  }
}
