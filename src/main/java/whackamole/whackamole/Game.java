package whackamole.whackamole;


import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
<<<<<<< HEAD
=======
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import whackamole.whackamole.Mole.*;
>>>>>>> 0f64a75 (SNAPSHOT -V1 :)

import org.bukkit.*;
import org.bukkit.entity.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

<<<<<<< HEAD
import whackamole.whackamole.Mole.*;
=======
import org.bukkit.inventory.meta.SkullMeta;
>>>>>>> 0d00087 (Commands:)


public class Game {
    public final BlockFace[] Directions = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};

    private final Logger  logger  = Logger.getInstance();
    private final Config  config  = Config.getInstance();
    private final Econ    econ    = Econ.getInstance();

    private YMLFile gameConfig;
    public  String  name;
    
    public HashMap<UUID, Long> cooldown         = new HashMap<>();
    private HashMap<UUID, Integer> missedMoles  = new HashMap<>();
    private ArrayList<UUID>  cooldownSendList    = new ArrayList<>();
    
    public Player gamePlayer;
    private World world;
<<<<<<< HEAD
    public Grid grid;
    public String gameName;
<<<<<<< HEAD
    public String gameType = "";
    public String scoreObjective = "";
<<<<<<< HEAD
    public Boolean cashHats = true;
    public Long Interval = 20L;
    public Integer pointsPerKill = 1;
<<<<<<< HEAD
<<<<<<< HEAD
    private World world;
=======
    private int ID = -1;
=======
=======
=======
    private Grid  grid;
<<<<<<< HEAD
    
>>>>>>> c279d38 (Config:)
    public boolean cashHats = true;
=======
    public boolean Jackpot = true;
    public boolean gameLost = false;
>>>>>>> 0d00087 (Commands:)
    public long Interval = 20L;
    public int pointsPerKill = 1;
    public int maxMissed = 3;
<<<<<<< HEAD
<<<<<<< HEAD
    public static int moleMissed = 0;
>>>>>>> de8c408 (Grid:)
=======
    public int moleMissed = 0;
>>>>>>> 86f74e4 (Changed: files cleanup)
    private int ID1 = -1;
<<<<<<< HEAD
>>>>>>> c865bc2 (Game starter:)
    private boolean Running = false;
>>>>>>> e262bdb (Game starter:)
=======
    public boolean Running = false;
>>>>>>> cfd2e6f (Config:)
=======
=======

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
>>>>>>> 2f10a80 (Beta release:)
    
    private int Score = 0;
    private int moleMissed = 0;
    private boolean Running = false;
    private int runnableIdentifier = -1;
<<<<<<< HEAD
    
    public String gameType = "";
    public String scoreObjective = "";
>>>>>>> c279d38 (Config:)
=======
    private int Tick = 0;

