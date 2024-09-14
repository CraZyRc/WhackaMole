package whackamole.whackamole.CD.Arguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.StringArgument;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.GS.GamesManager;
import whackamole.whackamole.Utils.Translator;

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
                if (game.getName().equals(info.input())) 
                    return game;
            }
            throw InputError(Translator.COMMANDS_ARGUMENTS_UNKNOWNGAMENAME);
        }).replaceSuggestions(ArgumentSuggestions.strings(Manager.games.stream().map(Game::getName).toList()));
    }


    protected static CustomArgumentException InputError(Translator message) {
        String arg = new CustomArgument.MessageBuilder().appendArgInput().toString();
        return CustomArgument.CustomArgumentException.fromString(message.Format(arg));
    }
}
