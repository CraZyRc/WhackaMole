package whackamole.whackamole.GS;

import org.bukkit.entity.Player;
import whackamole.whackamole.DB.CooldownDB;
import whackamole.whackamole.DB.SQLite;

import java.util.HashMap;
import java.util.UUID;

public class CooldownList {
    private final Game game;
    public HashMap<UUID, Long> cooldown = new HashMap<>();
    public CooldownDB db = SQLite.Cooldown;

    public CooldownList(Game game) {
        this.game = game;
    }

    public void onLoad() {
        var list = db.Select(this.game.getID());
        for (var item : list) {
            this.cooldown.put(item.playerID, item.endTimeStamp);
        }
    }

    public void Delete() {
        this.cooldown.forEach((key, value) -> {
            this.db.Delete(this.game.getSettings().ID, key);
        });
        this.cooldown.clear();
    }

    public void add(Player player) {
        this.add(player.getUniqueId(), this.game.settings.Cooldown);
    }

    public void add(UUID player, Long time) {
        this.cooldown.put(player, System.currentTimeMillis() + time);
        this.db.Insert(this.game.getID(), player, System.currentTimeMillis() + time);
    }

    public boolean contains(Player player) {
        return this.contains(player.getUniqueId());
    }

    public boolean contains(UUID player) {
        if(this.cooldown.containsKey(player)) {
            if(this.getTime(player) < System.currentTimeMillis()) {
                this.remove(player);
                return false;
            }
            return true;
        }
        return false;
    }

    public void remove(UUID player) {
        this.cooldown.remove(player);
        this.db.Delete(this.game.getID(), player);
    }

    private Long getTime(UUID player) {
        return this.cooldown.get(player);
    }

    public String getText(UUID player) {
        if (contains(player)) {
            return formatRestCooldown(this.cooldown.get(player));
        }
        return null;
    }

    public Long parseTime(String time) {
        if (time == null || time.isEmpty())
            return 0L;

        String[] fields = time.split(":");
        Long Hour = Long.parseLong(fields[0]) * 3600000;
        Long Minutes = Long.parseLong(fields[1]) * 60000;
        Long Seconds = Long.parseLong(fields[2]) * 1000;
        return Hour + Minutes + Seconds;
    }

    public String formatRestCooldown(long time) {
        time = time - System.currentTimeMillis();
        return formatSetCooldown(time);
    }
    public String formatSetCooldown(long time) {
        int seconds = (int) (Math.floorDiv(time, 1000));
        int minutes = (Math.floorDiv(seconds, 60));
        int hours = (Math.floorDiv(minutes, 60));

        seconds = seconds % 60;
        minutes = minutes % 60;

        return (hours > 0 ? hours > 9 ? String.valueOf(hours) : "0" + hours : "00")
                + ":" + (minutes > 0 ? minutes > 9 ? String.valueOf(minutes) : "0" + minutes : "00")
                + ":" + (seconds > 0 ? seconds > 9 ? String.valueOf(seconds) : "0" + seconds : "00");
    }

    public void walkOnGridHook(Player player) {
        if (!this.contains(player.getUniqueId()))
            return;

        UUID playerUUID = player.getUniqueId();
        if (this.getTime(playerUUID) < System.currentTimeMillis())
            this.remove(playerUUID);
    }

}
