package whackamole.whackamole.CD.Arguments;

import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.GS.GamesManager;
import whackamole.whackamole.Utils.Translator;

import java.util.ArrayList;
import java.util.List;

public class Arguments {
    private final static GamesManager Manager = GamesManager.getInstance();

    /**
     * Custom argument to get a game from a game name.
     * Argument name: {@code Game}
     * @return Argument<Game>
     */
    public static Argument<Game> Games() {
        return new CustomArgument<Game, String>(new StringArgument("Game"), info -> {
            for (var game : Manager.games) {
                if (game.getName().equals(info.input())) {
                    return game;
                }
            }
            throw CustomArgumentException.fromString(Translator.COMMANDS_ARGUMENTS_UNKNOWNGAMENAME.Format(info.input()));
        }).replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(Info -> {
            List<IStringTooltip> IDS = new ArrayList<>();
            for (var game : Manager.games) {
                IDS.add(StringTooltip.ofString(game.getName(), Translator.COMMANDS_TIPS_NAME.Format()));
            }
            return IDS.toArray(new IStringTooltip[0]);
        }));
    }

    /**
     * Custom Argument for retrieving Holo ID.
     * @return Argument<Integer>
     */
    public static Argument<Integer> holoIDArgument() {
        return new IntegerArgument("holoID").replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(Info -> {
            Game game = (Game) Info.previousArgs().get(0);
            List<IStringTooltip> IDS = new ArrayList<>();
            for (var v : game.holos) {
                IDS.add(StringTooltip.ofString(String.valueOf(v.holoID), Translator.COMMANDS_TIPS_HOLOID.Format()));
            }
            return IDS.toArray(new IStringTooltip[0]);
        }));
    }


    protected static CustomArgumentException InputError(String message) {
        String arg = new CustomArgument.MessageBuilder().appendArgInput().toString();
        return CustomArgument.CustomArgumentException.fromString(message);
    }
}
