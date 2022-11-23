package whackamole.whackamole;

<<<<<<< HEAD
=======
import java.io.File;
import java.security.spec.ECField;
>>>>>>> 1e62199 (change :))
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class GamesManager implements Listener {

    private Logger logger = Logger.getInstance();
    public List<GameHandler> games = new ArrayList<>();

    public GamesManager() {
        this.loadGames();
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
                this.logger.success("..." + i + " Successfully loaded!");
            } catch (Exception e) {
                this.logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void addGame(GameHandler game) throws Exception {
        if (this.nameExists(game.gameName)) {
            throw new Exception("Grid with name %s already exists!".formatted(game.gameName));
        }
        this.games.add(game);
    }

<<<<<<< HEAD
<<<<<<< HEAD
=======
    public void addGame(File gameName) {
=======
    public void addGame(File gameName) throws Exception {
>>>>>>> cffe322 (change :))
        this.games.add(new GameHandler(gameName));
    }
>>>>>>> 461f3dd (updated: addGame method)
    public void addGame(String gameName, Grid grid) throws Exception {
        if (this.nameExists(gameName)) {
            throw new Exception("Grid with name %s already exists!".formatted(ChatColor.YELLOW + gameName + ChatColor.WHITE));
        }
        this.games.add(new GameHandler(gameName, grid));
    }

    public void removeGame(GameHandler game) {
        this.games.remove(game);

        this.logger.info(game + " ...Successfully removed!");
    }

    public boolean nameExists(String name) {
        this.logger.info(name);
        for (GameHandler gameHandler : games) {
            this.logger.info(gameHandler.gameName);
            if (gameHandler.gameName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public GameHandler getOnGrid(Player player) {
        for (GameHandler gameHandler : games) {
            if (gameHandler.onGrid(player)) {
                return gameHandler;
            }
        }
        return null;
    }



    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event) {
        for (GameHandler gameHandler : games) {
            if (gameHandler.grid.onGrid(event.getTo())) {
//                this.logger.info("On Grid: " + gameHandler.gameName);
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
    }

    public void toggleArmorStands() {
        for (GameHandler gameHandler : games) {
            gameHandler.toggleArmorStands();
        }
    }
}
