package whackamole.whackamole;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import whackamole.whackamole.DB.*;
import whackamole.whackamole.Mole.MoleState;
import whackamole.whackamole.Mole.MoleType;

import static org.bukkit.Bukkit.spigot;

public class Game {
    public static final BlockFace[] Directions = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
            BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    public class CooldownList {
        private HashMap<UUID, Long> cooldown = new HashMap<>();
        private CooldownDB db = SQLite.getCooldownDB();

        private void add(Player player) {
            this.add(player.getUniqueId(), settings.Cooldown);
        }

        private void add(UUID player, Long time) {
            this.cooldown.put(player, System.currentTimeMillis() + time);
            this.db.Insert(Game.this.ID, player, System.currentTimeMillis() + time);
        }

        private boolean contains(Player player) {
            return this.contains(player.getUniqueId());
        }

        private boolean contains(UUID player) {
            if(this.cooldown.containsKey(player)) {
                if(this.getTime(player) < System.currentTimeMillis()) {
                    this.remove(player);
                    return false;
                }
                return true;
            }
            return false;
        }

        private void remove(UUID player) {
            this.cooldown.remove(player);
            this.db.Delete(Game.this.ID, player);
        }

        private Long getTime(UUID player) {
            return this.cooldown.get(player);
        }

        private String getText(UUID player) {
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

        private String formatRestCooldown(long time) {
            time = time - System.currentTimeMillis();
            return formatSetCooldown(time);
        }
        private String formatSetCooldown(long time) {
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
                Game.this.actionbarParse(player.getUniqueId(), Translator.GAME_ACTIONBAR_GAMEOVER, Game.this.cooldown.getText(player.getUniqueId()));
        }

    }
    public class Settings {
        public World world;
        public BlockFace spawnRotation;
        public Long Cooldown = 86400000L;
        public boolean Jackpot = true;

        public int jackpotSpawn = 1, difficultyScore = 1, pointsPerKill = 1, maxMissed = 3;

        public double Interval = 1, spawnChance = 100, difficultyScale = 10, moleSpeed = 2;

        public String getCooldown() {
            return Game.this.cooldown.formatSetCooldown(this.Cooldown);
        }


    }
    public class dataBase {
        private final GameDB gameDB = SQLite.getGameDB();
        public void save() {
            gameDB.Update(Game.this);
        }
    }
    public class GameFile {
        private final YMLFile gameConfig;

        public GameFile() {
            gameConfig = new YMLFile(Config.AppConfig.storageFolder + "/Games/" + name + ".yml");
            this.save();
        }

        public GameFile(YMLFile gameFile) {
            gameConfig = gameFile;
            this.load();
        }

        public void save() {
            List<String> header = Arrays.asList(
                    "############################################################\n",
                    "# ^------------------------------------------------------^ #\n",
                    "# |                       GameFile                       | #\n",
                    "# <------------------------------------------------------> #\n",
                    "############################################################\n",
                    "# " + Translator.GAME_CONFIG_FIRSTNOTE + "\n",
                    "# " + Translator.GAME_CONFIG_SECONDNOTE + "\n",
                    "# " + Translator.GAME_CONFIG_THIRDNOTE + "\n",
                    "#\n",
                    "#\n",
                    "############################################################\n",
                    "# ^------------------------------------------------------^ #\n",
                    "# |                      Explanation                     | #\n",
                    "# <------------------------------------------------------> #\n",
                    "############################################################\n",
                    "# " + Translator.GAME_CONFIG_NAME + "\n",
                    "# " + Translator.GAME_CONFIG_DIRECTION + "\n",
                    "# " + Translator.GAME_CONFIG_JACKPOT + "\n",
                    "# " + Translator.GAME_CONFIG_JACKPOTSPAWN + "\n",
                    "# " + Translator.GAME_CONFIG_GAMELOST + "\n",
                    "# " + Translator.GAME_CONFIG_POINTSPERKILL + "\n",
                    "# " + Translator.GAME_CONFIG_SPAWNRATE + "\n",
                    "# " + Translator.GAME_CONFIG_SPAWNCHANCE + "\n",
                    "# " + Translator.GAME_CONFIG_MOLESPEED + "\n",
                    "# " + Translator.GAME_CONFIG_DIFFICULTYSCALE + "\n",
                    "# " + Translator.GAME_CONFIG_DIFFICULTYINCREASE + "\n",
                    "# " + Translator.GAME_CONFIG_COOLDOWN + "\n",
                    "# " + Translator.GAME_CONFIG_ENDMESSAGE + "\n",
                    "# \n");

            this.gameConfig.FileConfig.options().header(header.toString());

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
            this.gameConfig.set("Properties.Cooldown", Game.this.cooldown.formatSetCooldown(Game.this.settings.Cooldown));
            this.gameConfig.set("Field Data.World", Game.this.grid.world.getName());
            this.gameConfig.set("Field Data.Grid", Game.this.grid.Serialize());
            try {
                this.gameConfig.save();
            } catch (Exception ignored) {
            }
        }

        public void load() {
            Game.this.name = this.gameConfig.getString("Properties.Name");
            Game.this.settings.world = Bukkit.getWorld(this.gameConfig.getString("Field Data.World"));
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
            Game.this.settings.Cooldown = cooldown.parseTime(this.gameConfig.getString("Properties.Cooldown"));
            Game.this.grid = Grid.Deserialize(Game.this.settings.world, this.gameConfig.getList("Field Data.Grid"));

            Logger.success(Translator.GAME_LOADSUCCESS.Format(Game.this));
        }

        public void delete() {
            this.gameConfig.remove();
        }
    }

    public class Scoreboard {

        public class Score {
            public Player player;
            public int score, molesHit, highestStreak;
            public Long timestamp;

            public Score(Player player, int score, int molesHit, int highestStreak, Long timestamp) {
                this.player = player;
                this.score = score;
                this.molesHit = molesHit;
                this.highestStreak = highestStreak;
                this.timestamp = timestamp;
            }
        }

        private ArrayList<Score> scores = new ArrayList<>();
        private ScoreboardDB db = SQLite.getScoreboardDB();

        public void add(Player player, int score, int molesHit, int highestStreak) {
            Score scoreItem = new Score(player, score, molesHit, highestStreak, System.currentTimeMillis() / 1000);
            this.scores.add(scoreItem);
            this.db.Insert(scoreItem, Game.this.ID);

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
        public int score = 0, missed = 0, difficultyModifier = 0, molesHit = 0, highestStreak = 0, Streak = 0;

        public double moleSpeed = settings.moleSpeed, interval = settings.Interval, spawnChance = settings.spawnChance;

        public GameRunner() {
        }

        private boolean Start(Player player) {
            if (cooldown.contains(player)
//                 || player.hasPermission(Config.Permissions.PERM_PLAY)
            ) {
                return false;
            }


            if (Econ.currencyType == Econ.Currency.NULL) {
                Logger.error(Translator.GAME_INVALIDECONOMY);
                cooldown.add(player.getUniqueId(), 10000L);
                Game.this.actionbarParse(player.getUniqueId(), Translator.GAME_ACTIONBAR_ERROR.Format());
                return false;
            }


            if (!givePlayerAxe(player)) {
                player.sendMessage(Config.AppConfig.PREFIX + Translator.GAME_START_FULLINVENTORY);
                Game.this.actionbarParse(player.getUniqueId(), Translator.GAME_ACTIONBAR_FULLINVENTORY.Format());
                return false;
            }

            this.player = player;
            return true;
        }

        private void Stop() {
            grid.removeEntities();
            Game.this.actionbarParse(this.player.getUniqueId(), "");
            this.removePlayerAxe(this.player);
            this.sendScoreToPlayer(this.player, this.score);
            econ.depositPlayer(this.player, this.score);

            if (this.score > 0) {
                if (this.Streak > this.highestStreak) { this.highestStreak = this.Streak; }
                this.Streak = 0;
                cooldown.add(this.player);
                scoreboard.add(this.player, this.score, this.molesHit, this.highestStreak);
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
                    this.molesHit ++;
                    this.Streak ++;
                    break;
                case Jackpot:
                    this.score += settings.pointsPerKill * 3;
                    this.molesHit ++;
                    this.Streak ++;
                    break;
                case Null:
                    break;
            }

            this.difficultyModifier++;
            if (this.difficultyModifier >= settings.difficultyScore)
                setSpeedScale();
        }

        public void RemovePlayerFromGame(PlayerMoveEvent e) { // TODO: MAKE TELEPORT LOCATION IN FRONT OF MIDDLE BLOCK FRONT ROW :) (WITH CHECK FOR BLOCK SPAWN AND SAFE TP)
            Player player = e.getPlayer();
            Location playerLocation = player.getLocation();
            Vector moveVector = e.getFrom().toVector().subtract(e.getTo().toVector()).normalize().multiply(2).setY(1.5);
            while (Game.this.grid.onGrid(playerLocation)) {
                if (moveVector.getX() == 0 && moveVector.getZ() == 0) {
                    moveVector = Game.this.getSettings().spawnRotation.getDirection();
                }
                playerLocation = playerLocation.add(moveVector);
            }
            player.teleport(playerLocation);
            player.sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_ALREADYACTIVE);
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
    private dataBase DB = new dataBase();
    private Settings settings = new Settings();
    private CooldownList cooldown = new CooldownList();
    private Scoreboard scoreboard = new Scoreboard();
    private GameFile gameFile;
    private GameRunner game;

    private List<UUID> currentyOnGird = new ArrayList<>();

    public int ID;
    public String name;
    private Grid grid;
    private GameDB gameDB = SQLite.getGameDB();
    private GridDB gridDB = SQLite.getGridDB();

    private Random random = new Random();
    public Game(GameRow result) {
        this.ID = result.ID;
        this.name = result.Name;
        this.settings.world = Bukkit.getWorld(result.worldName);
        this.settings.spawnRotation = BlockFace.valueOf(result.spawnDirection);
        this.settings.Jackpot = result.hasJackpot == 1;
        this.settings.jackpotSpawn = result.jackpotSpawnChance;
        this.settings.maxMissed = result.missCount;
        this.settings.pointsPerKill = result.scorePoints;
        this.settings.Interval = result.spawnTimer;
        this.settings.spawnChance = result.spawnChance;
        this.settings.moleSpeed = result.moleSpeed;
        this.settings.difficultyScale = result.difficultyScale;
        this.settings.difficultyScore = result.difficultyScore;
        this.settings.Cooldown = (long) result.Cooldown;
        Logger.success(Translator.GAME_LOADSUCCESS.Format(this.name));

    }

    public Game(YMLFile configFile) {
        this.gameFile = new GameFile(configFile);
    }

    public Game(String name, Grid grid, Player player) {
        this.name = formatName(name);
        this.settings.spawnRotation = Directions[Math.round(player.getLocation().getYaw() / 45) & 0x7];
        this.settings.world = player.getWorld();
        this.grid = grid;
        if (Config.Game.ENABLE_GAMECONFIG) {
            this.gameFile = new GameFile();
        }

        this.ID = this.gameDB.Insert(this);
        this.gridDB.Insert(this.grid, this.ID);
    }

    private String formatName(String name) {
        char[] chars = name.toLowerCase().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public void Start(Player player) {
        if (this.game != null)
            return;
        this.game = new GameRunner();
        if(!this.game.Start(player)) {
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

    public void save() {
        if (this.gameFile != null) {
            this.gameFile.save();
        }
        this.DB.save();
    }
    public void delete() {
        if (this.gameFile != null)
            this.gameFile.delete();
    }

    public GameRunner getRunning() {
        if (this.game != null) {
            return this.game;
        }
        return null;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public void setJackpotSpawn(int jackpotSpawn) {
        this.settings.jackpotSpawn = jackpotSpawn;
        this.save();
    }

    public void setDifficultyScore(int difficultyScore) {
        this.settings.difficultyScore = difficultyScore;
        this.save();
    }

    public void setPointsPerKill(int pointsPerKill) {
        this.settings.pointsPerKill = pointsPerKill;
        this.save();
    }

    public void setMaxMissed(int maxMissed) {
        this.settings.maxMissed = maxMissed;
        this.save();
    }

    public void setInterval(double Interval) {
        this.settings.Interval = Interval;
        this.save();
    }

    public void setSpawnChance(double spawnChance) {
        this.settings.spawnChance = spawnChance;
        this.save();
    }

    public void setDifficultyScale(double difficultyScale) {
        this.settings.difficultyScale = difficultyScale;
        this.save();
    }

    public void setMoleSpeed(double moleSpeed) {
        this.settings.moleSpeed = moleSpeed;
        this.save();
    }

    public void setJackpot(boolean Jackpot) {
        this.settings.Jackpot = Jackpot;
        this.save();
    }

    public void setCooldown(String Cooldown) {
        this.settings.Cooldown = this.cooldown.parseTime(Cooldown);
        this.save();
    }

    public void setSpawnRotation(BlockFace spawnRotation) {
        this.settings.spawnRotation = spawnRotation;
        this.save();
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
            return playerOnGrid;
        }

        // * Player walks of grid
        if (!playerOnGrid && currentyOnGird.contains(player.getUniqueId())) {
            currentyOnGird.remove(player.getUniqueId());
            Game.this.actionbarParse(player.getUniqueId(), "");
            return playerOnGrid;
        }

        return playerOnGrid;
    }
    public boolean inGrid(Block block) {
        if (this.grid.grid.contains(block)) {
            return true;
        }
        return false;
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

    private void actionbarParse(UUID player, String text) {
        Bukkit.getPlayer(player).spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new ComponentBuilder()
                        .append(text + "")
                        .create());
    }
    private void actionbarParse(UUID player, BaseComponent[] text, String text2) {
        Bukkit.getPlayer(player).spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new ComponentBuilder()
                        .append(text)
                        .append(text2)
                        .create());
    }
    private void actionbarParse(UUID player, Translator text, String text2) {
        Bukkit.getPlayer(player).spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new ComponentBuilder()
                        .append(text + text2)
                        .create());
    }

    public void updateActionBar() {
        if (Game.this.getRunning() != null) {
            this.actionbarParse(this.game.player.getUniqueId(), ComponentSerializer.parse(Config.Game.ACTIONTEXT), Config.Color("&2&l ") + this.game.score);
        }
        for (UUID player : this.currentyOnGird) {
            if (this.cooldown.contains(player))
                this.actionbarParse(player, Translator.GAME_ACTIONBAR_GAMEOVER, this.cooldown.getText(player));
            else if (Bukkit.getPlayer(player).getInventory().firstEmpty() != -1)
                this.actionbarParse(player, Translator.GAME_ACTIONBAR_RESTART.Format());
        }
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
            if (this.game.Streak > this.game.highestStreak) { this.game.highestStreak = this.game.Streak; }
            this.game.Streak = 0;
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
