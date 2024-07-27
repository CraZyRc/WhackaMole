package whackamole.whackamole.RS.Types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import whackamole.whackamole.Main;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

import java.util.LinkedHashMap;

public interface IRewardType {

    IRewardType Load(LinkedHashMap<String, ?> Settings);
    boolean Check();
    int getRewardChance();

    void Execute(Player player);
    void displayType(Main main, Location loc);
    void Remove(Player player);

    @Nullable
    static IRewardType Factory(String name, LinkedHashMap<String, ?> settings)
    {
        return switch (name) {
            case "Item"     ->  { yield new ItemType().Load(settings); }
            case "Currency" ->  { yield new CurrencyType().Load(settings); }
            case "Effect"   ->  { yield new EffectType().Load(settings); }
            case "Message"  ->  { yield new MessageType().Load(settings); }
            case "Sound"    ->  { yield new SoundType().Load(settings); }
            case "Teleport" ->  { yield new TeleportType().Load(settings); }
            default -> {
                Logger.error(Translator.REWARDS_INVALIDREWARDTYPE.Format(name));
                yield null;
            }
        };
    }
}
