package whackamole.whackamole.CD.Commands;


import whackamole.whackamole.Config;

public class WhackaMoleCMD extends SubCommand {

  protected String GetName() {
    return "whackamole";
  }

  @Override
  protected String Permission() { return "wam"; }

  protected String[] Aliases() {
    return new String[] {
            "wam"
    };
  }

  protected SubCommand[] SubCommands() {
    return new SubCommand[] {
            new GameCMD(),
            new SettingsCMD(),
            new TicketBuyCMD(),
            new PluginReloadCMD()
    };
  }
}
