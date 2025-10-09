package whackamole.whackamole.CD.Commands;

import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Commands.Holo.*;
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
            new HoloToggleCMD(),
            new HoloTeleportCMD()
    };
  }
}
