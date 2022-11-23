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
<<<<<<< HEAD
=======
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
>>>>>>> 0f64a75 (SNAPSHOT -V1 :)
import org.bukkit.event.player.PlayerInteractEvent;
<<<<<<< HEAD
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
=======
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerQuitEvent;
>>>>>>> 2f10a80 (Beta release:)


public final class GamesManager implements Listener {

<<<<<<< HEAD
<<<<<<< HEAD
=======
    private Config config = Config.getInstance();

>>>>>>> e262bdb (Game starter:)
    private Logger logger = Logger.getInstance();

    public List<Game> games = new ArrayList<>();
=======
    private final Config config = Config.getInstance();

    private final Logger logger = Logger.getInstance();

    private List<Game> games = new ArrayList<>();
    private int runnableTickCounter = 0;
    
>>>>>>> 0f64a75 (SNAPSHOT -V1 :)
    private static GamesManager Instance;
    public ItemStack ticket;
    public GamesManager() {

    }

    public GamesManager(Main main) {
        this.Ticket();
        this.loadGames();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, Tick, 1L, 1L);
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
            if (game.name.equalsIgnoreCase(name)) {
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

<<<<<<< HEAD
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
=======
    public void addGame(File gameName) {
>>>>>>> 0f64a75 (SNAPSHOT -V1 :)
        this.games.add(new Game(gameName));
>>>>>>> 2b9819f (Econ :)
    }
<<<<<<< HEAD
>>>>>>> 461f3dd (updated: addGame method)
    public void addGame(String gameName, Grid grid) throws Exception {
=======
    public void addGame(String gameName, Grid grid, Player player) throws Exception {
>>>>>>> 0f64a75 (SNAPSHOT -V1 :)
        if (this.gameExists(gameName)) {
            throw new Exception("Grid with name %s already exists!".formatted(ChatColor.YELLOW + gameName + ChatColor.WHITE));
        }
        this.games.add(new Game(gameName, grid, player));
    }

<<<<<<< HEAD
    public void removeGame(Game game) {
        this.logger.info(game.name + " ...Successfully removed!");
        this.games.remove(game);
=======
    public void unloadGames(boolean saveGames) {
        for (Game game : games) {
            game.unload(saveGames);
        }
        this.games.clear();
    }

    
    public void removeGame(Game game) {
        game.deleteSave();
        this.games.remove(game);
<<<<<<< HEAD
        this.logger.info(game + " ...Successfully removed!");
>>>>>>> 2f10a80 (Beta release:)
=======
>>>>>>> 0f64a75 (SNAPSHOT -V1 :)
    }


    public Game getOnGrid(Player player) {
        for (Game game : games) {
            if (game.onGrid(player)) {
                return game;
            }
        }
        return null;
    }
    Runnable Tick = () -> {
        for (Game game : GamesManager.this.games) {
            game.run();
            game.moleUpdater();
        }

        if (GamesManager.this.runnableTickCounter >= 20) {
            GamesManager.this.runnableTickCounter = 0;
            for (Game game : GamesManager.this.games) {
                game.displayActionBar();
            }
        }
        GamesManager.this.runnableTickCounter++;
    };

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        for (Game game : games) {
            if (game.gamePlayer == player) game.onPlayerExit(player);
        }
    }
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
                if(game.handleHitEvent(e)) {
                    break;
                }
            }
        }
    }
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
    @EventHandler
    public void itemDrop(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().equals(this.config.PLAYER_AXE)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    private void itemMove(InventoryClickEvent e) {
        if (e.getCurrentItem().equals(this.config.PLAYER_AXE)) {
            e.setCancelled(true);
        }
    }
>>>>>>> 0f64a75 (SNAPSHOT -V1 :)

    @EventHandler
    public void ticketUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Game game = this.getOnGrid(player);
<<<<<<< HEAD
        if (player.getInventory().getItemInMainHand().equals(ticket) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
=======
        if (player.getInventory().getItemInMainHand().isSimilar(this.config.TICKET)
            && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
        ) {
>>>>>>> 646e2ae (STABLE v1.1 :)
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
