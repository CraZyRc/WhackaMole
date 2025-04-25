package whackamole.whackamole.CD.Commands;

import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Commands.Game.*;
import whackamole.whackamole.Config;
import whackamole.whackamole.Utils.Translator;

public class GameCMD extends SubCommand {

  protected String GetName() {
    return Translator.COMMANDS_GAME.Format();
  }

  @Override
  protected @Nullable String Permission() { return "wam.game"; }

  protected SubCommand[] SubCommands() {
    return new SubCommand[] {
            new GameCreateCMD(),
            new GameRemoveCMD(),
            new GameStartCMD(),
            new GameStopCMD(),
            new GameToggleCMD(),
            new GameTopCMD()
    };
  }
}
