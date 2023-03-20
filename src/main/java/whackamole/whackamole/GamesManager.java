package whackamole.whackamole;

import java.io.File;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import whackamole.whackamole.DB.GameRow;
import whackamole.whackamole.DB.SQLite;

public final class GamesManager implements Listener {

    private static GamesManager Instance;
    public List<Game> games = new ArrayList<>();

    private GamesManager() {}

    public static GamesManager getInstance() {
        if (GamesManager.Instance == null) {
            GamesManager.Instance = new GamesManager();
        }
        return GamesManager.Instance;
    }

    private int tickId = -1;
    public void onLoad(Plugin main) {
        this.loadGames();
        this.tickId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, Tick, 1L, 1L);
    }

    public void onUnload() {
        this.unloadGames();
        Bukkit.getScheduler().cancelTask(this.tickId);
    }

    public void loadGames() {
        File GamesFolder = new File(Config.AppConfig.storageFolder + "/Games");

        Logger.info(Translator.MANAGER_LOADINGGAMES);

        if (GamesFolder.list() == null) {
            Logger.warning(Translator.MANAGER_NOGAMESFOUND);
            return;
        }

        for (File i : GamesFolder.listFiles()) {
            try {
                this.addGame(new YMLFile(i));
            } catch (Exception e) {
                Logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean gameExists(String name) {
        for (Game game : games) {
            if (game.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void addGame(YMLFile gameName) {
        this.games.add(new Game(gameName));
    }

    public void addGame(String gameName, Grid grid, Player player) throws Exception {
        if (this.gameExists(gameName)) {
            throw new Exception(Translator.Format(Translator.MANAGER_NAMEEXISTS, gameName));
        }
        this.games.add(new Game(gameName, grid, player));
    }

    public void unloadGames() {
        for (Game game : games) {
            game.unload();
        }
        this.games.clear();
    }

    public void deleteGame(Game game) {
        game.delete();
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

    private int runnableTickCounter = 0;
    Runnable Tick = () -> {
        for (Game game : GamesManager.this.games) {
            game.run();
            game.moleUpdater();
        }

        if (GamesManager.this.runnableTickCounter >= 20) {
            GamesManager.this.runnableTickCounter = 0;
            for (Game game : GamesManager.this.games) {
                game.updateActionBar();
            }
        }
        GamesManager.this.runnableTickCounter++;
    };
    public void loadFromDatabase() {
        loadFromDatabase("");
    }
    public boolean loadFromDatabase(String worldName) {
        List<GameRow> gameList;
        if (worldName.isEmpty()) {
            gameList = SQLite.getGameDB().Select();

        } else {
            gameList = SQLite.getGameDB().Select(worldName);
        }
        if (gameList.size() == 0) {
            return false;
        }
        for (var game : gameList) {
            this.games.add(new Game(game));
        }
        return true;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        Logger.info(Translator.MANAGER_LOADINGGAMES.Format(e.getWorld().getName()));
        if (!this.loadFromDatabase(e.getWorld().getName())) {
            Logger.warning(Translator.MANAGER_NOGAMESFOUND);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        for (int i = this.games.size() -1; i >= 0; i--) {
            if (this.games.get(i).getSettings().world.equals(e.getWorld())) {
                this.games.get(i).unload();
                this.games.remove(i);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        for (Game game : games) {
            game.onPlayerExit(player);
        }
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        for (Game game : games) {
            var gameRunner = game.getRunning();
            if (game.onGrid(player)) {
                if (gameRunner == null) continue;
                if (gameRunner.player != e.getPlayer()) {
                    gameRunner.RemovePlayerFromGame(e);
                }
                break;
            } else if (gameRunner != null && gameRunner.player == e.getPlayer()) {
                game.Stop();
                break;
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.PLAYER) {
            for (Game game : this.games) {
                if (game.handleHitEvent(e)) {
                    break;
                }
            }
        }
    }
    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        for (Game game : this.games) {
            if (game.inGrid(e.getBlock())) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void itemDrop(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().equals(Config.Game.PLAYER_AXE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void itemMove(InventoryClickEvent e) {
        if (Objects.equals(e.getCurrentItem(), Config.Game.PLAYER_AXE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void ticketUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (!player.getInventory().getItemInMainHand().isSimilar(Config.Game.TICKET))
            return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!player.hasPermission(Config.Permissions.PERM_TICKET_USE)) {
            player.sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_TICKETUSE_NOPERMISSION);
            e.setCancelled(true);
            return;
        }

        Game game = this.getOnGrid(player);
        if (game == null) {
            player.sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_TICKETUSE_GAMENOTFOUND);
            e.setCancelled(true);
            return;
        }

        game.useTicket(e);
    }
}
