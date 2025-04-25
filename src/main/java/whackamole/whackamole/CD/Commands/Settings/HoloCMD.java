package whackamole.whackamole.CD.Commands.Settings;

import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Commands.Settings.Holo.HoloCreateCMD;
import whackamole.whackamole.CD.Commands.Settings.Holo.HoloRemoveCMD;
import whackamole.whackamole.CD.Commands.Settings.Holo.HoloSelectCMD;
import whackamole.whackamole.CD.Commands.Settings.Holo.HoloToggleCMD;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Translator;

public class HoloCMD extends SubCommand {
  @Override
  protected String GetName() {
    return Translator.COMMANDS_HOLO.Format();
  }

  @Override
  protected @Nullable String Permission() {
    return "wam.settings.holo";
  }

  @Override
  protected SubCommand[] SubCommands() {
    return new SubCommand[]{
            new HoloCreateCMD(),
            new HoloRemoveCMD(),
            new HoloSelectCMD(),
            new HoloToggleCMD()
    };
  }
}
