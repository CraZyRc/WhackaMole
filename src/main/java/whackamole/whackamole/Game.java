package whackamole.whackamole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import whackamole.whackamole.Mole.MoleState;
import whackamole.whackamole.Mole.MoleType;

public class Game {
    public static final BlockFace[] Directions = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
            BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    public class CooldownList {
        private HashMap<UUID, Long> cooldown = new HashMap<>();

        private void add(Player player) {
            this.add(player.getUniqueId(), parseTime(settings.Cooldown));
        }

        private void add(UUID player, Long time) {
            this.cooldown.put(player, System.currentTimeMillis() + time);
        }

        private boolean contains(Player player) {
            return this.contains(player.getUniqueId());
        }

        private boolean contains(UUID player) {
            return this.cooldown.containsKey(player);
        }

        private void remove(UUID player) {
            if (contains(player)) {
                this.cooldown.remove(player);
            }
        }

        private Long getTime(UUID player) {
            if (contains(player)) {
                return this.cooldown.get(player);
            }
            return null;
        }

        private String getText(UUID player) {
            if (contains(player)) {
                return formatCooldown(this.cooldown.get(player));
            }
            return null;
        }

        private Long parseTime(String time) {
            if (time == null || time.isEmpty())
                return 0L;

            String[] fields = time.split(":");
            Long Hour = Long.parseLong(fields[0]) * 3600000;
            Long Minutes = Long.parseLong(fields[1]) * 60000;
            Long Seconds = Long.parseLong(fields[2]) * 1000;
            return Hour + Minutes + Seconds;
        }

        private String formatCooldown(long time) {
            time = time - System.currentTimeMillis();
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
            else 
                Game.this.sendPlayerCooldownMessage(playerUUID);
        }

        public void walkOffGridHook(Player player) {
            if (!this.contains(player.getUniqueId()))
                return;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder().append("").create());
        }
    }

    public class Settings {
        public BlockFace spawnRotation;
        public String Cooldown = "24:00:00";

        public boolean Jackpot = true;

        public int jackpotSpawn = 1, difficultyScore = 1, pointsPerKill = 1, maxMissed = 3;

        public double Interval = 1, spawnChance = 100, difficultyScale = 10, moleSpeed = 2;

    }

    public class GameFile {
        private YMLFile gameConfig;

        public GameFile() {
            gameConfig = new YMLFile(Config.AppConfig.storageFolder + "/Games/" + name + ".yml");
            this.save();
        }

        public GameFile(YMLFile gameFile) {
            gameConfig = gameFile;
            this.load();
        }

        // @SuppressWarnings("deprecation")
        public void save() {
            List<String> header = Arrays.asList(
                    "############################################################",
                    "# ^------------------------------------------------------^ #",
                    "# |                       GameFile                       | #",
                    "# <------------------------------------------------------> #",
                    "############################################################",
                    "# " + Translator.GAME_CONFIG_FIRSTNOTE,
                    "# " + Translator.GAME_CONFIG_SECONDNOTE,
                    "# " + Translator.GAME_CONFIG_THIRDNOTE,
                    "#",
                    "#",
                    "############################################################",
                    "# ^------------------------------------------------------^ #",
                    "# |                      Explanation                     | #",
                    "# <------------------------------------------------------> #",
                    "############################################################",
                    "# " + Translator.GAME_CONFIG_NAME,
                    "# " + Translator.GAME_CONFIG_DIRECTION,
                    "# " + Translator.GAME_CONFIG_JACKPOT,
                    "# " + Translator.GAME_CONFIG_JACKPOTSPAWN,
                    "# " + Translator.GAME_CONFIG_GAMELOST,
                    "# " + Translator.GAME_CONFIG_POINTSPERKILL,
                    "# " + Translator.GAME_CONFIG_SPAWNRATE,
                    "# " + Translator.GAME_CONFIG_SPAWNCHANCE,
                    "# " + Translator.GAME_CONFIG_MOLESPEED,
                    "# " + Translator.GAME_CONFIG_DIFFICULTYSCALE,
                    "# " + Translator.GAME_CONFIG_DIFFICULTYINCREASE,
                    "# " + Translator.GAME_CONFIG_COOLDOWN,
                    "# " + Translator.GAME_CONFIG_ENDMESSAGE,
                    "# ");

            this.gameConfig.FileConfig.options().setHeader(header);

            this.gameConfig.set("Properties.Name", Game.this.name);
            this.gameConfig.set("Properties.Direction", Game.this.settings.spawnRotation.name());
            this.gameConfig.set("Properties.Jackpot", Game.this.settings.Jackpot);
            this.gameConfig.set("Properties.Jackpot spawn chance", Game.this.settings.jackpotSpawn);
            this.gameConfig.set("Properties.Game lost", Game.this.settings.maxMissed);
            this.gameConfig.set("Properties.Points per kill", Game.this.settings.pointsPerKill);
            this.gameConfig.set("Properties.Spawn rate", Game.this.settings.Interval);
            this.gameConfig.set("Properties.Spawn chance", Game.this.settings.spawnChance);
            this.gameConfig.set("Properties.Mole speed", Game.this.settings.moleSpeed);
            this.gameConfig.set("Properties.Difficulty scaling", Game.this.settings.difficultyScale);
            this.gameConfig.set("Properties.Difficulty increase", Game.this.settings.difficultyScore);
            this.gameConfig.set("Properties.Cooldown", Game.this.settings.Cooldown);
            this.gameConfig.set("Field Data.World", Game.this.grid.world.getName());
            this.gameConfig.set("Field Data.Grid", Game.this.grid.Serialize());
            try {
                this.gameConfig.save();
            } catch (Exception e) {
            }
        }

        public void load() {
            Game.this.world = Bukkit.getWorld(this.gameConfig.getString("Field Data.World"));
            Game.this.grid = Grid.Deserialize(Game.this.world, this.gameConfig.getList("Field Data.Grid"));
            Game.this.name = this.gameConfig.getString("Properties.Name");
            Game.this.settings.spawnRotation = BlockFace.valueOf(this.gameConfig.getString("Properties.Direction"));
            Game.this.settings.Jackpot = this.gameConfig.getBoolean("Properties.Jackpot");
            Game.this.settings.jackpotSpawn = this.gameConfig.getInt("Properties.Jackpot spawn chance");
            Game.this.settings.maxMissed = this.gameConfig.getInt("Properties.Game lost");
            Game.this.settings.pointsPerKill = this.gameConfig.getInt("Properties.Points per kill");
            Game.this.settings.Interval = this.gameConfig.getDouble("Properties.Spawn rate");
            Game.this.settings.spawnChance = this.gameConfig.getDouble("Properties.Spawn chance");
            Game.this.settings.moleSpeed = this.gameConfig.getDouble("Properties.Mole speed");
            Game.this.settings.difficultyScale = this.gameConfig.getDouble("Properties.Difficulty scaling");
            Game.this.settings.difficultyScore = this.gameConfig.getInt("Properties.Difficulty increase");
            Game.this.settings.Cooldown = this.gameConfig.getString("Properties.Cooldown");
            Logger.success(Translator.GAME_LOADSUCCESS.Format(this.gameConfig));
        }

        public void delete() {
            this.gameConfig.remove();
        }

    }

    public class Scoreboard {

        private class Score {
            private Player player;
            private int score;
            private Long timestamp;

            public Score(Player player, int score, Long timestamp) {
                this.player = player;
                this.score = score;
                this.timestamp = timestamp;
            }
        }

        private ArrayList<Score> scores = new ArrayList<>();

        public void add(Player player, int score) {
            this.scores.add(new Score(player, score, System.currentTimeMillis() / 1000));
        }

        public List<Score> getTop() {
            return getTop(10);
        }

        public List<Score> getTop(int count) {
            this.scores.sort((a, b) -> {
                return a.score - b.score;
            });
            return this.scores.subList(0, count);
        }
    }

    public class GameRunner {
        public Player player;
        public int score = 0, missed = 0, difficultyModifier = 0;

        public double moleSpeed = settings.moleSpeed, interval = settings.Interval, spawnChance = settings.spawnChance;

        public GameRunner() {
        }

        public GameRunner(Player player) throws Exception {
            if (!Start(player))
                throw new Exception("Failed to start game");
        }

        private boolean Start(Player player) {
            if (Econ.currencyType == Econ.Currency.NULL) {
                Logger.error(Translator.GAME_INVALIDECONOMY);
                cooldown.add(player.getUniqueId(), 10000L);
                return false;
            }

            if (cooldown.contains(player)
            // || player.hasPermission(Config.Permissions.PERM_PLAY)
            ) {
                return false;
            }

            if (!givePlayerAxe(player)) {
                player.sendMessage(Config.AppConfig.PREFIX + Translator.GAME_START_FULLINVENTORY);
                cooldown.add(player.getUniqueId(), 10000L);
                return false;
            }

            this.player = player;
            return true;
        }

        private void Stop() {
            grid.removeEntities();
            this.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            this.removePlayerAxe(this.player);
            this.sendScoreToPlayer(this.player, this.score);
            econ.depositPlayer(this.player, this.score);

            if (this.score > 0) {
                cooldown.add(this.player);
                scoreboard.add(this.player, this.score);
            }
            this.player = null;
        }

        private boolean givePlayerAxe(Player player) {
            PlayerInventory inventory = player.getInventory();
            if (!inventory.getItemInMainHand().equals(Material.AIR)) {
                if (inventory.firstEmpty() != -1) {
                    inventory.setItem(inventory.firstEmpty(),
                            inventory.getItemInMainHand());
                    inventory.setItemInMainHand(Config.Game.PLAYER_AXE);
                    return true;
                }
            }
            return false;
        }

        private void removePlayerAxe(Player player) {
            player.getInventory().removeItem(Config.Game.PLAYER_AXE);
        }

        private void sendScoreToPlayer(Player player, int score) {
            var message = "";
            if(score == 0) message = Translator.GAME_STOP_REWARD_NONE.Format(); 
            if(score == 1) message = Translator.GAME_STOP_REWARD_SING.Format(Game.this); 
            if(score >  1) message = Translator.GAME_STOP_REWARD_PLUR.Format(Game.this); 
            player.sendMessage(Config.AppConfig.PREFIX + message);
        }

        public void moleHit(Mole mole) {
            switch (mole.type) {
                case Mole:
                    this.score += settings.pointsPerKill;
                    break;
                case Jackpot:
                    this.score += settings.pointsPerKill * 3;
                    break;
                case Null:
                    break;
            }

            this.difficultyModifier++;
            if (this.difficultyModifier >= settings.difficultyScore)
                setSpeedScale();
        }

        private void setSpeedScale() {
            double Scaler = 1 + (settings.difficultyScale / 100);
            this.moleSpeed = this.moleSpeed / Scaler;
            this.interval = this.interval / Scaler;
            this.spawnChance = Math.max(0, Math.min(100, this.spawnChance * Scaler));
            this.difficultyModifier = 0;
        }

    }

    private Econ econ = new Econ();
    private Settings settings = new Settings();
    private CooldownList cooldown = new CooldownList();
    private Scoreboard scoreboard = new Scoreboard();
    private GameFile gameFile;
    private GameRunner game;

    private List<UUID> currentyOnGird = new ArrayList<>();
    private boolean disabled = false;

    public String name;
    private World world;
    private Grid grid;

    private Random random = new Random();

    public Game(YMLFile configFile) {
        this.gameFile = new GameFile(configFile);
    }

    public Game(String name, Grid grid, Player player) {
        this.name = formatName(name);
        this.settings.spawnRotation = Directions[Math.round(player.getLocation().getYaw() / 45) & 0x7];
        this.grid = grid;
        this.gameFile = new GameFile();
    }

    private String formatName(String name) {
        char[] chars = name.toLowerCase().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public void Start(Player player) {
        try {
            if (this.game != null)
                return;
            this.game = new GameRunner(player);
        } catch (Exception e) {
            this.game = null;
        }
    }

    public void Stop() {
        if (this.game != null) {
            this.game.Stop();
            this.game = null;
        }
    }

    public void unload() {
        this.Stop();
    }

    public void delete() {
        if (this.gameFile != null)
            this.gameFile.delete();
    }

    public GameRunner getRunning() {
        if (this.game != null) {
            return this.game;
        }
        return new GameRunner();
    }

    public Settings getSettings() {
        return this.settings;
    }

    public void setJackpotSpawn(int jackpotSpawn) {
        this.settings.jackpotSpawn = jackpotSpawn;
    }

    public void setDifficultyScore(int difficultyScore) {
        this.settings.difficultyScore = difficultyScore;
    }

    public void setPointsPerKill(int pointsPerKill) {
        this.settings.pointsPerKill = pointsPerKill;
    }

    public void setMaxMissed(int maxMissed) {
        this.settings.maxMissed = maxMissed;
    }

    public void setInterval(double Interval) {
        this.settings.Interval = Interval;
    }

    public void setSpawnChance(double spawnChance) {
        this.settings.spawnChance = spawnChance;
    }

    public void setDifficultyScale(double difficultyScale) {
        this.settings.difficultyScale = difficultyScale;
    }

    public void setMoleSpeed(double moleSpeed) {
        this.settings.moleSpeed = moleSpeed;
    }

    public void setJackpot(boolean Jackpot) {
        this.settings.Jackpot = Jackpot;
    }

    public void setCooldown(String Cooldown) {
        this.settings.Cooldown = Cooldown;
    }

    public void setSpawnRotation(BlockFace spawnRotation) {
        this.settings.spawnRotation = spawnRotation;
    }

    public void onPlayerExit(Player player) {
        if (this.currentyOnGird.contains(player.getUniqueId()))
            this.currentyOnGird.remove(player.getUniqueId());

        if (this.game != null && this.game.player == player)
            this.Stop();
    }

    public boolean onGrid(Player player) {
        boolean playerOnGrid = this.grid.onGrid(player);

        // * Player walks on grid
        if (playerOnGrid && !currentyOnGird.contains(player.getUniqueId())) {
            currentyOnGird.add(player.getUniqueId());
            this.Start(player);
            this.cooldown.walkOnGridHook(player);
        }

        // * Player walks of grid
        if (!playerOnGrid && currentyOnGird.contains(player.getUniqueId())) {
            currentyOnGird.remove(player.getUniqueId());
            this.cooldown.walkOffGridHook(player);
            return playerOnGrid;
        }

        return playerOnGrid;
    }

    public void useTicket(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (!this.cooldown.contains(player.getUniqueId())) {
            player.sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_TICKETUSE_NOCOOLDOWN);
            e.setCancelled(true);
            return;
        }

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        e.getPlayer().sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_TICKETUSE_SUCCESS);
        e.setUseItemInHand(Event.Result.DENY);
        player.getInventory().removeItem(Config.Game.TICKET);

    }

    public void updateActionBar() {
        if (this.game != null) {
            BaseComponent[] actionMessage = new ComponentBuilder()
                    .append(ComponentSerializer.parse(Config.Game.ACTIONTEXT))
                    .append((Config.Color("&2&l ") + this.game.score))
                    .create();
            this.game.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionMessage);
        }

        for (UUID player : this.currentyOnGird) {
            if (this.cooldown.contains(player))
                sendPlayerCooldownMessage(player);
        }
    }
        
    private void sendPlayerCooldownMessage(UUID player) {
        Bukkit.getPlayer(player).spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            new ComponentBuilder()
                .append(Translator.GAME_ACTIONBAR_GAMEOVER + this.cooldown.getText(player))
                .create());
    }

    public boolean handleHitEvent(EntityDamageByEntityEvent e) {
        if (this.game == null)
            return false;

        Player player = (Player) e.getDamager();
        if (player != this.game.player || !player.getInventory().getItemInMainHand().equals(Config.Game.PLAYER_AXE)) {
            e.setCancelled(true);
            return true;
        }

        Mole mole = this.grid.handleHitEvent(e.getEntity());
        if (mole == null)
            return false;
        this.game.moleHit(mole);
        mole.state = MoleState.Hit;

        player.playSound(e.getDamager().getLocation(), Config.Game.HITSOUND, 1, 1);
        player.spawnParticle(Particle.COMPOSTER, mole.mole.getLocation().add(0, 1.75, 0), 10, 0.1, 0.1, 0.1, 0);

        return true;
    }

    public void moleUpdater() {
        int missed = this.grid.entityUpdate();
        if (this.game != null && missed > 0) {
            this.game.missed += missed;
            this.game.player.playSound(game.player.getLocation(), Config.Game.MISSSOUND, 1, 1);
            this.game.player.sendMessage(Config.AppConfig.PREFIX + Translator.GAME_MOLEMISSED.Format(this));
            if (this.getRunning().missed >= this.settings.maxMissed) {
                this.Stop();
            }
        }
    }

    private int Tick = 0;

    public void run() {
        if (this.game != null) {
            double gameInterval = this.game.interval * 20;
            Tick++;
            if (Tick >= gameInterval) {
                double Speed = 1 / (this.game.moleSpeed * 10);
                int DROP = random.nextInt(100);
                if (DROP <= this.game.spawnChance) {
                    if (this.settings.Jackpot) {
                        if (DROP <= this.settings.jackpotSpawn) {
                            this.grid.spawnRandomEntity(MoleType.Jackpot, Speed, this.settings.spawnRotation);
                        } else {
                            this.grid.spawnRandomEntity(MoleType.Mole, Speed, this.settings.spawnRotation);
                        }
                    } else {
                        this.grid.spawnRandomEntity(MoleType.Mole, Speed, this.settings.spawnRotation);
                    }
                }
                Tick = 0;
            }
        }
    }
}
