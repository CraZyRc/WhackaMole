package whackamole.whackamole.CD.Commands;

import org.bukkit.block.BlockFace;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import whackamole.whackamole.Config;
import whackamole.whackamole.CD.SubCommand;
import whackamole.whackamole.CD.Arguments.Arguments;
import whackamole.whackamole.CD.Arguments.GameSettingsArgument;
import whackamole.whackamole.GS.Game;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

public class GameSettingSetCommand extends SubCommand {

    @Override
    protected String GetName() {
        return Translator.COMMANDS_SETTINGS.Format();
    }

    @Override
    protected String Permission() {
        return Config.Permissions.PERM_SETTINGS;
    }

    @Override
    protected Argument<?>[] Arguments() {
        return new Argument[] {
            Arguments.Games(),
            GameSettingsArgument.GameSettingKeys(),
            GameSettingsArgument.GameSettingsValues(),
        };
    }

    @Override
    protected PlayerCommandExecutor ExecutesPlayer() {
        return (sender, args) -> {
            var game = args.<Game>getUnchecked("Game");
            var setting = args.<GameSettingsArgument.GameSetting>getUnchecked("GameSetting");
            if (game == null || setting == null) return;

            switch (setting) {
                case COOLDOWN -> {
                    var value = args.<String>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setCooldown(value);
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_COOLDOWN_SUCCESS.Format(value.toString()));
                    }
                }
                case DIFFICULTYSCALE -> {
                    var value = args.<Double>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setDifficultyScale(value);
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_DIFFICULTYSCALE_SUCCESS.Format(value.toString()));
                    }
                }
                case DIFFICULTYSCORE -> {
                    var value = args.<Integer>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setDifficultyScore(value);
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_DIFFICULTYINCREASE_SUCCESS.Format(value.toString()));
                    }
                }
                case DIRECTION -> {
                    var value = args.<BlockFace>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setSpawnRotation(value);
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_DIRECTION_SUCCESS.Format(value.toString()));
                    }
                }
                case HASJACKPOT -> {
                    var value = args.<Boolean>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setJackpot(value);
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOT_SUCCESS.Format(value.toString()));
                    }
                }
                case JACKPOTHEAD -> {
                    var value = args.<String>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setJackpotHead(value);
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTHEAD_SUCCESS.Format(value.toString()));
                    }
                }
                case JACKPOTSPAWNCHANCE -> {
                    var value = args.<Integer>getUnchecked("GameSettingValue");
                    if (value != null) {
                        if (0 <= value && value <= 100) {
                            game.setJackpotSpawn(value);
                            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_SUCCESS.Format(value.toString()));
                        }
                        else {
                            Logger.error(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE.Format(sender.getDisplayName()));
                            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER);
                        }
                    }
                }
                case MISSCOUNT -> {
                    var value = args.<Integer>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setMaxMissed(value); 
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MAXMISSED_SUCCESS.Format(value.toString()));
                    }
                }
                case MOLEHEAD -> {
                    var value = args.<String>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setMoleHead(value); 
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MOLEHEAD_SUCCESS.Format(value.toString()));
                    }
                }
                case MOLESPEED -> {
                    var value = args.<Double>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setMoleSpeed(value); 
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MOLESPEED_SUCCESS.Format(value.toString()));
                    }
                }
                case MUSIC -> {
                    var value = args.<String>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setMusic(value); 
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_MUSIC_SUCCESS.Format(value.toString()));
                    }
                }
                case SCOREPOINTS -> {
                    var value = args.<Integer>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setPointsPerKill(value); 
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_SCOREPOINTS_SUCCESS.Format(value.toString()));
                    }
                }
                case SPAWNCHANCE -> {
                    var value = args.<Double>getUnchecked("GameSettingValue");
                    if (value != null) {
                        if (0 <= value && value <= 100) {
                            game.setDifficultyScale(value);
                            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_SPAWNCHANCE_SUCCESS.Format(value.toString()));
                        }
                        else {
                            Logger.error(Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_CONSOLE.Format(sender.getDisplayName()));
                            sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_JACKPOTSPAWNCHANCE_ERROR_PLAYER);
                        }
                    }
                }
                case SPAWNTIMER -> {
                    var value = args.<Double>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setInterval(value); 
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_SPAWNRATE_SUCCESS.Format(value.toString()));
                    }
                }
                case TOGGLESCOREBOARD -> {
                    var value = args.<Boolean>getUnchecked("GameSettingValue");
                    if (value != null) {
                        game.setToggleScoreboard(value); 
                        sender.sendMessage(Config.AppConfig.PREFIX + Translator.COMMANDS_SETTINGS_TOGGLESCOREBOARD_SUCCESS.Format(value.toString()));
                    }
                }
                default -> {

                }
            }
        };
    }
}