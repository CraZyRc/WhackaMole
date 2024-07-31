package whackamole.whackamole.GS;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import whackamole.whackamole.Config;
import whackamole.whackamole.DB.*;
import whackamole.whackamole.Grid;
import whackamole.whackamole.Mole;
import whackamole.whackamole.Mole.MoleState;
import whackamole.whackamole.Mole.MoleType;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;


public class Game {
    public static final BlockFace[] Directions = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
            BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
    public enum gameState {READY, RUNNING, STOPPING, REWARDING, DISABLED}

    public gameState State;
    public Settings settings = new Settings(this);
    public CooldownList cooldown = new CooldownList(this);
    public Scoreboard scoreboard = new Scoreboard(this);
    public GameRunner game;
    public Grid grid;

    public List<Player> hasActionbar = new ArrayList<>();
    private Random random = new Random();
    private List<UUID> currentyOnGird = new ArrayList<>();


    public Game(GameRow result) {
        this.setState(gameState.READY);
        this.settings.onLoad(result);

        this.cooldown.onLoad();
        this.scoreboard.onLoad();
        this.grid = new Grid(settings);
        Logger.success(Translator.GAME_LOADSUCCESS.Format(this.getName()));
    }


    public Game(String name, Grid grid, Player player) {
        this.setState(gameState.READY);

        this.settings.scoreLocation = player.getLocation().add(0,1,0);
        this.settings.Setup(formatName(name), player);

        this.grid = grid.setSettings(settings);
        this.scoreboard.createTopHolo();
    }

    public void setState(gameState state) {
        this.State = state;
        switch (state) {
            case READY -> this.game = new GameRunner(this);
            case RUNNING -> {}
            case STOPPING -> this.game.Stop();
            case REWARDING -> RewardsManager.executeRewards(this);
            case DISABLED -> Logger.error("fix this code");
        }

    }

    private String formatName(String name) {
        char[] chars = name.toLowerCase().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public void Start(Player player) {
        if (this.State == gameState.READY) {
            this.game.player = player;
            if (this.game.Start(player)) this.setState(gameState.RUNNING);
        }
    }

    public void Stop() {
        if (this.game != null) {
            this.setState(gameState.STOPPING);
        }
    }

    public void Unload() {
        if (this.State == gameState.RUNNING) {
            this.Stop();
        }
    }

    public void Save() {
        this.settings.Save();
    }
    public void Delete() {
        this.grid.Delete();
        this.cooldown.Delete();
        this.scoreboard.Delete();
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

    public void setMusic(String Music) {
        this.settings.Music = Music;
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
        scoreboard.tpTopHolo(loc);
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
    public void setToggleScoreboard(Boolean value) {
        this.settings.toggleScoreboard = value;
        this.Save();

        if (value) {
            this.getScoreboard().createTopHolo();
        } else if (getScoreboard().checkHolo()) {
            this.getScoreboard().killTopHolo();
        }
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

        boolean playerOnGrid = (player.getWorld() == settings.world && this.grid.onGrid(player));

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

    public boolean onGrid(Player player, Location loc) {

        boolean playerOnGrid = (player.getWorld() == settings.world && this.grid.onGrid(loc));

        // * Player walks on the grid
        if (playerOnGrid && !currentyOnGird.contains(player.getUniqueId()) && this.State == gameState.READY) {
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
        if (loc.getWorld() != settings.world ) {
            return false;
        }
        
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
    }

    public void actionbarParse(UUID player, String text) {
        Bukkit.getPlayer(player).spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new ComponentBuilder()
                        .append(text + "")
                        .create());
    }
    public void actionbarParse(UUID player, Translator text, String text2) {
        Bukkit.getPlayer(player).spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new ComponentBuilder()
                        .append(text + text2)
                        .create());
    }

    public void updateActionBar() {
        if (this.State == gameState.RUNNING) {
            Game.this.getRunning().ifPresent((game) -> {
                this.actionbarParse(game.player.getUniqueId(), Misc.Color(Translator.GAME_ACTIONBAR_CURRENTSCORE + "&a&l ") + this.game.score);
//            game.player.sendMessage(Translator.GAME_ACTIONBAR_CURRENTSCORE.toString());
            });
        }
        for (UUID player : this.currentyOnGird) {
            if (this.cooldown.contains(player) && this.State != gameState.REWARDING) {
                this.actionbarParse(player, Translator.GAME_ACTIONBAR_GAMEOVER, this.cooldown.getText(player));
            }
            else if (Bukkit.getPlayer(player).getInventory().firstEmpty() != -1 && Game.this.State != gameState.RUNNING && this.State != gameState.REWARDING) {
                this.actionbarParse(player, Translator.GAME_ACTIONBAR_RESTART.Format());
            }
            else if (Bukkit.getPlayer(player).getInventory().firstEmpty() != -1 && Game.this.State == gameState.REWARDING){
                this.actionbarParse(player, Translator.GAME_ACTIONBAR_REWARDING.Format());
            }
        }
    }


    public boolean handleHitEvent(EntityDamageByEntityEvent e) {
        if (this.getRunning().isEmpty())
            return false;

        Optional<Mole> optionalMole = this.grid.handleHitEvent(e.getEntity());
        if (optionalMole.isEmpty())
            return false;

        Player player = (Player) e.getDamager();
        if (player != this.game.player || !player.getInventory().getItemInMainHand().equals(Config.Game.PLAYER_AXE)) {
            e.setCancelled(true);
            return true;
        }
        
        Mole mole = optionalMole.get();
        this.game.moleHit(mole);
        mole.state = MoleState.Hit;

        player.playSound(e.getDamager().getLocation(), Config.Game.HITSOUND, 1, 1);
        player.spawnParticle(Particle.COMPOSTER, mole.mole.getLocation().add(0, 1.75, 0), 10, 0.1, 0.1, 0.1, 0);

        updateActionBar();

        return true;
    }

    public void moleUpdater() {
        if (this.State == gameState.RUNNING) {
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
    }

    private int Tick = 0;

    public void run() {
        if (this.State == gameState.RUNNING) {
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
