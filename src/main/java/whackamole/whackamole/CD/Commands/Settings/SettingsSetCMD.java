package whackamole.whackamole.CD.Commands.Settings;

import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Commands.SubCommand;
import whackamole.whackamole.Config;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

public class SettingsSetCMD extends SubCommand {
  enum Settings {
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
    Settings() {}
    Settings(Translator settings) {
      this.value = settings;
    }

    @Override
    public String toString() {
      return this.value.Format();
    }
  }

  @Override
  protected String GetName() { return Translator.COMMANDS_SETTINGS_SET.Format(); }

  @Override
  protected String Permission() {
    return "wam.settings.set";
  }

  @Override
  protected Argument<?>[] Arguments() {
    return new Argument[] {
            Arguments.Games(),
            this.settingNameArgument(),
            this.settingValueArgument()

    };
  }

  @Override
  protected @Nullable CommandExecutor Executes() {
    return ((sender, args) -> {
      var game = args.<Game>getUnchecked(0);
      var name = args.<Settings>getUnchecked(1);
      var value = args.<String>getUnchecked(2);

      if (game == null || name == null || value == null) { return; }

      switch (name) {
        case NULL -> Logger.error(Translator.COMMANDS_ARGUMENTS_INVALIDSETTING);
        case DIRECTION -> {
          game.setSpawnRotation(BlockFace.valueOf(value));
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_DIRECTION_SUCCESS.Format(value));
        }
        case HASJACKPOT -> {
          game.setJackpot(Boolean.parseBoolean(value));
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOT_SUCCESS.Format(value));
        }
        case JACKPOTSPAWNCHANCE -> {
          if (Integer.parseInt(value) <= 100) {
            game.setJackpotSpawn(Integer.parseInt(value));
            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_SUCCESS.Format(value));
          } else {
            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR);
          }
        }
        case MISSCOUNT -> {
          game.setMaxMissed(Integer.parseInt(value));
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MAXMISSED_SUCCESS.Format(value));
        }
        case SCOREPOINTS -> {
          game.setPointsPerKill (Integer.parseInt(value));
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_SCOREPOINTS_SUCCESS.Format(value));
        }
        case SPAWNTIMER -> {
          game.setInterval (Double.parseDouble(value));
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_SPAWNRATE_SUCCESS.Format(value));
        }
        case SPAWNCHANCE -> {
          if (Double.parseDouble(value) <= 100) {
            game.setSpawnChance(Double.parseDouble(value));
            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_SPAWNCHANCE_SUCCESS.Format(value));
          } else {
            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR);
          }
        }
        case MOLESPEED -> {
          game.setMoleSpeed(Double.parseDouble(value));
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MOLESPEED_SUCCESS.Format(value));
        }
        case DIFFICULTYSCALE -> {
          game.setDifficultyScale(Double.parseDouble(value));
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE_SUCCESS.Format(value));
        }
        case DIFFICULTYSCORE -> {
          game.setDifficultyScore(Integer.parseInt(value));
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE_SUCCESS.Format(value));
        }
        case COOLDOWN -> {
          game.setCooldown(value);
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_COOLDOWN_SUCCESS.Format(value));
        }
        case MUSIC -> {
          game.setMusic(value);
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MUSIC_SUCCESS.Format(value));
        }
        case MOLEHEAD -> {
          game.setMoleHead(value);
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MOLEHEAD_SUCCESS.Format(value));
        }
        case JACKPOTHEAD -> {
          game.setJackpotHead(value);
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTHEAD_SUCCESS.Format(value));
        }
        case TOGGLESCOREBOARD -> {
          game.setToggleScoreboard(Boolean.parseBoolean(value));
          sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_TOGGLESCOREBOARD_SUCCESS.Format(value));
        }
      }
    });
  }

  private Argument<Settings> settingNameArgument() {
    return new CustomArgument<>(new TextArgument("Settings"), Info -> {
      if (Settings.DIRECTION.toString().equals(Info.input()))                 return Settings.DIRECTION;
      if (Settings.HASJACKPOT.toString().equals(Info.input()))                return Settings.HASJACKPOT;
      if (Settings.JACKPOTSPAWNCHANCE.toString().equals(Info.input()))        return Settings.JACKPOTSPAWNCHANCE;
      if (Settings.MISSCOUNT.toString().equals(Info.input()))                 return Settings.MISSCOUNT;
      if (Settings.SCOREPOINTS.toString().equals(Info.input()))               return Settings.SCOREPOINTS;
      if (Settings.SPAWNTIMER.toString().equals(Info.input()))                return Settings.SPAWNTIMER;
      if (Settings.SPAWNCHANCE.toString().equals(Info.input()))               return Settings.SPAWNCHANCE;
      if (Settings.MOLESPEED.toString().equals(Info.input()))                 return Settings.MOLESPEED;
      if (Settings.DIFFICULTYSCALE.toString().equals(Info.input()))           return Settings.DIFFICULTYSCALE;
      if (Settings.DIFFICULTYSCORE.toString().equals(Info.input()))           return Settings.DIFFICULTYSCORE;
      if (Settings.COOLDOWN.toString().equals(Info.input()))                  return Settings.COOLDOWN;
      if (Settings.MUSIC.toString().equals(Info.input()))                     return Settings.MUSIC;
      if (Settings.MOLEHEAD.toString().equals(Info.input()))                  return Settings.MOLEHEAD;
      if (Settings.JACKPOTHEAD.toString().equals(Info.input()))               return Settings.JACKPOTHEAD;
      if (Settings.TOGGLESCOREBOARD.toString().equals(Info.input()))          return Settings.TOGGLESCOREBOARD;
      return Settings.NULL;

    }).replaceSuggestions(ArgumentSuggestions.strings(
              Translator.COMMANDS_SETTINGS_DIRECTION.Format()
            , Translator.COMMANDS_SETTINGS_JACKPOT.Format()
            , Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE.Format()
            , Translator.COMMANDS_SETTINGS_MAXMISSED.Format()
            , Translator.COMMANDS_SETTINGS_SCOREPOINTS.Format()
            , Translator.COMMANDS_SETTINGS_SPAWNRATE.Format()
            , Translator.COMMANDS_SETTINGS_SPAWNCHANCE.Format()
            , Translator.COMMANDS_SETTINGS_MOLESPEED.Format()
            , Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE.Format()
            , Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE.Format()
            , Translator.COMMANDS_SETTINGS_COOLDOWN.Format()
            , Translator.COMMANDS_SETTINGS_MUSIC.Format()
            , Translator.COMMANDS_SETTINGS_MOLEHEAD.Format()
            , Translator.COMMANDS_SETTINGS_JACKPOTHEAD.Format()
            , Translator.COMMANDS_SETTINGS_TOGGLESCOREBOARD.Format()
    ));
  }

  private Argument<String> settingValueArgument() {
    return new TextArgument("settingValue").replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(info -> {
      var settingType = (Settings) info.previousArgs().get(1);
      if (settingType == null) { settingType = Settings.NULL; }

      switch (settingType) {
        case NULL -> Logger.error(Translator.COMMANDS_ARGUMENTS_INVALIDSETTING);
        case DIRECTION -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("NORTH",             Translator.COMMANDS_TIPS_DIRECTION.Format()),
                  StringTooltip.ofString("NORTH_EAST",        Translator.COMMANDS_TIPS_DIRECTION.Format()),
                  StringTooltip.ofString("EAST",              Translator.COMMANDS_TIPS_DIRECTION.Format()),
                  StringTooltip.ofString("SOUTH_EAST",        Translator.COMMANDS_TIPS_DIRECTION.Format()),
                  StringTooltip.ofString("SOUTH",             Translator.COMMANDS_TIPS_DIRECTION.Format()),
                  StringTooltip.ofString("SOUTH_WEST",        Translator.COMMANDS_TIPS_DIRECTION.Format()),
                  StringTooltip.ofString("WEST",              Translator.COMMANDS_TIPS_DIRECTION.Format()),
                  StringTooltip.ofString("NORTH_WEST",        Translator.COMMANDS_TIPS_DIRECTION.Format())
          };
        }
        case HASJACKPOT -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("true",              Translator.COMMANDS_TIPS_JACKPOT.Format()),
                  StringTooltip.ofString("false",             Translator.COMMANDS_TIPS_JACKPOT.Format())
          };
        }
        case JACKPOTSPAWNCHANCE -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("1",                 Translator.COMMANDS_TIPS_JACKPOTSPAWNS.Format()),
                  StringTooltip.ofString("10",                Translator.COMMANDS_TIPS_JACKPOTSPAWNS.Format()),
                  StringTooltip.ofString("50",                Translator.COMMANDS_TIPS_JACKPOTSPAWNS.Format()),
                  StringTooltip.ofString("100",               Translator.COMMANDS_TIPS_JACKPOTSPAWNS.Format())
          };
        }
        case MISSCOUNT -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("1",                 Translator.COMMANDS_TIPS_MAXMISSED.Format()),
                  StringTooltip.ofString("2",                 Translator.COMMANDS_TIPS_MAXMISSED.Format()),
                  StringTooltip.ofString("3",                 Translator.COMMANDS_TIPS_MAXMISSED.Format()),
                  StringTooltip.ofString("600",               Translator.COMMANDS_TIPS_MAXMISSED.Format())
          };
        }
        case SCOREPOINTS -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("1",                 Translator.COMMANDS_TIPS_HITPOINTS.Format()),
                  StringTooltip.ofString("2",                 Translator.COMMANDS_TIPS_HITPOINTS.Format()),
                  StringTooltip.ofString("3",                 Translator.COMMANDS_TIPS_HITPOINTS.Format()),
                  StringTooltip.ofString("600",               Translator.COMMANDS_TIPS_HITPOINTS.Format())
          };
        }
        case SPAWNTIMER -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("1",                 Translator.COMMANDS_TIPS_INTERVAL.Format()),
                  StringTooltip.ofString("2",                 Translator.COMMANDS_TIPS_INTERVAL.Format()),
                  StringTooltip.ofString("3",                 Translator.COMMANDS_TIPS_INTERVAL.Format()),
                  StringTooltip.ofString("600",               Translator.COMMANDS_TIPS_INTERVAL.Format())
          };
        }
        case SPAWNCHANCE -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("1",                 Translator.COMMANDS_TIPS_SPAWNCHANCE.Format()),
                  StringTooltip.ofString("10",                Translator.COMMANDS_TIPS_SPAWNCHANCE.Format()),
                  StringTooltip.ofString("50",                Translator.COMMANDS_TIPS_SPAWNCHANCE.Format()),
                  StringTooltip.ofString("100",               Translator.COMMANDS_TIPS_SPAWNCHANCE.Format())
          };
        }
        case MOLESPEED -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("0.5",               Translator.COMMANDS_TIPS_MOLESPEED.Format()),
                  StringTooltip.ofString("1",                 Translator.COMMANDS_TIPS_MOLESPEED.Format()),
                  StringTooltip.ofString("2",                 Translator.COMMANDS_TIPS_MOLESPEED.Format()),
                  StringTooltip.ofString("4",                 Translator.COMMANDS_TIPS_MOLESPEED.Format())
          };
        }
        case DIFFICULTYSCALE -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("1",                 Translator.COMMANDS_TIPS_DIFFICULTYSCALE.Format()),
                  StringTooltip.ofString("5",                 Translator.COMMANDS_TIPS_DIFFICULTYSCALE.Format()),
                  StringTooltip.ofString("10",                Translator.COMMANDS_TIPS_DIFFICULTYSCALE.Format()),
                  StringTooltip.ofString("20",                Translator.COMMANDS_TIPS_DIFFICULTYSCALE.Format())
          };
        }
        case DIFFICULTYSCORE -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("1",                 Translator.COMMANDS_TIPS_DIFFICULTYINCREASE.Format()),
                  StringTooltip.ofString("5",                 Translator.COMMANDS_TIPS_DIFFICULTYINCREASE.Format()),
                  StringTooltip.ofString("10",                Translator.COMMANDS_TIPS_DIFFICULTYINCREASE.Format()),
                  StringTooltip.ofString("20",                Translator.COMMANDS_TIPS_DIFFICULTYINCREASE.Format())
          };
        }
        case COOLDOWN -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("\"24:00:00\"",      Translator.COMMANDS_TIPS_COOLDOWN.Format()),
                  StringTooltip.ofString("\"01:00:00\"",      Translator.COMMANDS_TIPS_COOLDOWN.Format()),
                  StringTooltip.ofString("\"00:30:00\"",      Translator.COMMANDS_TIPS_COOLDOWN.Format()),
                  StringTooltip.ofString("\"00:10:00\"",      Translator.COMMANDS_TIPS_COOLDOWN.Format()),
                  StringTooltip.ofString("\"00:00:10\"",      Translator.COMMANDS_TIPS_COOLDOWN.Format())
          };
        }
        case MUSIC -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("\"minecraft:music.dragon\"",                                            Translator.COMMANDS_TIPS_MUSIC.Format()),
                  StringTooltip.ofString("\"minecraft:music.under_water\"",                                       Translator.COMMANDS_TIPS_MUSIC.Format()),
                  StringTooltip.ofString("\"minecraft:music_disc.pigstep\"",                                      Translator.COMMANDS_TIPS_MUSIC.Format()),
                  StringTooltip.ofString("\"minecraft:music_disc.ward\"",                                         Translator.COMMANDS_TIPS_MUSIC.Format())
          };
        }
        case MOLEHEAD -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("eba5bff4334deceaba7b267777d0eb19386a325a0a80de42783d4e4fac7ea41d",      Translator.COMMANDS_TIPS_MOLEHEAD.Format()),
                  StringTooltip.ofString("1280329dc03ae541a7b3fb7e1cfe63a8dd85f3ecb97f353b092ad6c0e0fb1c95",      Translator.COMMANDS_TIPS_MOLEHEAD.Format()),
                  StringTooltip.ofString("cc1df0db8d4773cc255a63b8f0b0e4cdd7d74c3b4c675362168460bc67bb302e",      Translator.COMMANDS_TIPS_MOLEHEAD.Format()),
                  StringTooltip.ofString("ebda5f31937b2ff755271d97f01be84d52a407b36ca77451856162ac6cfbb34f",      Translator.COMMANDS_TIPS_MOLEHEAD.Format()),
          };
        }
        case JACKPOTHEAD -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("a50f09a9acbb71b696c6b51cc9f5f51c5125e8001c44a4963564b15da08df254",      Translator.COMMANDS_TIPS_MOLEHEAD.Format()),
                  StringTooltip.ofString("572c1040fd40e4e83a5be50edcb1cd037c9c66b9d0c9171e7080a02d3f4cfbf",       Translator.COMMANDS_TIPS_MOLEHEAD.Format()),
                  StringTooltip.ofString("5d8e1ebc83f0f5662821bc6600981e17f3ce26b2574edf67de228c749481f230",      Translator.COMMANDS_TIPS_MOLEHEAD.Format()),
                  StringTooltip.ofString("95fd67d56ffc53fb360a17879d9b5338d7332d8f129491a5e17e8d6e8aea6c3a",      Translator.COMMANDS_TIPS_MOLEHEAD.Format()),
          };
        }
        case TOGGLESCOREBOARD -> {
          return new IStringTooltip[] {
                  StringTooltip.ofString("true",                                                                  Translator.COMMANDS_TIPS_TOGGLESCOREBOARD.Format()),
                  StringTooltip.ofString("false",                                                                 Translator.COMMANDS_TIPS_TOGGLESCOREBOARD.Format())
          };
        }
      }
      return new IStringTooltip[] {StringTooltip.ofString("","")};
    }));
  }

}
