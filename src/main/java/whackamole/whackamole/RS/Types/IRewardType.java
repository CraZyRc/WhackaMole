package whackamole.whackamole.RS.Types;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

import java.util.LinkedHashMap;

public interface IRewardType {
    
    static IRewardType Load(int threshold, LinkedHashMap<String, ?> Settings) 
    {
        return null;
    }

    boolean Check();
    int getRewardChance();
    int getThreshold();

    void Execute(Player player);

    @Nullable
    static IRewardType Factory(String name, int threshold, LinkedHashMap<String, ?> settings)
    {
        return switch (name) {
            case "Animation"    ->  { yield AnimationType.Load(threshold, settings); }
            case "Item"         ->  { yield ItemType.Load(threshold, settings); }
            case "Currency"     ->  { yield CurrencyType.Load(threshold, settings); }
            case "Effect"       ->  { yield EffectType.Load(threshold, settings); }
            case "Message"      ->  { yield MessageType.Load(threshold, settings); }
            case "Sound"        ->  { yield SoundType.Load(threshold, settings); }
            case "Teleport"     ->  { yield TeleportType.Load(threshold, settings); }
            default -> {
                Logger.error(Translator.REWARDS_INVALIDREWARDTYPE.Format(name));
                yield null;
            }
        };
    }
}
