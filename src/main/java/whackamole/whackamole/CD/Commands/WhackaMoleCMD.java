package whackamole.whackamole.CD.Commands;


public class WhackaMoleCMD extends SubCommand {

  protected String GetName() {
    return "whackamole";
  }

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
