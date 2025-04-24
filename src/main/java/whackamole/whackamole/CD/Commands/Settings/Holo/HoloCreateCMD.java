package whackamole.whackamole.CD.Commands.Settings.Holo;

import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class HoloCreateCMD extends SubCommand {
  @Override
  protected String GetName() { return Translator.COMMANDS_HOLO_CREATE.Format(); }

  @Override
  protected @Nullable String Permission() {
    return Config.Permissions.PERM_SETTINGS_HOLO_CREATE;
  }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[] {
            Arguments.Games(),
            new IntegerArgument(Translator.COMMANDS_HOLO_CREATE_HOLOID.Format()).replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(
                            StringTooltip.ofString("1",                                           Translator.COMMANDS_TIPS_HOLO_CREATE_HOLOID.Format())
                    ,       StringTooltip.ofString("2",                                           Translator.COMMANDS_TIPS_HOLO_CREATE_HOLOID.Format())
                    ,       StringTooltip.ofString("3",                                           Translator.COMMANDS_TIPS_HOLO_CREATE_HOLOID.Format())
            )),
            new StringArgument(Translator.COMMANDS_HOLO_CREATE_TYPE.Format()).replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(
                            StringTooltip.ofString(Translator.HOLOGRAM_ARMORSTANDTYPE_SCORE.Format(),      Translator.COMMANDS_TIPS_HOLO_CREATE_TYPE1.Format())
                    ,       StringTooltip.ofString(Translator.HOLOGRAM_ARMORSTANDTYPE_STREAK.Format(),     Translator.COMMANDS_TIPS_HOLO_CREATE_TYPE2.Format())
                    ,       StringTooltip.ofString(Translator.HOLOGRAM_ARMORSTANDTYPE_MOLESHIT.Format(),   Translator.COMMANDS_TIPS_HOLO_CREATE_TYPE3.Format())
            )),
            new IntegerArgument(Translator.COMMANDS_HOLO_CREATE_ROWNUMBERS.Format()).replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(
                            StringTooltip.ofString("3",                                           Translator.COMMANDS_TIPS_HOLOCREATEROW.Format())
                    ,       StringTooltip.ofString("5",                                           Translator.COMMANDS_TIPS_HOLOCREATEROW.Format())
                    ,       StringTooltip.ofString("10",                                          Translator.COMMANDS_TIPS_HOLOCREATEROW.Format())
            ))
    };
  }

  @Override
  protected @Nullable PlayerCommandExecutor ExecutesPlayer() {
    return (sender, args) -> {
      Game game = (Game) args.get(0);
      if (game.holoCreate((int) args.get(1), (String) args.get(2), sender.getLocation() , (int) args.get(3))) {
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_HOLO_CREATE_SUCCESS);
      } else {
        sender.sendMessage(Config.AppConfig.PREFIX + Translator.Format(Translator.COMMANDS_HOLO_CREATE_ERROR, String.valueOf(args.get(1))));
      }
    };
  }


}
