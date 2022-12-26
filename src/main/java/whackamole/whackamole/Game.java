package whackamole.whackamole;


import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import whackamole.whackamole.Mole.*;

import org.bukkit.*;
import org.bukkit.entity.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Game {
    public final BlockFace[] Directions = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};

    private final Logger  logger  = Logger.getInstance();
    private final Config  config  = Config.getInstance();
    private final Econ    econ    = Econ.getInstance();
    private final Translator translator = Translator.getInstance();

    private YMLFile gameConfig;
    public  String  name;
    
    public HashMap<UUID, Long> cooldown         = new HashMap<>();
    private HashMap<UUID, Integer> missedMoles  = new HashMap<>();
    private ArrayList<UUID>  cooldownSendList    = new ArrayList<>();
    
    public Player gamePlayer;
    private World world;
    private Grid  grid;

    private Random random = new Random();

    public boolean Jackpot = true;
    public BlockFace spawnRotation;
    public double Interval = 1;
    public double spawnChance = 100;
    public int jackpotSpawn = 1;
    public double difficultyScale = 10;
    public double moleSpeed = 2;
    public int difficultyPoints = 0;
    private int difficultyScore = 1;
    public int pointsPerKill = 1;
    public int maxMissed = 3;
    public String Cooldown = "24:00:00";
    public double moleSpeedScaled;
    public double intervalScaled;
    public double spawnChanceScaled;
    
    private int Score = 0;
    private int moleMissed = 0;
    private boolean Running = false;
    private int runnableIdentifier = -1;
    private int Tick = 0;

    public Game(File configFile) {
        this.gameConfig = new YMLFile(configFile);
        this.loadGame();
    }
    public Game(String name, Grid grid, Player player) throws FileNotFoundException {
        this.spawnRotation = Directions[Math.round(player.getLocation().getYaw()/45) & 0x7];
        this.name = formatName(name);
        this.gameConfig = new YMLFile(this.config.gamesData, this.name + ".yml");
        this.grid = grid;
        this.saveGame();
    }

    private String formatName(String name) {
        char[] chars = name.toLowerCase().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }


    public boolean onGrid(Player player) {
        boolean playerOnGrid = this.grid.onGrid(player);
        if (this.cooldown.containsKey(player.getUniqueId())) {
            if (playerOnGrid && !this.cooldownSendList.contains(player.getUniqueId())) {
                this.cooldownSendList.add(player.getUniqueId());
            } else if (!playerOnGrid && this.cooldownSendList.contains(player.getUniqueId())) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder().append("").create());
                this.missedMoles.remove(player.getUniqueId());
                this.cooldownSendList.remove(player.getUniqueId());
            }
        }
        return playerOnGrid;
    }


    public void Start(Player player) {
        if (this.econ.currencyType != Econ.Currency.NULL) {
            if (!this.Running && this.runnableIdentifier == -1 && player.hasPermission(this.config.PERM_PLAY) && this.placeAxe(player)) {
                this.moleSpeedScaled = this.moleSpeed;
                this.intervalScaled = this.Interval;
                this.spawnChanceScaled = this.spawnChance;
                this.gamePlayer = player;
                this.moleMissed = 0;
                this.Running = true;
            }
        } else {
            this.logger.error(this.translator.GAME_INVALIDECONOMY);
            this.setCooldown(player.getUniqueId(), 10000L);
        }
    }

    public void Stop() {
        if (this.Running) {
            Bukkit.getScheduler().cancelTask(runnableIdentifier);
            this.Running = false;
            this.runnableIdentifier = -1;
            this.grid.removeEntities();

            this.gamePlayer.getInventory().removeItem(this.config.PLAYER_AXE);
            this.gamePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            if (this.Score > 0 || this.moleMissed >= this.maxMissed) {
                this.setCooldown(gamePlayer.getUniqueId(), this.parseTime(this.Cooldown));
                if (this.moleMissed >= this.maxMissed) this.missedMoles.put(this.gamePlayer.getUniqueId(), moleMissed);
            }
            econ.depositPlayer(gamePlayer, this.Score);
            if (this.Score == 1) {
                gamePlayer.sendMessage(this.config.PREFIX + this.translator.Format(this.translator.GAME_STOP_REWARD_SING, String.valueOf(this.Score)));
            } else if (this.Score > 1) {
                gamePlayer.sendMessage(this.config.PREFIX + this.translator.Format(this.translator.GAME_STOP_REWARD_PLUR, String.valueOf(this.Score)));
            } else {
                gamePlayer.sendMessage(this.config.PREFIX + this.translator.Format(this.translator.GAME_STOP_REWARD_NONE));
            }
            this.moleSpeedScaled = this.moleSpeed;
            this.intervalScaled = this.Interval;
            this.spawnChanceScaled = this.spawnChance;
            this.Score = 0;
            this.gamePlayer = null;

        }
    }
    private boolean placeAxe(Player player) {
        if (!player.getInventory().getItemInMainHand().equals(Material.AIR)) {
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(this.config.PREFIX + this.translator.GAME_START_FULLINVENTORY);
                this.setCooldown(player.getUniqueId(), 10000L);
                return false;
            } else {
                player.getInventory().setItem(player.getInventory().firstEmpty(), player.getInventory().getItemInMainHand());
                player.getInventory().setItemInMainHand(this.config.PLAYER_AXE);
                return true;
            }
        } else return false;
    }

    public void onPlayerExit(Player player) {
        this.missedMoles.remove(player.getUniqueId());
        if (Running) this.Stop();
    }


    public boolean hasCooldown(UUID player) {
        return this.cooldown.containsKey(player) && this.cooldown.get(player) > System.currentTimeMillis();
    }

    private void setCooldown(UUID player, Long Cooldown) {
        if (!hasCooldown(player)) {
            this.cooldown.put(player, System.currentTimeMillis() + Cooldown);
        }
    }
    private Long parseTime(String time) {
        if (time == null || time.isEmpty()) {
            logger.error(this.translator.GAME_INVALIDCOOLDOWN);
            return 0L;
        }

        String[] fields = time.split(":");
        Long Hour = Long.parseLong(fields[0])*3600000;
        Long Minutes = Long.parseLong(fields[1])*60000;
        Long Seconds = Long.parseLong(fields[2])*1000;
        return Hour + Minutes + Seconds;

    }

    public void removeCooldown(UUID player) {
        this.cooldown.remove(player);
    }

    private String formatCooldown(long time) {
        time = time - System.currentTimeMillis();
        int seconds = (int) (Math.floorDiv(time, 1000));
        int minutes = (Math.floorDiv(seconds, 60));
        int hours = (Math.floorDiv(minutes, 60));

        seconds = seconds % 60;
        minutes = minutes % 60;
        
        return      (hours      > 0 ? hours     > 9 ? String.valueOf(hours)     : "0" + hours   : "00")
            + ":" + (minutes    > 0 ? minutes   > 9 ? String.valueOf(minutes)   : "0" + minutes : "00")
            + ":" + (seconds    > 0 ? seconds   > 9 ? String.valueOf(seconds)   : "0" + seconds : "00");
    }

    public void displayActionBar() {
        if(this.Running) {
            BaseComponent[] actionMessage = new ComponentBuilder()
                    .append(ComponentSerializer.parse(this.config.ACTIONTEXT))
                    .append((Config.color("&2&l ") + this.Score))
                    .create();
            this.gamePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionMessage);
        }

        for(Map.Entry<UUID, Long> cooldownEntry : this.cooldown.entrySet()) {
            if(!this.hasCooldown(cooldownEntry.getKey())) {
                this.removeCooldown(cooldownEntry.getKey());
            }
            else if(this.cooldownSendList.contains(cooldownEntry.getKey()) && Bukkit.getPlayer(cooldownEntry.getKey()) != null) {
                BaseComponent[] resetMessage;
                if (this.econ.currencyType == Econ.Currency.NULL) {
                    resetMessage = new ComponentBuilder()
                            .append(Config.color(this.translator.GAME_ACTIONBAR_ERROR))
                            .create();
                } else if (this.missedMoles.containsKey(cooldownEntry.getKey())) {
                    resetMessage = new ComponentBuilder()
                            .append(Config.color(this.translator.Format(this.translator.GAME_ACTIONBAR_MOLEGAMEOVER, String.valueOf(this.missedMoles.get(cooldownEntry.getKey())))) + this.formatCooldown(cooldownEntry.getValue()))
                            .create();
                }else {
                    resetMessage = new ComponentBuilder().append(Config.color(this.translator.GAME_ACTIONBAR_GAMEOVER)
                                    + this.formatCooldown(cooldownEntry.getValue()))
                            .create();
                }
                Bukkit.getPlayer(cooldownEntry.getKey()).spigot().sendMessage(ChatMessageType.ACTION_BAR, resetMessage);
            }
        }
    }

    public void moleUpdater() {
        int missed = this.grid.entityUpdate();
        if (missed > 0) {
            this.gamePlayer.playSound(gamePlayer.getLocation(), this.config.MISSSOUND, 1, 1);
            this.gamePlayer.sendMessage(this.config.PREFIX + this.translator.Format(this.translator.GAME_MOLEMISSED, String.valueOf((this.moleMissed + missed)), String.valueOf(this.maxMissed)));
        }
        this.moleMissed += missed;
        if (moleMissed >= maxMissed) {
            this.Stop();
        }
    }

    public boolean handleHitEvent(EntityDamageByEntityEvent e) {
        if(!this.Running) return false;
        Player player = (Player) e.getDamager();
        Mole mole = this.grid.handleHitEvent(e.getEntity());

        if (mole == null) return false;
        if (player != this.gamePlayer || !player.getInventory().getItemInMainHand().equals(this.config.PLAYER_AXE)) {
            e.setCancelled(true);
            return true;
        }

        if (mole.type == MoleType.Mole) {
            this.Score = this.Score + this.pointsPerKill;
        } else if (mole.type == MoleType.Jackpot) {
            this.Score = this.Score + (this.pointsPerKill*3);
        }
        this.gamePlayer.playSound(e.getDamager().getLocation(), this.config.HITSOUND, 1, 1);
        player.spawnParticle(Particle.COMPOSTER, mole.mole.getLocation().add(0, 1.75, 0), 10, 0.1, 0.1, 0.1, 0);
        this.difficultyPoints++;
        if (Game.this.difficultyPoints == Game.this.difficultyScore) setSpeedScale();
        mole.state = MoleState.Hit;
        return true;
    }


    public void run() {
        if (Running) {
            double gameInterval = intervalScaled * 20;
            Tick++;
            if (Tick >= gameInterval) {
                double Speed = 1/(this.moleSpeedScaled*10);
                final int DROP = random.nextInt(100);
                if (DROP <= Game.this.spawnChanceScaled) {
                    if (Game.this.Jackpot) {
                        if (DROP <= Game.this.jackpotSpawn) {
                            this.grid.spawnRandomEntity(MoleType.Jackpot, Speed, spawnRotation);
                        } else {
                            this.grid.spawnRandomEntity(MoleType.Mole, Speed, spawnRotation);
                        }
                    } else {
                        this.grid.spawnRandomEntity(MoleType.Mole, Speed, spawnRotation);
                    }
                }
                Tick = 0;
            }
        }

    }
    private void setSpeedScale() {
        double Scaler = 1 + (this.difficultyScale/100);
        this.moleSpeedScaled = this.moleSpeedScaled / Scaler;
        this.intervalScaled = this.intervalScaled / Scaler;
        if (this.spawnChanceScaled < 100) {
            this.spawnChanceScaled = (int) ( this.spawnChanceScaled * Scaler);
            if (this.spawnChanceScaled > 100) this.spawnChanceScaled = 100;
        } else this.spawnChanceScaled = 100;
        this.difficultyPoints = 0;
    }

    public void saveGame() {
        this.gameConfig.FileConfig.options().header(
                "###########################################################"+
                "\n ^------------------------------------------------------^ #"+
                "\n |                       GameFile                       | #"+
                "\n <------------------------------------------------------> #"+
                "\n###########################################################"+
                "\n " + this.translator.GAME_CONFIG_FIRSTNOTE+
                "\n " + this.translator.GAME_CONFIG_SECONDNOTE+
                "\n " + this.translator.GAME_CONFIG_THIRDNOTE+
                "\n"+
                "\n"+
                "\n###########################################################"+
                "\n ^------------------------------------------------------^ #"+
                "\n |                      Explanation                     | #"+
                "\n <------------------------------------------------------> #"+
                "\n###########################################################"+
                "\n " + this.translator.GAME_CONFIG_NAME+
                "\n " + this.translator.GAME_CONFIG_DIRECTION+
                "\n " + this.translator.GAME_CONFIG_JACKPOT+
                "\n " + this.translator.GAME_CONFIG_JACKPOTSPAWN+
                "\n " + this.translator.GAME_CONFIG_GAMELOST+
                "\n " + this.translator.GAME_CONFIG_POINTSPERKILL+
                "\n " + this.translator.GAME_CONFIG_SPAWNRATE+
                "\n " + this.translator.GAME_CONFIG_SPAWNCHANCE+
                "\n " + this.translator.GAME_CONFIG_MOLESPEED+
                "\n " + this.translator.GAME_CONFIG_DIFFICULTYSCALE+
                "\n " + this.translator.GAME_CONFIG_DIFFICULTYINCREASE+
                "\n " + this.translator.GAME_CONFIG_COOLDOWN+
                "\n " + this.translator.GAME_CONFIG_ENDMESSAGE+
                "\n ");
        this.gameConfig.set("Properties.Name", this.name);
        this.gameConfig.set("Properties.Direction", this.spawnRotation.name());
        this.gameConfig.set("Properties.Jackpot", this.Jackpot);
        this.gameConfig.set("Properties.Jackpot spawn chance", this.jackpotSpawn);
        this.gameConfig.set("Properties.Game lost", this.maxMissed);
        this.gameConfig.set("Properties.Points per kill", this.pointsPerKill);
        this.gameConfig.set("Properties.Spawn rate", this.Interval);
        this.gameConfig.set("Properties.Spawn chance", this.spawnChance);
        this.gameConfig.set("Properties.Mole speed", this.moleSpeed);
        this.gameConfig.set("Properties.Difficulty scaling", this.difficultyScale);
        this.gameConfig.set("Properties.Difficulty increase", this.difficultyScore);
        this.gameConfig.set("Properties.Cooldown", this.Cooldown);
        this.gameConfig.set("Field Data.World", this.grid.world.getName());
        this.gameConfig.set("Field Data.Grid", this.grid.Serialize());
        this.gameConfig.save();
    }

    public void loadGame() {
        this.world              = Bukkit.getWorld(this.gameConfig.getString("Field Data.World"));
        this.grid               = Grid.Deserialize(this.world, this.gameConfig.getList("Field Data.Grid"));
        this.name               = this.gameConfig.getString("Properties.Name");
        this.spawnRotation      = BlockFace.valueOf(this.gameConfig.getString("Properties.Direction"));
        this.Jackpot            = this.gameConfig.getBoolean("Properties.Jackpot");
        this.jackpotSpawn       = this.gameConfig.getInt("Properties.Jackpot spawn chance");
        this.maxMissed          = this.gameConfig.getInt("Properties.Game lost");
        this.pointsPerKill      = this.gameConfig.getInt("Properties.Points per kill");
        this.Interval           = this.gameConfig.getDouble("Properties.Spawn rate");
        this.spawnChance        = this.gameConfig.getDouble("Properties.Spawn chance");
        this.moleSpeed          = this.gameConfig.getDouble("Properties.Mole speed");
        this.difficultyScale    = this.gameConfig.getDouble("Properties.Difficulty scaling");
        this.difficultyScore    = this.gameConfig.getInt("Properties.Difficulty increase");
        this.Cooldown           = this.gameConfig.getString("Properties.Cooldown");
        this.logger.success(this.translator.Format(this.translator.GAME_LOADSUCCESS, this.gameConfig.file));
    }

    public void unload(boolean saveGame) {
        this.Stop();
        if (saveGame) this.saveGame();

    }

    public void deleteSave() {
        this.Stop();
        this.gameConfig.remove();
    }

}


