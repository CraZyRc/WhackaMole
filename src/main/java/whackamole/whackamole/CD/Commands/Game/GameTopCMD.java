package whackamole.whackamole.CD.Commands.Game;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Translator;

public class GameTopCMD extends SubCommand {
  @Override
  protected String GetName() { return Translator.COMMANDS_TOP.Format(); }

  @Override
  protected @Nullable String Permission() { return "wam.game.top"; }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[] {
            Arguments.Games(),
            new CustomArgument<>(new StringArgument("Type"), Info -> {
      Game game = (Game) Info.previousArgs().get(0);
      String line = ChatColor.YELLOW + "\n| ";
      StringBuilder outputString = new StringBuilder(ChatColor.YELLOW + "\n[>------------------------------------<]\n" +
              "|" + ChatColor.WHITE + " Game: " + ChatColor.AQUA + ChatColor.BOLD + game.getName());

      switch (Info.input()) {
        case "score" -> {
          var score = game.getScoreboard().getTop(0);
          outputString.append(line).append(ChatColor.WHITE).append("Type: ").append(ChatColor.GOLD).append("Score").append(line);
          for (int i = 0; i < score.length; i++) {
            outputString.append(line).append(ChatColor.DARK_AQUA).append(i + 1).append(". ").append(ChatColor.WHITE).append(score[i].player.getName()).append(" : ").append(ChatColor.AQUA).append(score[i].Score).append(ChatColor.WHITE).append(", ").append(ChatColor.YELLOW).append(score[i].Datetime.toLocalDate().toString());
          }

        }
        case "streak" -> {
          var score = game.getScoreboard().getTop(1);
          outputString.append(line).append(ChatColor.WHITE).append("Type: ").append(ChatColor.GOLD).append("Streak").append(line);
          for (int i = 0; i < score.length; i++) {
            outputString.append(line).append(ChatColor.DARK_AQUA).append(i + 1).append(". ").append(ChatColor.WHITE).append(score[i].player.getName()).append(" : ").append(ChatColor.AQUA).append(score[i].scoreStreak).append(ChatColor.WHITE).append(", ").append(ChatColor.YELLOW).append(score[i].Datetime.toLocalDate().toString());
          }

        }
        case "moles" -> {
          var score = game.getScoreboard().getTop(2);
          outputString.append(line).append(ChatColor.WHITE).append("Type: ").append(ChatColor.GOLD).append("Moles").append(line);
          for (int i = 0; i < score.length; i++) {
            outputString.append(line).append(ChatColor.DARK_AQUA).append(i + 1).append(". ").append(ChatColor.WHITE).append(score[i].player.getName()).append(" : ").append(ChatColor.AQUA).append(score[i].molesHit).append(ChatColor.WHITE).append(", ").append(ChatColor.YELLOW).append(score[i].Datetime.toLocalDate().toString());
          }
        }
      }
      outputString.append(ChatColor.YELLOW + "\n| \n[>------------------------------------<]");
      return outputString.toString();

    }).replaceSuggestions(ArgumentSuggestions.strings("score", "streak", "moles"))
    };
  }

  @Override
  protected CommandExecutor Executes() {
    return (sender, args) -> {
      sender.sendMessage((String) args.get(1));
    };
  }
}
