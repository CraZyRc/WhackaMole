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

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
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

    public void addGame(Game game) throws Exception {
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
<<<<<<< HEAD
>>>>>>> cffe322 (change :))
        this.games.add(new GameHandler(gameName));
=======
        this.games.add(new Game(gameName));
>>>>>>> 2b9819f (Econ :)
    }
>>>>>>> 461f3dd (updated: addGame method)
    public void addGame(String gameName, Grid grid) throws Exception {
        if (this.nameExists(gameName)) {
            throw new Exception("Grid with name %s already exists!".formatted(ChatColor.YELLOW + gameName + ChatColor.WHITE));
        }
        this.games.add(new Game(gameName, grid));
    }

    public void removeGame(Game game) {
        this.games.remove(game);
        game.deleteSave();
        this.logger.info(game + " ...Successfully removed!");
    }

    public boolean nameExists(String name) {
        this.logger.info(name);
        for (Game game : games) {
            this.logger.info(game.gameName);
            if (game.gameName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Game getOnGrid(Player player) {
        for (Game game : games) {
            if (game.onGrid(player)) {
                return game;
            }
        }
        return null;
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

    int runnableTickCounter = 0;
    Runnable Tick = new Runnable() {
        @Override
        public void run() {
            for (Game game : games) {
                for (int i = game.moleList.size() -1; i >= 0 ; i--) {
                    Mole mole = game.moleList.get(i);
                    if (!(mole.Update())) {
                        game.moleList.remove(i);
                    }
                }
            }
            if (runnableTickCounter >= 20) {
                runnableTickCounter = 0;
                for (Game game : games) {
                    if (game.Running) {
                        BaseComponent[] actionMessage = new ComponentBuilder().append(ComponentSerializer.parse(config.ACTIONTEXT)).append((Config.color("&2&l ") + game.Score)).create();
                        game.gamePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionMessage);
                    }
                    for (Map.Entry<UUID, Long> cooldownPlayer : game.cooldown.entrySet()) {
                        if (!game.hasCooldown(cooldownPlayer.getKey())) {
                            game.removeCooldown(cooldownPlayer.getKey());
                        }else if (game.cooldownSendList.contains(cooldownPlayer.getKey()) && game.gameLost) {
                            BaseComponent[] resetMessage = new ComponentBuilder().append(Config.color("&l" + game.moleMissed + " moles missed: &4&lGAME OVER&f&l, please buy a new ticket or wait. Time left: &a&l") + game.formatCooldown(cooldownPlayer.getValue())).create();
                            Bukkit.getPlayer(cooldownPlayer.getKey()).spigot().sendMessage(ChatMessageType.ACTION_BAR, resetMessage);
                        } else if (game.cooldownSendList.contains(cooldownPlayer.getKey()) && !game.gameLost) {
                            BaseComponent[] resetMessage = new ComponentBuilder().append(Config.color("&4&lGAME OVER !&f&l please buy a new ticket or wait. Time left: &a&l") + game.formatCooldown(cooldownPlayer.getValue())).create();
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
            for (Game game : this.games) {
                if (e.getDamager() == game.gamePlayer && game.gamePlayer.getInventory().getItemInMainHand().equals(Game.axe)) {
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
        for (Game game : games) {
            game.toggleArmorStands();
        }
    }
}
