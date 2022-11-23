package whackamole.whackamole;

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
import java.awt.*;
>>>>>>> 6eaa2d8 (WAMHammer color fix)
=======
>>>>>>> e262bdb (Game starter:)
import java.io.File;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import java.security.spec.ECField;
>>>>>>> 1e62199 (change :))
=======
>>>>>>> 328aa81 (change :))
import java.util.ArrayList;
import java.util.List;
=======
import java.text.SimpleDateFormat;
=======
>>>>>>> 86f74e4 (Changed: files cleanup)
=======
import java.text.SimpleDateFormat;
>>>>>>> cfd2e6f (Config:)
=======
>>>>>>> 2b9819f (Econ :)
import java.util.*;
>>>>>>> de8c408 (Grid:)

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
<<<<<<< HEAD
=======
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
>>>>>>> 0d00087 (Commands:)
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public final class GamesManager implements Listener {

<<<<<<< HEAD
=======
    private Config config = Config.getInstance();

>>>>>>> e262bdb (Game starter:)
    private Logger logger = Logger.getInstance();

    public List<Game> games = new ArrayList<>();
    private static GamesManager Instance;
    public ItemStack ticket;
    public GamesManager() {

    }

    public GamesManager(Main main) {
        this.Ticket();
        this.loadGames();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, Tick, 1l, 1l);
    }
    public static GamesManager getInstance() {
        if (GamesManager.Instance == null) {
            GamesManager.Instance = new GamesManager();
        }
        return GamesManager.Instance;
    }

    public static GamesManager getInstance(Main main) {
        if (GamesManager.Instance == null) {
            GamesManager.Instance = new GamesManager(main);
        }
        return GamesManager.Instance;
    }

    public void loadGames() {
        File GamesFolder = this.config.gamesData;

        this.logger.info("Loading Games...");
        
        if (GamesFolder.list().length == 0) {
            this.logger.warning("...No games found");
            return;
        }

        for (File i : GamesFolder.listFiles()) {
            try {
                this.addGame(i);
            } catch (Exception e) {
                this.logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

<<<<<<< HEAD
    private boolean gameExists(String name) {
        this.logger.info(name);
        for (Game game : games) {
            if (game.name.toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
=======
    public void unloadGames() {
        for (Game game : games) {
            game.unload();
        }
>>>>>>> 0d00087 (Commands:)
    }

    public void addGame(Game game) throws Exception {
        if (this.gameExists(game.name)) {
            throw new Exception("Grid with name %s already exists!".formatted(game.name));
        }
        this.games.add(game);
    }

<<<<<<< HEAD
<<<<<<< HEAD
=======
    public void addGame(File gameName) {
=======
    public void addGame(File gameName) throws Exception {
<<<<<<< HEAD
>>>>>>> cffe322 (change :))
        this.games.add(new GameHandler(gameName));
=======
        this.games.add(new Game(gameName));
>>>>>>> 2b9819f (Econ :)
    }
>>>>>>> 461f3dd (updated: addGame method)
    public void addGame(String gameName, Grid grid) throws Exception {
        if (this.gameExists(gameName)) {
            throw new Exception("Grid with name %s already exists!".formatted(ChatColor.YELLOW + gameName + ChatColor.WHITE));
        }
        this.games.add(new Game(gameName, grid));
    }

    public void removeGame(Game game) {
        this.logger.info(game.name + " ...Successfully removed!");
        this.games.remove(game);
    }


    public Game getOnGrid(Player player) {
        for (Game game : games) {
            if (game.onGrid(player)) {
                return game;
            }
        }
        return null;
    }

    public void toggleArmorStands() {
        for (Game game : games) {
            game.toggleArmorStands();
        }
    }

    int runnableTickCounter = 0;
    Runnable Tick = new Runnable() {
        @Override
        public void run() {
            for (Game game : GamesManager.this.games) {
                game.moleUpdater();
            }

            if (GamesManager.this.runnableTickCounter >= 20) {
                GamesManager.this.runnableTickCounter = 0;
                for (Game game : GamesManager.this.games) {
                    game.displayActionBar();
                }
            }
            GamesManager.this.runnableTickCounter++;
        }
    };

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event) {
        for (Game game : games) {
            if (game.onGrid(event.getPlayer())) {
                if (!game.hasCooldown(event.getPlayer().getUniqueId())) {
                    game.Start(event.getPlayer());
                    break;
                }
            } else if (game.gamePlayer == event.getPlayer()) {
                game.Stop();
                break;
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.PLAYER) {
            for (Game game : this.games) {
                if(game.handleHitEvent((Player) e.getDamager(), e.getEntity())) {
                    e.setDamage(0);
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
<<<<<<< HEAD
=======

    @EventHandler
    public void ticketUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Game game = this.getOnGrid(player);
        if (player.getInventory().getItemInMainHand().equals(ticket) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (player.hasPermission(this.config.PERM_TICKET_USE)) {
                if (game == null) {
                    player.sendMessage(this.config.PREFIX + ChatColor.DARK_RED + "Stand on the game you wish to reset !");
                    e.setCancelled(true);
                } else if (game.hasCooldown(player.getUniqueId())) {
                    game.removeCooldown(player.getUniqueId());
                    e.getPlayer().sendMessage(this.config.PREFIX + ChatColor.AQUA + "Cooldown successfully removed !");
                    e.setUseItemInHand(Event.Result.DENY);
                    player.getInventory().removeItem(ticket);
                }  else {
                    player.sendMessage(this.config.PREFIX + ChatColor.DARK_RED + "You can't use this item without cooldown !");
                    e.setCancelled(true);
                }
            } else {
                player.sendMessage(this.config.PREFIX + ChatColor.DARK_RED + "You don't seem to have the right permission to use this!");
                e.setCancelled(true);
            }
        }
    }


    public void toggleArmorStands() {
        for (Game game : games) {
            game.toggleArmorStands();
        }
    }

    public void Ticket() {
        ItemStack ticket = new ItemStack(Material.MAP);
        ItemMeta ticketInfo = ticket.getItemMeta();
        ticketInfo.addEnchant(Enchantment.LURE, 1, true);
        ticketInfo.setDisplayName(Config.color("&0&l[&6&lWAM&0&l] &b&lReset Ticket"));
        ticketInfo.setLore(Arrays.asList(Config.color("&eRight click this ticket"), Config.color("&ewhen on top of a WhackaMole gamefield"), Config.color("&eto reset the waiting time!")));
        ticketInfo.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);


        ticket.setItemMeta(ticketInfo);

        this.ticket = ticket;
    }
>>>>>>> 0d00087 (Commands:)
}
