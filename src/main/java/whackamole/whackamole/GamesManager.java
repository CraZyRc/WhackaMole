package whackamole.whackamole;

import java.io.File;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerQuitEvent;


public final class GamesManager implements Listener {

    private final Config config = Config.getInstance();

    private final Logger logger = Logger.getInstance();
    private final Translator translator = Translator.getInstance();

    private List<Game> games = new ArrayList<>();
    private int runnableTickCounter = 0;
    
    private static GamesManager Instance;

    public GamesManager() {

    }

    public GamesManager(Main main) {
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

        this.logger.info(this.translator.MANAGER_LOADINGGAMES);
        
        if (GamesFolder.list().length == 0) {
            this.logger.warning(this.translator.MANAGER_NOGAMESFOUND);
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

    private boolean gameExists(String name) {
        this.logger.info(name);
        for (Game game : games) {
            if (game.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void addGame(File gameName) {
        this.games.add(new Game(gameName));
    }
    public void addGame(String gameName, Grid grid, Player player) throws Exception {
        if (this.gameExists(gameName)) {
            throw new Exception(this.translator.Format(this.translator.MANAGER_NAMEEXISTS, gameName));
        }
        this.games.add(new Game(gameName, grid, player));
    }

    public void unloadGames(boolean saveGames) {
        for (Game game : games) {
            game.unload(saveGames);
        }
        this.games.clear();
    }

    
    public void removeGame(Game game) {
        game.deleteSave();
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
    @EventHandler
    public void itemDrop(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().equals(this.config.PLAYER_AXE)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    private void itemMove(InventoryClickEvent e) {
        if (Objects.equals(e.getCurrentItem(), this.config.PLAYER_AXE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void ticketUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Game game = this.getOnGrid(player);
        if (player.getInventory().getItemInMainHand().isSimilar(this.config.TICKET)
            && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
        ) {
            if (player.hasPermission(this.config.PERM_TICKET_USE)) {
                if (game == null) {
                    player.sendMessage(this.config.PREFIX + this.translator.MANAGER_TICKETUSE_GAMENOTFOUND);
                    e.setCancelled(true);

                } else if (game.hasCooldown(player.getUniqueId())) {
                    game.removeCooldown(player.getUniqueId());
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    e.getPlayer().sendMessage(this.config.PREFIX + this.translator.MANAGER_TICKETUSE_SUCCESS);
                    e.setUseItemInHand(Event.Result.DENY);
                    player.getInventory().removeItem(this.config.TICKET);

                } else {
                    player.sendMessage(this.config.PREFIX + this.translator.MANAGER_TICKETUSE_NOCOOLDOWN);
                    e.setCancelled(true);

                }
            } else {
                player.sendMessage(this.config.PREFIX + this.translator.MANAGER_TICKETUSE_NOPERMISSION);
                e.setCancelled(true);
            }
        }
    }
}
