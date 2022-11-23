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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;


public final class GamesManager implements Listener {

<<<<<<< HEAD
=======
    private Config config = Config.getInstance();

>>>>>>> e262bdb (Game starter:)
    private Logger logger = Logger.getInstance();

    public List<Game> games = new ArrayList<>();

    public GamesManager() {
        this.loadGames();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, Tick, 1l, 1l);
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

    private boolean gameExists(String name) {
        this.logger.info(name);
        for (Game game : games) {
            if (game.name.toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
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
}
