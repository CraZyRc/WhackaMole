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
import net.md_5.bungee.chat.ComponentSerializer;
import whackamole.whackamole.DB.*;
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
            if (this.ID != -1) 
            gameDB.Delete(this);
        }
    }

    public class GameFile {
        private final YMLFile gameConfig;

        public GameFile() {
            gameConfig = new YMLFile(Config.AppConfig.storageFolder + "/Games/" + getName() + ".yml");
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
            this.gameConfig.set("Field Data.World", Game.this.grid.world.getName());
            this.gameConfig.set("Field Data.Grid", Game.this.grid.Serialize());
            try {
                this.gameConfig.save();
            } catch (Exception ignored) {
            }
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
            Game.this.settings.Save();
            
            Game.this.grid = Grid.Deserialize(Game.this.settings.world, this.gameConfig.getList("Field Data.Grid"));
            Game.this.grid.Delete(getID());
            Game.this.grid.Save(getID());

            Logger.success(Translator.GAME_LOADSUCCESS.Format(Game.this));
        }

        public void delete() {
            this.gameConfig.remove();
        }
    }

    public class Scoreboard {

        private List<ScoreboardRow> scores = new ArrayList<>();
        private ScoreboardDB db = SQLite.getScoreboardDB();

        public void add(Player player, int score, int molesHit, int scoreStreak) {
            ScoreboardRow scoreItem = this.db.Insert(player.getUniqueId(), getID(), score, molesHit, scoreStreak);
            this.scores.add(scoreItem);
        }

        private void onLoad() {
            this.scores = db.Select(getID());
        }
        
        private void Delete() {
            for (var row : scores) {
                db.Delete(row);
            }
        }

        public List<ScoreboardRow> getTop() {
            return getTop(10);
        }

        public List<ScoreboardRow> getTop(int count) {
            this.scores.sort((a, b) -> {
                return a.Score - b.Score;
            });
            return this.scores.subList(0, count);
        }
    }

    public class GameRunner {
        public Player player;
        public int score = 0, missed = 0, difficultyModifier = 0, molesHit = 0, highestStreak = 0, Streak = 0;

        public double moleSpeed = settings.moleSpeed, interval = settings.spawnTimer, spawnChance = settings.spawnChance;

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
                    this.score += settings.scorePoints;
                    this.molesHit ++;
                    this.Streak ++;
                    break;
                case Jackpot:
                    this.score += settings.scorePoints * 3;
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
    private Settings settings = new Settings();
    private CooldownList cooldown = new CooldownList();
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
    }

    public Game(String name, Grid grid, Player player) {
        this.settings.Setup(formatName(name), player);

        this.grid = grid;
        this.grid.Save(getID());

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
        this.settings.Save();
    }
    public void delete() {
        this.cooldown.Delete();
        this.scoreboard.Delete();
        this.grid.Delete(getID());
        this.settings.Delete();
        if (this.gameFile != null)
            this.gameFile.delete();
    }

    public int getID() {
        return this.settings.ID;
    }

    public String getName() {
        return this.settings.Name;
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
        this.settings.jackpotSpawnChance = jackpotSpawn;
        this.save();
    }

    public void setDifficultyScore(int difficultyScore) {
        this.settings.difficultyScore = difficultyScore;
        this.save();
    }

    public void setPointsPerKill(int pointsPerKill) {
        this.settings.scorePoints = pointsPerKill;
        this.save();
    }

    public void setMaxMissed(int maxMissed) {
        this.settings.missCount = maxMissed;
        this.save();
    }

    public void setInterval(double Interval) {
        this.settings.spawnTimer = Interval;
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
        this.settings.hasJackpot = Jackpot;
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
            if (this.getRunning().missed >= this.settings.missCount) {
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