<<<<<<< HEAD
>>>>>>> 2f10a80 (Beta release:)

    public Game(YMLFile configFile) throws Exception {
        this.gameConfig = configFile;
        this.loadGame();
    }
    public Game(File configFile) throws Exception {
=======
    public Game(File configFile) {
>>>>>>> 0f64a75 (SNAPSHOT -V1 :)
        this.gameConfig = new YMLFile(configFile);
        this.loadGame();
    }
    public Game(String name, Grid grid, Player player) throws FileNotFoundException {
//        this.spawnRotation = player.getFacing().getOppositeFace();
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
        if (!this.Running && this.runnableIdentifier == -1 && player.hasPermission(this.config.PERM_PLAY) && this.placeAxe(player)) {
            this.moleSpeedScaled = this.moleSpeed;
            this.intervalScaled = this.Interval;
            this.spawnChanceScaled = this.spawnChance;
            this.gamePlayer = player;
            this.moleMissed = 0;
            this.Running = true;
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
<<<<<<< HEAD
<<<<<<< HEAD
            this.setCooldown(gamePlayer.getUniqueId());
        
=======
            if (this.Score > 0 || this.moleMissed == this.maxMissed) {
=======
            if (this.Score > 0 || this.moleMissed >= this.maxMissed) {
<<<<<<< HEAD
>>>>>>> 2f10a80 (Beta release:)
                this.setCooldown(gamePlayer.getUniqueId());
=======
                this.setCooldown(gamePlayer.getUniqueId(), this.parseTime(this.Cooldown));
>>>>>>> 0f64a75 (SNAPSHOT -V1 :)
                if (this.moleMissed >= this.maxMissed) this.missedMoles.put(this.gamePlayer.getUniqueId(), moleMissed);
            }
            if (econ.currencyType != Econ.Currency.NULL) {
                econ.depositPlayer(gamePlayer, this.Score);
                if (this.Score == 1) {
                    gamePlayer.sendMessage(this.config.PREFIX + "You've been rewarded " + ChatColor.AQUA + this.config.SYMBOL + this.Score + ChatColor.WHITE + " " + this.config.CURRENCY_SING);
                } else if (this.Score > 1) {
                    gamePlayer.sendMessage(this.config.PREFIX + "You've been rewarded " + ChatColor.AQUA + this.config.SYMBOL + this.Score + ChatColor.WHITE + " " + this.config.CURRENCY_PLUR);
                } else {
                    gamePlayer.sendMessage(this.config.PREFIX + "No moles hit, " + ChatColor.AQUA +  this.config.SYMBOL + "0 " + this.config.CURRENCY_PLUR + ChatColor.WHITE + " rewarded");

                }
<<<<<<< HEAD
>>>>>>> 0d00087 (Commands:)
            this.Score = 0;
            this.gamePlayer = null;
        }
    }
<<<<<<< HEAD
=======
    public void unload() {
        this.Stop();
        this.saveGame();
    }
    public void saveGame() {
>>>>>>> 0d00087 (Commands:)


    public void saveGame() {
        this.gameConfig.FileConfig.options().setHeader(Arrays.asList(
                "###########################################################",
                " ^------------------------------------------------------^ #",
                " |                       GameFile                       | #",
                " <------------------------------------------------------> #",
                "###########################################################",
                "NOTE: you can edit the Properties to change the game rules",
                "NOTE: Do not touch the Field Data!",
                "",
                "EXPLANATION:",
                "Name = name (make sure this is the same as the Filename)",
                "CashHats = a chance of 0.1% that a special mole will spawn giving a bigger reward",
                "Interval = XXXXXXXXXXXXXXXXXXXXXXXX",
                "Game lost = the amount of moles not hit before the game ends",
                "Points per Kill = how many points should be rewarded per kill",
                "That is all, enjoy messing around :)",
                ""));
<<<<<<< HEAD
        this.gameConfig.set("Properties.Name", this.name);
        this.gameConfig.set("Properties.CashHats", this.cashHats);
=======
        this.gameConfig.set("Properties.Name", this.gameName);
        this.gameConfig.set("Properties.Jackpot", this.Jackpot);
>>>>>>> 0d00087 (Commands:)
        this.gameConfig.set("Properties.Interval", this.Interval);
        this.gameConfig.set("Properties.Game lost", this.maxMissed);
        this.gameConfig.set("Properties.Points per Kill", this.pointsPerKill);
        this.gameConfig.set("Field Data.World", this.grid.world.getName());
        this.gameConfig.set("Field Data.Grid", this.grid.Serialize());
        this.gameConfig.save();
    }
    
    public void loadGame() {
        this.world          = Bukkit.getWorld(this.gameConfig.getString("Field Data.World"));
        this.grid           = Grid.Deserialize(this.world, (List<List<Integer>>) this.gameConfig.getList("Field Data.Grid"));
<<<<<<< HEAD
        this.name       = this.gameConfig.getString("Properties.Name");
        this.cashHats       = this.gameConfig.getBoolean("Properties.CashHats");
        this.Interval       = this.gameConfig.getLong("Properties.Interval");
        this.maxMissed      = this.gameConfig.getInt("Properties.Game lost");
        this.ticketCost     = this.gameConfig.getInt("Properties.Ticket price");
=======
        this.gameName       = this.gameConfig.getString("Properties.Name");
        this.Jackpot        = this.gameConfig.getBoolean("Properties.Jackpot");
        this.Interval       = this.gameConfig.getLong("Properties.Interval");
        this.maxMissed      = this.gameConfig.getInt("Properties.Game lost");
>>>>>>> 0d00087 (Commands:)
        this.pointsPerKill  = this.gameConfig.getInt("Properties.Points per Kill");
    }

    private void deleteSave() {
        this.gameConfig.remove();
=======
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
                player.sendMessage(this.config.PREFIX + "Inventory full, please empty a slot to play the game");
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
>>>>>>> 2f10a80 (Beta release:)
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
            logger.error("Cooldown has improperly been set, correct format is HH:mm:ss");
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
                    .append(ComponentSerializer.parse(config.ACTIONTEXT))
                    .append((Config.color("&2&l ") + this.Score))
                    .create();
            this.gamePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionMessage);
        }

        for(Map.Entry<UUID, Long> cooldownEntry : this.cooldown.entrySet()) {
            if(!this.hasCooldown(cooldownEntry.getKey())) {
                this.removeCooldown(cooldownEntry.getKey());
            }
<<<<<<< HEAD
            else if(this.cooldownSendList.contains(cooldownEntry.getKey())) {
                // TODO: look at this
                // BaseComponent[] resetMessage = new ComponentBuilder()
                //         .append(Config.color("&l"
                //                 + this.moleMissed
                //                 + " moles missed: &4&lGAME OVER&f&l, please buy a new ticket or wait. Time left: &a&l")
                //                 + this.formatCooldown(cooldownEntry.getValue()))
                //         .create();
                // Bukkit.getPlayer(cooldownEntry.getKey()).spigot().sendMessage(ChatMessageType.ACTION_BAR, resetMessage);

                BaseComponent[] resetMessage = new ComponentBuilder()
                    .append(Config.color("&4&lGAME OVER !&f&l please buy a new ticket or wait. Time left: &a&l"))
                    .append(this.formatCooldown(cooldownEntry.getValue()))
                    .create();
=======
            else if(this.cooldownSendList.contains(cooldownEntry.getKey()) && Bukkit.getPlayer(cooldownEntry.getKey()) != null) {
                BaseComponent[] resetMessage;
                if (this.missedMoles.containsKey(cooldownEntry.getKey())) {
                    resetMessage = new ComponentBuilder()
                            .append(Config.color("&l"
                                    + this.missedMoles.get(cooldownEntry.getKey())
                                    + " moles missed: &4&lGAME OVER&f&l, please buy a new ticket or wait. Time left: &a&l")
                                    + this.formatCooldown(cooldownEntry.getValue()))
                            .create();
                }else {
                    resetMessage = new ComponentBuilder()
                            .append(Config.color("&4&lGAME OVER !&f&l please buy a new ticket or wait. Time left: "))
                            .append(Config.color("&a&l") + this.formatCooldown(cooldownEntry.getValue()))
                            .create();
                }
>>>>>>> 2f10a80 (Beta release:)
                Bukkit.getPlayer(cooldownEntry.getKey()).spigot().sendMessage(ChatMessageType.ACTION_BAR, resetMessage);
            }
        }
    }

    public void moleUpdater() {
        int missed = this.grid.entityUpdate();
        if (missed > 0) {
            this.gamePlayer.playSound(gamePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            this.gamePlayer.sendMessage(this.config.PREFIX + "Mole missed, " + (this.moleMissed + missed) + "/" + this.maxMissed);
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
        this.gamePlayer.playSound(e.getDamager().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
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
        this.moleSpeedScaled = roundupDouble(this.moleSpeedScaled / Scaler);
        this.intervalScaled = roundupDouble(this.intervalScaled / Scaler);
        if (this.spawnChanceScaled < 100) {
            this.spawnChanceScaled = (int) ( this.spawnChanceScaled * Scaler);
            if (this.spawnChanceScaled > 100) this.spawnChanceScaled = 100;
        } else this.spawnChanceScaled = 100;
        this.difficultyPoints = 0;
    }

    public void saveGame() {
        this.gameConfig.FileConfig.options().setHeader(Arrays.asList(
                "###########################################################",
                " ^------------------------------------------------------^ #",
                " |                       GameFile                       | #",
                " <------------------------------------------------------> #",
                "###########################################################",
                "NOTE: you can edit the Properties to change the game rules",
                "NOTE: you can change properties with a decimal to a value with decimal, those without decimal should stay without decimal",
                "NOTE: Do not touch the Field Data!",
                "",
                "EXPLANATION:",
                "Name = name (make sure this is the same as the Filename)",
                "Direction = What direction the moles spawning should be facing",
                "Jackpot = enable the chance that a special mole will spawn giving triple the points if hit",
                "Jackpot spawn chance = the chance per mole spawn for a jackpot",
                "Game lost = the amount of moles not hit before the game ends",
                "Points per kill = how many points should be rewarded per kill",
                "Spawn rate = per how many seconds the mole has a chance of spawning",
                "Spawn chance = percentage of successful spawn per spawn rate",
                "Mole speed = how quick the mole should move up and down (the lower the quicker)",
                "Difficulty scaling = by how many percent the game should become more difficult (every x moles hit makes the game ..% more difficult)",
                "Difficulty increase = per how many moles hit the game difficulty should increase",
                "Cooldown = the cooldown when a player loses the game (HH:mm:ss)",
                "That is all, enjoy messing around :)"));
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
        this.logger.success("Loaded File: %s".formatted(this.gameConfig.file.getName()));
    }

    public void unload(boolean saveGame) {
        this.Stop();
        if (saveGame) this.saveGame();

    }

    public void deleteSave() {
        this.Stop();
        this.gameConfig.remove();
    }

    private double roundupDouble(double value) {
        DecimalFormat round = new DecimalFormat("###.##");
        return Double.parseDouble(round.format(value));
    }

}


