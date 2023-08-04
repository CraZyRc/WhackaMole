package whackamole.whackamole;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
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
import net.md_5.bungee.chat.ComponentSerializer;
import whackamole.whackamole.DB.*;
import whackamole.whackamole.DB.Model.Row;
import whackamole.whackamole.Mole.MoleState;
import whackamole.whackamole.Mole.MoleType;

public class Game {
    public static final BlockFace[] Directions = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
            BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    public class CooldownList {
        private HashMap<UUID, Long> cooldown = new HashMap<>();
        private CooldownDB db = SQLite.getCooldownDB();

        private void onLoad() {
            var list = db.Select(getID());
            for (var item : list) {
                this.cooldown.put(item.playerID, item.endTimeStamp);
            }
        }
        
        private void Delete() {
            for (var player : cooldown.keySet()) {
                this.remove(player);
            }
        }

        private void add(Player player) {
            this.add(player.getUniqueId(), settings.Cooldown);
        }

        private void add(UUID player, Long time) {
            this.cooldown.put(player, System.currentTimeMillis() + time);
            this.db.Insert(getID(), player, System.currentTimeMillis() + time);
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
            this.db.Delete(getID(), player);
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

        private void walkOnGridHook(Player player) {
            if (!this.contains(player.getUniqueId()))
                return;

            UUID playerUUID = player.getUniqueId();
            if (this.getTime(playerUUID) < System.currentTimeMillis())
                this.remove(playerUUID);
            else
                Game.this.actionbarParse(player.getUniqueId(), Translator.GAME_ACTIONBAR_GAMEOVER, Game.this.cooldown.getText(player.getUniqueId()));
        }

    }
    
    public class Settings extends GameRow {
        public GameDB gameDB = SQLite.getGameDB();

        public World world;
        public BlockFace spawnRotation;

        public String getCooldown() {
            return Game.this.cooldown.formatSetCooldown(this.Cooldown);
        }

        private void onLoad(GameRow a) {
            upCast(a, this);
            this.onLoad();
        }

        private void onLoad() {
            this.world = Bukkit.getWorld(this.worldName);
            this.spawnRotation = BlockFace.valueOf(this.spawnDirection);
        }

        private void Setup(String name, Player player) {
            this.Name = name;
            this.spawnRotation = Directions[Math.round(player.getLocation().getYaw() / 45) & 0x7];
            this.world = player.getWorld();
            this.Save();
        }

        private void Save() {
            this.worldName = this.world.getName();
            this.spawnDirection = this.spawnRotation.name();
            if (this.ID == -1)  gameDB.Insert(this);
            else                gameDB.Update(this);
        }

        private void Delete() {
            if (this.ID != -1) gameDB.Delete(this);
        }
    }

    public class GameFile {
        private final YMLFile gameConfig;

        public GameFile() throws FileNotFoundException {
            gameConfig = new YMLFile(Config.AppConfig.storageFolder + "/Games",  getName() + ".yml");
            this.save();
        }

        public GameFile(YMLFile gameFile) {
            gameConfig = gameFile;
            this.load();
        }

        @SuppressWarnings("deprecation")
        public void save() {
            List<String> header = Arrays.asList(
                    "###########################################################",
                    " ^------------------------------------------------------^ #",
                    " |                       GameFile                       | #",
                    " <------------------------------------------------------> #",
                    "###########################################################\n",
                    Translator.GAME_CONFIG_FIRSTNOTE.toString(),
                    Translator.GAME_CONFIG_SECONDNOTE.toString(),
                    Translator.GAME_CONFIG_THIRDNOTE.toString(),
                    "",
                    "",
                    "###########################################################",
                    " ^------------------------------------------------------^ #",
                    " |                      Explanation                     | #",
                    " <------------------------------------------------------> #",
                    "###########################################################\n",
                    Translator.GAME_CONFIG_NAME.toString(),
                    Translator.GAME_CONFIG_DIRECTION.toString(),
                    Translator.GAME_CONFIG_JACKPOT.toString(),
                    Translator.GAME_CONFIG_JACKPOTSPAWN.toString(),
                    Translator.GAME_CONFIG_GAMELOST.toString(),
                    Translator.GAME_CONFIG_POINTSPERKILL.toString(),
                    Translator.GAME_CONFIG_SPAWNRATE.toString(),
                    Translator.GAME_CONFIG_SPAWNCHANCE.toString(),
                    Translator.GAME_CONFIG_MOLESPEED.toString(),
                    Translator.GAME_CONFIG_DIFFICULTYSCALE.toString(),
                    Translator.GAME_CONFIG_DIFFICULTYINCREASE.toString(),
                    Translator.GAME_CONFIG_COOLDOWN.toString(),
                    Translator.GAME_CONFIG_MOLEHEAD.toString(),
                    Translator.GAME_CONFIG_TPLOCATION.toString(),
                    Translator.GAME_CONFIG_SCORELOCATION.toString(),
                    Translator.GAME_CONFIG_STREAKHOLOLOCATION.toString(),
                    Translator.GAME_CONFIG_ENDMESSAGE.toString(),
                    "\n");

            if (Updater.versionCompare(Bukkit.getBukkitVersion().split("-")[0], "1.17.2")) {
                this.gameConfig.FileConfig.options().header(String.join("", header.stream().map(o -> String.format("# %s\n", o)).toList()));
            } else {
                this.gameConfig.FileConfig.options().setHeader(header);
            }

            this.gameConfig.set("Properties.ID", getID());
            this.gameConfig.set("Properties.Name", getName());
            this.gameConfig.set("Properties.Direction", Game.this.settings.spawnRotation.name());
            this.gameConfig.set("Properties.Jackpot", Game.this.settings.hasJackpot);
            this.gameConfig.set("Properties.Jackpot spawn chance", Game.this.settings.jackpotSpawnChance);
            this.gameConfig.set("Properties.Game lost", Game.this.settings.missCount);
            this.gameConfig.set("Properties.Points per kill", Game.this.settings.scorePoints);
            this.gameConfig.set("Properties.Spawn rate", Game.this.settings.spawnTimer);
            this.gameConfig.set("Properties.Spawn chance", Game.this.settings.spawnChance);
            this.gameConfig.set("Properties.Mole speed", Game.this.settings.moleSpeed);
            this.gameConfig.set("Properties.Difficulty scaling", Game.this.settings.difficultyScale);
            this.gameConfig.set("Properties.Difficulty increase", Game.this.settings.difficultyScore);
            this.gameConfig.set("Properties.Cooldown", Game.this.cooldown.formatSetCooldown(Game.this.settings.Cooldown));
            this.gameConfig.set("Properties.moleHead", Game.this.settings.moleHead);
            this.gameConfig.set("Properties.jackpotHead", Game.this.settings.jackpotHead);
            this.gameConfig.set("Properties.teleportLocation", Game.this.settings.teleportLocation);
            this.gameConfig.set("Properties.scoreLocation", Game.this.settings.scoreLocation);
            this.gameConfig.set("Properties.streakLocation", Game.this.settings.streakHoloLocation);
            this.gameConfig.set("Field Data.World", Game.this.grid.world.getName());
            try {
                this.gameConfig.save();
            } catch (Exception ignored) { }
        }

        public void load() {
            Game.this.settings.ID = this.gameConfig.getInt("Properties.ID");
            Game.this.settings.Name = this.gameConfig.getString("Properties.Name");
            Game.this.settings.world = Bukkit.getWorld(this.gameConfig.getString("Field Data.World"));
            Game.this.settings.spawnRotation = BlockFace.valueOf(this.gameConfig.getString("Properties.Direction"));
            Game.this.settings.hasJackpot = this.gameConfig.getBoolean("Properties.Jackpot");
            Game.this.settings.jackpotSpawnChance = this.gameConfig.getInt("Properties.Jackpot spawn chance");
            Game.this.settings.missCount = this.gameConfig.getInt("Properties.Game lost");
            Game.this.settings.scorePoints = this.gameConfig.getInt("Properties.Points per kill");
            Game.this.settings.spawnTimer = this.gameConfig.getDouble("Properties.Spawn rate");
            Game.this.settings.spawnChance = this.gameConfig.getDouble("Properties.Spawn chance");
            Game.this.settings.moleSpeed = this.gameConfig.getDouble("Properties.Mole speed");
            Game.this.settings.difficultyScale = this.gameConfig.getDouble("Properties.Difficulty scaling");
            Game.this.settings.difficultyScore = this.gameConfig.getInt("Properties.Difficulty increase");
            Game.this.settings.Cooldown = cooldown.parseTime(this.gameConfig.getString("Properties.Cooldown"));
            Game.this.settings.teleportLocation = this.gameConfig.FileConfig.getLocation("Properties.teleportLocation");
            Game.this.settings.scoreLocation = this.gameConfig.FileConfig.getLocation("Properties.scoreLocation");
            Game.this.settings.streakHoloLocation = this.gameConfig.FileConfig.getLocation("Properties.streakLocation");
            Game.this.settings.Save();

            Logger.success(Translator.GAME_LOADSUCCESS.Format(Game.this));
        }

        public void delete() {
            this.gameConfig.remove();
        }
    }

    public class Scoreboard {

        List<ArmorStand> holoScores = new ArrayList<>();
        ArmorStand highScore, Score, Streak, molesHit;

        class Score extends ScoreboardRow {
            public OfflinePlayer player;
            private void onLoad() {
                this.player = Bukkit.getOfflinePlayer(playerID);
            }
        }

        private List<Score> scores = new ArrayList<>();
        private ScoreboardDB db = SQLite.getScoreboardDB();

        public void add(Player player, int score, int molesHit, int scoreStreak) {
            var scoreItem = new Score();
            scoreItem.player = player;
            scoreItem.Score = score;
            scoreItem.molesHit = molesHit;
            scoreItem.scoreStreak = scoreStreak;
            scoreItem.Datetime = LocalDateTime.now();
            scoreItem.gameID = getID();
            scoreItem.playerID = player.getUniqueId();
            this.scores.add(scoreItem);
            this.db.Insert(scoreItem);
        }

        private void onLoad() {
            this.scores.clear();
            this.createTopHolo();
            var dbScores = db.Select(getID());
            for(var i : dbScores) {
                Score score = new Score();
                Row.upCast(i, score);
                score.onLoad();
                this.scores.add(score);
            }
            this.updateTopHolo();
        }
        
        private void Delete() {
            this.killTopHolo();
            for (var row : scores) {
                db.Delete(row);
            }
        }

        public Score[] getTop(int scoreType) {
            return getTop(10, scoreType);
        }

        public Score[] getTop(int count, int scoreType) {
            Score[] template = new Score[]{};
            if (scores.size() == 0) {
                return this.scores.toArray(template);
            }
            count = Math.min(this.scores.size(), count);
            if (scoreType == 0) {
                this.scores.sort((a, b) -> b.Score - a.Score);
            } else if (scoreType == 1) {
                this.scores.sort((a, b) -> b.scoreStreak - a.scoreStreak);
            } else if (scoreType == 2) {
                this.scores.sort((a, b) -> b.molesHit - a.molesHit);
            } else { Logger.error("getTop scoreType = " + scoreType + " unknown, please report this bug."); }

            return this.scores.subList(0, count).toArray(template);
        }

        private void createTopHolo() {
            var settings = Game.this.getSettings();
            Location spawnloc = settings.scoreLocation.clone().add(0,1.75,0);
            this.highScore  = (ArmorStand) settings.world.spawnEntity(spawnloc, EntityType.ARMOR_STAND);
            this.Score      = (ArmorStand) settings.world.spawnEntity(spawnloc.subtract(0,0.25,0), EntityType.ARMOR_STAND);
            this.Streak     = (ArmorStand) settings.world.spawnEntity(spawnloc.subtract(0,0.25,0), EntityType.ARMOR_STAND);
            this.molesHit   = (ArmorStand) settings.world.spawnEntity(spawnloc.subtract(0,0.25,0), EntityType.ARMOR_STAND);
            this.holoScores.add(highScore);
            this.holoScores.add(Score);
            this.holoScores.add(Streak);
            this.holoScores.add(molesHit);
            for (ArmorStand armorStand : this.holoScores) {
                armorStand.setVisible(true);
                armorStand.setCustomNameVisible(true);
                armorStand.setGravity(false);
                armorStand.setInvisible(true);
                armorStand.setMarker(true);
                armorStand.isInvulnerable();
            }
            this.highScore.setCustomName(DefaultFontInfo.Color("&e&l[-> &6&lHigh scores &e&l<-]"));
            this.Score.setCustomName(DefaultFontInfo.Color("&e&l[- &6&lScore: &bNone &e&l-]"));
            this.Streak.setCustomName(DefaultFontInfo.Color("&e&l[- &6&lStreaks: &byou can &e&l-]"));
            this.molesHit.setCustomName(DefaultFontInfo.Color("&e&l[- &6&lMoles hit: &bbe the first! &e&l-]"));
        }
        private void updateTopHolo() {
            var Score = getTop(1, 0);
            var Streak = getTop(1, 1);
            var molesHit = getTop(1, 2);
            if (Score.length > 0) this.Score.setCustomName(DefaultFontInfo.Color("&e&l[- &6&lScore: &3" + Score[0].player.getName() + "&f - &b" + Score[0].Score + " &e&l-]"));
            if (Streak.length > 0) this.Streak.setCustomName(DefaultFontInfo.Color("&e&l[- &6&lStreaks: &3" + Streak[0].player.getName() + "&f - &b" + Streak[0].scoreStreak + " &e&l-]"));
            if (molesHit.length > 0) this.molesHit.setCustomName(DefaultFontInfo.Color("&e&l[- &6&lMoles hit: &3" + molesHit[0].player.getName() + "&f - &b" + molesHit[0].molesHit + " &e&l-]"));
        }

        public void tpTopHolo(Location loc) {
            highScore.teleport(loc.add(0,0.5,0));
            Score.teleport(loc.subtract(0,0.25,0));
            Streak.teleport(loc.subtract(0,0.25,0));
            molesHit.teleport(loc.subtract(0,0.25,0));
        }
        public void killTopHolo() {
            for (ArmorStand armorStand : this.holoScores) {
                armorStand.remove();
            }
        }
    }

    public class GameRunner {
        private Econ econ = new Econ();

        public Player player;
        public int score = 0, missed = 0, difficultyModifier = 0, molesHit = 0, highestStreak = 0, Streak = 0;

        public double moleSpeed = settings.moleSpeed, interval = settings.spawnTimer, spawnChance = settings.spawnChance;
        public Location teleportLocation = settings.teleportLocation;

        private ArmorStand streakScoreHolo, streakNameHolo;

        public GameRunner() {
        }

        private boolean Start(Player player) {
            if (cooldown.contains(player)
            || !player.hasPermission(Config.Permissions.PERM_PLAY)
            || Config.Game.PLAYER_AXE == null
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
            if (settings.streakHoloLocation != null) {
                this.createStreakHolo();
            }
            return true;
        }

        private void Stop() {
            Game.this.grid.removeEntities();
            Game.this.actionbarParse(this.player.getUniqueId(), "");
            this.removePlayerAxe(this.player);
            this.sendScoreToPlayer(this.player, this.score);
            this.econ.depositPlayer(this.player, this.score);
            this.removeStreakHolo();

            if (this.score > 0) {
                if (this.Streak > this.highestStreak) { this.highestStreak = this.Streak; }
                this.Streak = 0;
                Game.this.scoreboard.add(this.player, this.score, this.molesHit, this.highestStreak);
                Game.this.cooldown.add(this.player);
            }
            Game.this.scoreboard.updateTopHolo();
            this.player = null;
        }

        public Player getPlayer() {
            return this.player;
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
                    this.score += settings.scorePoints;
                    this.molesHit ++;
                    this.Streak ++;
                    this.updateStreakHolo();
                    break;
                case Jackpot:
                    this.score += settings.scorePoints * 3;
                    this.molesHit ++;
                    this.Streak ++;
                    this.updateStreakHolo();
                    break;
                case Null:
                    break;
            }

            this.difficultyModifier++;
            if (this.difficultyModifier >= settings.difficultyScore)
                this.setSpeedScale();
        }

        public void RemovePlayerFromGame(PlayerMoveEvent e) {
            Player player = e.getPlayer();
            Location playerLocation = player.getLocation();
            Vector moveVector = e.getFrom().toVector().subtract(e.getTo().toVector()).normalize().multiply(2).setY(1.5);
            while (Game.this.grid.onGrid(playerLocation)) {
                if (moveVector.getX() == 0 && moveVector.getZ() == 0) {
                    moveVector = Game.this.getSettings().spawnRotation.getDirection();
                }
                playerLocation = playerLocation.add(moveVector);
            }
            if (this.teleportLocation == null) {
                player.teleport(playerLocation);
            } else player.teleport(this.teleportLocation);
            player.sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_ALREADYACTIVE);
        }

        private void setSpeedScale() {
            double Scaler = 1 + (settings.difficultyScale / 100);
            this.moleSpeed = this.moleSpeed / Scaler;
            this.interval = this.interval / Scaler;
            this.spawnChance = Math.max(0, Math.min(100, this.spawnChance * Scaler));
            this.difficultyModifier = 0;
        }
        public void createStreakHolo() {
            var settings = Game.this.getSettings();
            this.streakNameHolo = (ArmorStand) settings.world.spawnEntity(settings.streakHoloLocation, EntityType.ARMOR_STAND);
            this.streakNameHolo.setVisible(true);
            this.streakNameHolo.setCustomNameVisible(true);
            this.streakNameHolo.setGravity(false);
            this.streakNameHolo.setInvisible(true);
            this.streakNameHolo.setMarker(true);
            this.streakNameHolo.isInvulnerable();
            this.streakNameHolo.setCustomName(DefaultFontInfo.Color("&6&l(<- &e&lHit-streak &6&l->)"));
            
            this.streakScoreHolo = (ArmorStand) settings.world.spawnEntity(settings.streakHoloLocation.clone().subtract(0, 0.25, 0), EntityType.ARMOR_STAND);
            this.streakScoreHolo.setVisible(true);
            this.streakScoreHolo.setCustomNameVisible(true);
            this.streakScoreHolo.setGravity(false);
            this.streakScoreHolo.setInvisible(true);
            this.streakScoreHolo.setMarker(true);
            this.streakScoreHolo.isInvulnerable();
            this.streakScoreHolo.setCustomName(DefaultFontInfo.Color("&0"));
        }

        private String[] StreakColors = new String[] {
            "#fb1200&l", "#f82500&l", "#f43800&l", "#f14b00&l", "#ee5e00&l", "#ea7100&l", "#e78400&l", "#e39700&l", "#e0aa00&l", "#ddbd00&l", "#c6c300&l", "#b0ca00&l", "#9ad000&l", "#84d700&l", "#6ede00&l", "#58e400&l", "#42eb00&l", "#2cf100&l", "#16f800&l"
        };

        public void updateStreakHolo() {
            if (this.streakScoreHolo == null) return;
            var settings = Game.this.getSettings();
            Location particleLocation = settings.streakHoloLocation.clone().subtract(0, 0.05, 0);

            if(this.Streak == 0) 
                this.streakScoreHolo.setCustomName(DefaultFontInfo.Color("#ff0000&l0"));
            else if (this.Streak < 20) 
                this.streakScoreHolo.setCustomName(DefaultFontInfo.Color(this.StreakColors[this.Streak - 1] + this.Streak));
            else if (this.Streak < 40) 
                this.streakScoreHolo.setCustomName(DefaultFontInfo.Color("#00ff00&l" + this.Streak));
            else 
                this.streakScoreHolo.setCustomName(DefaultFontInfo.Color("#00ff00&l" + this.Streak));
                int red   = Math.min(255, Math.max(0, 255 - (this.Streak - 40) * 12));
                int green = Math.min(255, Math.max(0, (this.Streak - 40) * 12));
                settings.world.spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(Color.fromRGB(red, green, 0), 2));


        }
        public void removeStreakHolo() {
            if (this.streakScoreHolo != null) { this.streakScoreHolo.remove(); this.streakScoreHolo = null; }
            if (this.streakNameHolo != null)  { this.streakNameHolo.remove();  this.streakNameHolo  = null; }
        }

    }

    private Settings settings = new Settings();
    public CooldownList cooldown = new CooldownList();
    private Scoreboard scoreboard = new Scoreboard();
    private GameFile gameFile;
    private GameRunner game;
    private Grid grid;

    private Random random = new Random();
    private List<UUID> currentyOnGird = new ArrayList<>();


    public Game(GameRow result) {
        this.settings.onLoad(result);
        this.cooldown.onLoad();
        this.scoreboard.onLoad();
        this.grid = new Grid(settings);
        Logger.success(Translator.GAME_LOADSUCCESS.Format(this.getName()));
    }

    public Game(YMLFile configFile) {
        this.gameFile = new GameFile(configFile);
        this.settings.Save();
        this.grid = new Grid(settings);

        this.cooldown.onLoad();
        this.scoreboard.onLoad();
    }

    public Game(String name, Grid grid, Player player) throws FileNotFoundException {
        this.settings.scoreLocation = player.getLocation().add(0,1,0);
        this.settings.Setup(formatName(name), player);

        this.grid = grid.setSettings(settings);
        this.scoreboard.createTopHolo();

        if (Config.Game.ENABLE_GAMECONFIG) {
            this.gameFile = new GameFile();
        }
    }

    private String formatName(String name) {
        char[] chars = name.toLowerCase().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public void Start(Player player) {
        if (this.game != null) return;
        this.game = new GameRunner();
        if (!this.game.Start(player)) {
            this.game = null;
        }
    }

    public void Stop() {
        if (this.game != null) {
            this.game.Stop();
            this.game = null;
        }
    }

    public void Unload() {
        this.Stop();
        this.scoreboard.killTopHolo();
    }

    public void Save() {
        if (this.gameFile != null) {
            this.gameFile.save();
        }
        this.settings.Save();
    }
    public void Delete() {
        this.grid.Delete();
        this.cooldown.Delete();
        this.scoreboard.Delete();
        if (this.gameFile != null) this.gameFile.delete();
        this.settings.Delete();
    }

    public int getID() {
        return this.settings.ID;
    }

    public String getName() {
        return this.settings.Name;
    }

    public Optional<GameRunner> getRunning() {
        return Optional.ofNullable(this.game);
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Scoreboard getScoreboard() { 
        return this.scoreboard;
    }

    public void setJackpotSpawn(int jackpotSpawn) {
        this.settings.jackpotSpawnChance = jackpotSpawn;
        this.Save();
    }

    public void setDifficultyScore(int difficultyScore) {
        this.settings.difficultyScore = difficultyScore;
        this.Save();
    }

    public void setPointsPerKill(int pointsPerKill) {
        this.settings.scorePoints = pointsPerKill;
        this.Save();
    }

    public void setMaxMissed(int maxMissed) {
        this.settings.missCount = maxMissed;
        this.Save();
    }

    public void setInterval(double Interval) {
        this.settings.spawnTimer = Interval;
        this.Save();
    }

    public void setSpawnChance(double spawnChance) {
        this.settings.spawnChance = spawnChance;
        this.Save();
    }

    public void setDifficultyScale(double difficultyScale) {
        this.settings.difficultyScale = difficultyScale;
        this.Save();
    }

    public void setMoleSpeed(double moleSpeed) {
        this.settings.moleSpeed = moleSpeed;
        this.Save();
    }

    public void setJackpot(boolean Jackpot) {
        this.settings.hasJackpot = Jackpot;
        this.Save();
    }

    public void setCooldown(String Cooldown) {
        this.settings.Cooldown = this.cooldown.parseTime(Cooldown);
        this.Save();
    }

    public void setMoleHead(String moleHead) {
        this.settings.moleHead = moleHead;
        this.Save();
    }

    public void setJackpotHead(String jackpotHead) {
        this.settings.jackpotHead = jackpotHead;
        this.Save();
    }

    public void setSpawnRotation(BlockFace spawnRotation) {
        this.settings.spawnRotation = spawnRotation;
        this.Save();
    }
    public void setHighScoreLocation(Location loc) {
        this.settings.scoreLocation = loc;
        this.Save();
    }
    public boolean setTeleportLocation(World world, double X, double Y, double Z) {
        if (this.grid.onGrid(new Location(world, X, Y, Z))) {
            return false;
        } else {
            this.settings.teleportLocation = new Location(world, X, Y, Z);
            this.Save();
            return true;
        }
    }
    public void setStreakHoloLocation(World world, double X, double Y, double Z) {
        this.settings.streakHoloLocation = new Location(world, X, Y, Z);
        this.Save();
    }


    public void onPlayerExit(Player player) {
        if (this.currentyOnGird.contains(player.getUniqueId()))
            this.currentyOnGird.remove(player.getUniqueId());

        this.getRunning().ifPresent((game) -> {
            if(game.player == player) this.Stop();
        });
    }

    /**
     * Checks whether the player is currently on the grid
     * @param player
     * @return True if the player is on the grid
     */
    public boolean onGrid(Player player) {
        boolean playerOnGrid = this.grid.onGrid(player);

        // * Player walks on the grid
        if (playerOnGrid && !currentyOnGird.contains(player.getUniqueId())) {
            this.Start(player);
            currentyOnGird.add(player.getUniqueId());
            this.cooldown.walkOnGridHook(player);
            return playerOnGrid;
        }

        // * Player walks of the grid
        if (!playerOnGrid && currentyOnGird.contains(player.getUniqueId())) {
            currentyOnGird.remove(player.getUniqueId());
            Game.this.actionbarParse(player.getUniqueId(), "");
            return playerOnGrid;
        }

        return playerOnGrid;
    }
    public boolean onGrid(Location loc) {
        return this.grid.onGrid(loc);
    }

    public void useTicket(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (!this.cooldown.contains(player.getUniqueId())) {
            player.sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_TICKETUSE_NOCOOLDOWN);
            e.setCancelled(true);
            return;
        }

        this.cooldown.remove(player.getUniqueId());

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        e.getPlayer().sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_TICKETUSE_SUCCESS);
        e.setUseItemInHand(Event.Result.DENY);
        player.getInventory().removeItem(Config.Game.TICKET);
        this.cooldown.remove(player.getUniqueId());
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
        Game.this.getRunning().ifPresent((game) -> {
            this.actionbarParse(game.player.getUniqueId(), ComponentSerializer.parse(Config.Game.ACTIONTEXT), DefaultFontInfo.Color("&2&l ") + game.score);
        });
        for (UUID player : this.currentyOnGird) {
            if (this.cooldown.contains(player))
                this.actionbarParse(player, Translator.GAME_ACTIONBAR_GAMEOVER, this.cooldown.getText(player));
            else if (Bukkit.getPlayer(player).getInventory().firstEmpty() != -1 && Game.this.getRunning().isEmpty())
                this.actionbarParse(player, Translator.GAME_ACTIONBAR_RESTART.Format());
        }
    }


    public boolean handleHitEvent(EntityDamageByEntityEvent e) {
        if (this.getRunning().isEmpty())
            return false;

        Player player = (Player) e.getDamager();
        if (player != this.game.player || !player.getInventory().getItemInMainHand().equals(Config.Game.PLAYER_AXE)) {
            e.setCancelled(true);
            return true;
        }

        Optional<Mole> optionalMole = this.grid.handleHitEvent(e.getEntity());
        if (optionalMole.isEmpty())
            return false;
        
        Mole mole = optionalMole.get();
        this.game.moleHit(mole);
        mole.state = MoleState.Hit;

        player.playSound(e.getDamager().getLocation(), Config.Game.HITSOUND, 1, 1);
        player.spawnParticle(Particle.COMPOSTER, mole.mole.getLocation().add(0, 1.75, 0), 10, 0.1, 0.1, 0.1, 0);

        return true;
    }

    public void moleUpdater() {
        int missed = this.grid.entityUpdate();
        this.getRunning().ifPresent((game) -> {
            if (missed > 0) {
                game.missed += missed;
                if (game.Streak > game.highestStreak) { game.highestStreak = game.Streak; }
                game.Streak = 0;
                game.player.playSound(game.player.getLocation(), Config.Game.MISSSOUND, 1, 1);
                game.player.sendMessage(Config.AppConfig.PREFIX + Translator.GAME_MOLEMISSED.Format(this));
                if (game.missed >= this.settings.missCount) {
                    this.Stop();
                }
            }
        });
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
                    if (this.settings.hasJackpot) {
                        if (DROP <= this.settings.jackpotSpawnChance) {
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
