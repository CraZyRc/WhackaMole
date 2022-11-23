package whackamole.whackamole;

import java.util.ArrayList;
import java.util.List;

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
        for (File i : GamesFolder.listFiles()) {
            try {
                this.addGame(i);
                this.logger.success("..." + i + " Successfully loaded!");
            } catch (Exception e) {
                this.logger.error(e.getStackTrace().toString());
            }
        }
    }

    public void addGame(GameHandler game) throws Exception {
        if (this.nameExists(game.gameName)) {
            throw new Exception("Grid with name {0} already exists!".formatted(game.gameName));
        }
        this.games.add(game);
    }

    public void addGame(String gameName, Grid grid) throws Exception {
        if (this.nameExists(gameName)) {
            throw new Exception("Grid with name {0} already exists!".formatted(gameName));
        }
        this.games.add(new GameHandler(gameName, grid));
    }

    public boolean nameExists(String name) {
        this.logger.info(name);
        for (GameHandler gameHandler : games) {
            this.logger.info(gameHandler.gameName);
            if (gameHandler.gameName == name) {
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
                this.logger.info("On Grid: " + gameHandler.gameName);
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
