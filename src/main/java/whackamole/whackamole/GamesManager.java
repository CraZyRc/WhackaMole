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
import java.util.*;
>>>>>>> de8c408 (Grid:)

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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

    public List<GameHandler> games = new ArrayList<>();

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
        game.deleteSave();
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
            if (gameHandler.onGrid(event.getPlayer())) {
                if (!gameHandler.hasCooldown(event.getPlayer().getUniqueId())) {
                    gameHandler.Start(event.getPlayer());
                    break;
                }
            } else if (gameHandler.gamePlayer == event.getPlayer()) {
                gameHandler.Stop();
            break;
            }
        }
    }

    int runnableTickCounter = 0;
    Runnable Tick = new Runnable() {
        @Override
        public void run() {
            for (GameHandler gameHandler : games) {
                for (int i = gameHandler.moleList.size() -1; i >= 0 ; i--) {
                    Mole mole = gameHandler.moleList.get(i);
                    if (!(mole.Update())) {
                        gameHandler.moleList.remove(i);
                    }
                }
            }
            if (runnableTickCounter >= 20) {
                runnableTickCounter = 0;
                for (GameHandler gameHandler : games) {
                    for (Map.Entry<UUID, Long> cooldownPlayer : gameHandler.cooldown.entrySet()) {
                        if (!gameHandler.hasCooldown(cooldownPlayer.getKey())) {
                            gameHandler.removeCooldown(cooldownPlayer.getKey());
                        }else if (gameHandler.cooldownSendList.contains(cooldownPlayer.getKey()) && gameHandler.gameLost) {
                            BaseComponent[] resetMessage = new ComponentBuilder().append(Config.color("&l" + gameHandler.moleMissed + " moles missed: &4&lGAME OVER&f&l, please buy a new ticket or wait. Time left: &a&l") + gameHandler.formatCooldown(cooldownPlayer.getValue())).create();
                            Bukkit.getPlayer(cooldownPlayer.getKey()).spigot().sendMessage(ChatMessageType.ACTION_BAR, resetMessage);
                        } else if (gameHandler.cooldownSendList.contains(cooldownPlayer.getKey()) && !gameHandler.gameLost) {
                            BaseComponent[] resetMessage = new ComponentBuilder().append(Config.color("&4&lGAME OVER !&f&l please buy a new ticket or wait. Time left: &a&l") + gameHandler.formatCooldown(cooldownPlayer.getValue())).create();
                            Bukkit.getPlayer(cooldownPlayer.getKey()).spigot().sendMessage(ChatMessageType.ACTION_BAR, resetMessage);
                        }
                    }
                }
            }
            runnableTickCounter++;

        }
    };



    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.PLAYER) {
            gameloop:
            for (GameHandler game : this.games) {
                if (e.getDamager() == game.gamePlayer && game.gamePlayer.getInventory().getItemInMainHand().equals(GameHandler.axe)) {
                    for (Mole mole : game.moleList) {
                        if (e.getEntity() == mole.mole) {
                            mole.hit = true;
                            game.gamePlayer.playSound(game.gamePlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                            game.Score = game.Score + game.pointsPerKill;
                            break gameloop;
                        }
                    }

                }
            }
        }
    }


    public void toggleArmorStands() {
        for (GameHandler gameHandler : games) {
            gameHandler.toggleArmorStands();
        }
    }
}
