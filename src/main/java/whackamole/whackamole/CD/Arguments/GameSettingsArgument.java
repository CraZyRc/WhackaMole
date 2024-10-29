package whackamole.whackamole.CD.Arguments;

import java.util.Arrays;

import org.bukkit.block.BlockFace;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import whackamole.whackamole.Utils.Translator;

public class GameSettingsArgument extends Arguments {
    public enum GameSetting {
        NULL,
        DIRECTION(Translator.COMMANDS_SETTINGS_DIRECTION),
        HASJACKPOT(Translator.COMMANDS_SETTINGS_JACKPOT),
        JACKPOTSPAWNCHANCE(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE),
        MISSCOUNT(Translator.COMMANDS_SETTINGS_MAXMISSED),
        SCOREPOINTS(Translator.COMMANDS_SETTINGS_SCOREPOINTS),
        SPAWNTIMER(Translator.COMMANDS_SETTINGS_SPAWNRATE),
        SPAWNCHANCE(Translator.COMMANDS_SETTINGS_SPAWNCHANCE),
        MOLESPEED(Translator.COMMANDS_SETTINGS_MOLESPEED),
        DIFFICULTYSCALE(Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE),
        DIFFICULTYSCORE(Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE),
        COOLDOWN(Translator.COMMANDS_SETTINGS_COOLDOWN),
        MUSIC(Translator.COMMANDS_SETTINGS_MUSIC),
        MOLEHEAD(Translator.COMMANDS_SETTINGS_MOLEHEAD),
        JACKPOTHEAD(Translator.COMMANDS_SETTINGS_JACKPOTHEAD),
        TOGGLESCOREBOARD(Translator.COMMANDS_SETTINGS_TOGGLESCOREBOARD);

        Translator value;
        String message;
        GameSetting() {}
        GameSetting(Translator settings) {
            this.value = settings;
            this.message = settings.Format();
        }

        @Override
        public String toString() {
            return this.message;
        }

        static GameSetting get(String input) {
            for(var i : values()) {
                if (i.message.equals(input)) {
                    return i;
                }
            }
            return NULL;
        }
    }

    public static Argument<GameSetting> GameSettingKeys() {
        return new CustomArgument<GameSetting, String>(new StringArgument("Setting"), Info -> {
            return GameSetting.get(Info.input());
        }).replaceSuggestions(ArgumentSuggestions.strings(Arrays.asList(GameSetting.values()).stream().map(GameSetting::toString).toList()));
    }


    public static Argument<Object> GameSettingsValues() {
        return new CustomArgument<Object, String>(new StringArgument("SettingValue"), Info -> {
            var setting = Info.previousArgs().<GameSetting>getUnchecked("setting");
            if (setting == null) throw InputError(Translator.COMMANDS_ARGUMENTS_INVALIDSETTING);

            switch (setting) {
                case COOLDOWN:              return (Info.input());
                case DIFFICULTYSCALE:       return (Double.parseDouble(Info.input()));
                case DIFFICULTYSCORE:       return (Integer.parseInt(Info.input()));
                case DIRECTION:             return (BlockFace.valueOf(Info.input()));
                case HASJACKPOT:            return (Boolean.parseBoolean(Info.input()));
                case JACKPOTHEAD:           return (Info.input());
                case JACKPOTSPAWNCHANCE:    return (Integer.parseInt(Info.input()));
                case MISSCOUNT:             return (Integer.parseInt(Info.input()));
                case MOLEHEAD:              return (Info.input());
                case MOLESPEED:             return (Double.parseDouble(Info.input()));
                case MUSIC:                 return (Info.input());
                case SCOREPOINTS:           return (Integer.parseInt(Info.input()));
                case SPAWNCHANCE:           return (Double.parseDouble(Info.input()));
                case SPAWNTIMER:            return (Double.parseDouble(Info.input()));
                case TOGGLESCOREBOARD:      return (Boolean.parseBoolean(Info.input()));
                default:
                    throw InputError(Translator.COMMANDS_ARGUMENTS_INVALIDSETTING);
            }
        }); // TODO: Add suggestions
    }
}
