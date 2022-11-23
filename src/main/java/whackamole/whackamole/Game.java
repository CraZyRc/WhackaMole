package whackamole.whackamole;


import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import org.bukkit.*;
import org.bukkit.entity.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

<<<<<<< HEAD
import whackamole.whackamole.Mole.*;
=======
import org.bukkit.inventory.meta.SkullMeta;
>>>>>>> 0d00087 (Commands:)


public class Game {

    private Logger  logger  = Logger.getInstance();
    private Config  config  = Config.getInstance();
    private Econ    econ    = Econ.getInstance();

    private YMLFile gameConfig;
    public  String  name;
    
    public HashMap<UUID, Long> cooldown      = new HashMap<>();
    public ArrayList<UUID>  cooldownSendList = new ArrayList<>();
    
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
    
    public int Score = 0;
    public int moleMissed = 0;
    public boolean Running = false;
    public boolean gameLost = false;
    private boolean debug = false;
    private int runnableIdentifier = -1;
    
    public String gameType = "";
    public String scoreObjective = "";
>>>>>>> c279d38 (Config:)

    public Game(YMLFile configFile) throws Exception {
        this.gameConfig = configFile;
        this.loadGame();
    }
    public Game(File configFile) throws Exception {
        this.gameConfig = new YMLFile(configFile);
        this.loadGame();
    }
    public Game(String name, Grid grid) throws FileNotFoundException {
        this.name = formatName(name);
        this.gameConfig = new YMLFile(this.config.gamesData, name + ".yml");
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
                this.cooldownSendList.remove(player.getUniqueId());
            }
        }
        return playerOnGrid;
    }

    public boolean onGrid(Location loc) {
        return this.grid.onGrid(loc);
    }


    public void Start(Player player) {
        if (!this.Running && this.runnableIdentifier == -1) {
            this.gamePlayer = player;
            this.moleMissed = 0;
            this.gameLost = false;
            this.Running = true;
            player.getInventory().addItem(this.config.PLAYER_AXE);
            this.runnableIdentifier = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, this.Runner, this.Interval * 20L, this.Interval * 20L);
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
            this.setCooldown(gamePlayer.getUniqueId());
        
=======
            if (this.Score > 0 || this.moleMissed == this.maxMissed) {
                this.setCooldown(gamePlayer.getUniqueId());
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
                }
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
    }


    public void toggleArmorStands() {
        this.debug = !this.debug;
        if (this.debug) {
            this.grid.spawnArmorStands();
        } else {
            this.grid.removeArmorStands();
        }
    }


    public boolean hasCooldown(UUID player) {
        if (this.cooldown.containsKey(player) && this.cooldown.get(player) > System.currentTimeMillis()) {
            return true;
        } else {
            return false;
        }
    }

    public void setCooldown(UUID player) {
        if (!hasCooldown(player)) {
            this.cooldown.put(player, System.currentTimeMillis() + 86400000);
        }
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
                Bukkit.getPlayer(cooldownEntry.getKey()).spigot().sendMessage(ChatMessageType.ACTION_BAR, resetMessage);
            }
        }
    }

    public void moleUpdater() {
        this.moleMissed = this.grid.entityUpdate();
        if (moleMissed == maxMissed) {
            this.gameLost = true;
            this.Stop();
        }
    }

    public boolean handleHitEvent(Player player, Entity e) {
        if(!this.Running) return false;
        if(player != this.gamePlayer) return false;

        boolean hasHit = this.grid.handleHitEvent(e);
        if(hasHit) this.Score++;

        return hasHit;
    }

    Runnable Runner = new Runnable() {
        @Override
        public void run() {
            Game.this.grid.spawnRandomEntity(MoleType.Mole);

        }
    };
}


