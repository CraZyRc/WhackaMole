package whackamole.whackamole.GS;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import whackamole.whackamole.Config;
import whackamole.whackamole.DB.GameRow;
import whackamole.whackamole.DB.SQLite;
import whackamole.whackamole.Grid;
import whackamole.whackamole.RS.RewardsManager;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

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
        this.GameLoading(null);
        this.tickId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, Tick, 1L, 1L);
    }

    public void onUnload() {
        this.unloadGames();
        Bukkit.getScheduler().cancelTask(this.tickId);
    }


    public boolean GameLoading(@Nullable World world) {
        List<GameRow> DBGameList;
        if(world == null) { DBGameList = SQLite.getGameDB().Select(); }
        else {              DBGameList = SQLite.getGameDB().Select(world); }
        
        for(var game : DBGameList) {
            if(!Config.Game.ENABLED_WOLRDS.isEmpty() && ! Config.Game.ENABLED_WOLRDS.contains(game.worldName)) {
                Logger.warning(Translator.MANAGER_WORLDNOTENABLED.Format(game.Name, game.worldName));
                continue;
            }
            this.games.add(new Game(game));
        }

        return !DBGameList.isEmpty();
    }

    private boolean gameExists(String name) {
        for (Game game : games) {
            if (game.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void addGame(String gameName, Grid grid, Player player) throws Exception {
        if (this.gameExists(gameName)) {
            throw new Exception(Translator.Format(Translator.MANAGER_NAMEEXISTS, gameName));
        }
        this.games.add(new Game(gameName, grid, player));
    }

    public void unloadGames() {
        for (Game game : games) {
            game.Unload();
        }
        this.games.clear();
    }

    public void deleteGame(Game game) {
        game.Delete();
        this.games.remove(game);
    }

    public Optional<Game> getOnGrid(Player player) {
        for (Game game : games) {
            if (game.onGrid(player)) {
                return Optional.of(game);
            }
        }
        return Optional.empty();
    }

    private int runnableTickCounter = 0;
    Runnable Tick = () -> {
        for (Game game : GamesManager.this.games) {
            game.run();
            game.moleUpdater();
        }
        RewardsManager.Tick();

        if (GamesManager.this.runnableTickCounter >= 20) {
            GamesManager.this.runnableTickCounter = 0;
            for (Game game : GamesManager.this.games) {
                game.updateActionBar();
            }
        }
        GamesManager.this.runnableTickCounter++;
    };

    @EventHandler
    public void entitySpawnEvent(EntitySpawnEvent e) {
        Entity entity = e.getEntity();
        if (entity.getScoreboardTags().contains("Mole_new")) {
            entity.getScoreboardTags().remove("Mole_new");
            entity.addScoreboardTag("Mole");
        } else if (entity.getScoreboardTags().contains("Mole")){
            entity.remove();
        }
    }
    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (!Config.Game.ENABLED_WOLRDS.isEmpty() && ! Config.Game.ENABLED_WOLRDS.contains(e.getWorld())) {
            return;
        }
        Logger.info(Translator.MANAGER_LOADINGGAMES.Format(e.getWorld().getName()));
        if (!this.GameLoading(e.getWorld())) {
            Logger.warning(Translator.MANAGER_NOGAMESFOUND);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        for (int i = this.games.size() -1; i >= 0; i--) {
            if (this.games.get(i).getSettings().world.equals(e.getWorld())) {
                this.games.get(i).Unload();
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
            var gameRunner = game.getRunning().orElse(null);
            if (game.onGrid(player)) {
                if (!game.hasActionbar.contains(player)) {
                    game.updateActionBar();
                    game.hasActionbar.add(player);
                }
                if (game.State != Game.gameState.RUNNING) continue;
                if (gameRunner.player != player) {
                    gameRunner.RemovePlayerFromGame(e.getPlayer(), e.getFrom(), Objects.requireNonNull(e.getTo()));
                }
                break;
            } else {
                if (game.hasActionbar.contains(player)) game.hasActionbar.remove(player);
                if (gameRunner != null && gameRunner.player == player && game.State == Game.gameState.RUNNING) {
                    game.Stop();
                    break;
                }
            }
        }
    }

    @EventHandler
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        for (Game game : games) {
            var gameRunner = game.getRunning().orElse(null);
            if (game.onGrid(player)) {
                if (gameRunner == null) continue;
                if (gameRunner.player != player) {
                    player.teleport(game.getSettings().teleportLocation);
                }
                break;
            } else if (gameRunner != null && gameRunner.player == player) {
                game.Stop();
                break;
            }
        }
    }

    @EventHandler
    public void PlayerTeleportEvent(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        for (Game game : games) {
            var gameRunner = game.getRunning().orElse(null);
            if (game.onGrid(player, e.getTo())) {
                if (gameRunner == null) continue;
                if (gameRunner.player != player) {
                    gameRunner.RemovePlayerFromGame(e.getPlayer(), e.getFrom(), Objects.requireNonNull(e.getTo()));
                }
                break;
            } else if (gameRunner != null && gameRunner.player == player && game.State == Game.gameState.RUNNING) {
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
            if (game.onGrid(e.getBlock().getLocation().add(0,1,0)) || game.getRunning().map(GameRunner::getPlayer).orElse(null) == e.getPlayer()) {
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
    public void RewardInteraction(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        for (Game game : this.games) {
            var gameRunner = game.getRunning().orElse(null);
            if (e.getRightClicked().getType().equals(EntityType.INTERACTION) && gameRunner.player.equals(player)) {
                RewardsManager.onInteractEvent(player, e.getRightClicked());
            }
        }
    }

    @EventHandler
    public void ticketUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.getInventory().getItemInMainHand().hasItemMeta()) {
            if (!player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Bukkit.getPluginManager().getPlugin("WhackaMole"), "Reset-Ticket"), PersistentDataType.DOUBLE))
                return;
        } else return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!player.hasPermission(Config.Permissions.PERM_TICKET_USE)) {
            player.sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_TICKETUSE_NOPERMISSION);
            e.setCancelled(true);
            return;
        }

        this.getOnGrid(player).ifPresentOrElse((game) -> {
            game.useTicket(e);
        }, () -> {
            player.sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_TICKETUSE_GAMENOTFOUND);
            e.setCancelled(true);
        });
    }
}
