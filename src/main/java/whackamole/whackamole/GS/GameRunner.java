package whackamole.whackamole.GS;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import whackamole.whackamole.Config;
import whackamole.whackamole.Mole;
import whackamole.whackamole.Utils.Econ;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

public class GameRunner {
    private final Game game;

    public double moleSpeed, interval, spawnChance;
    public Player player;
    public int score = 0, missed = 0, difficultyModifier = 0, molesHit = 0, highestStreak = 0, Streak = 0;

    public Location teleportLocation;

    private ArmorStand streakScoreHolo, streakNameHolo;

    public GameRunner(Game game) {
        this.game = game;
        this.moleSpeed = this.game.settings.moleSpeed;
        this.interval = this.game.settings.spawnTimer;
        this.spawnChance = this.game.settings.spawnChance;
        this.teleportLocation = this.game.settings.teleportLocation;
    }

    public boolean Start(Player player) {

        if (this.game.cooldown.contains(player)
                || !player.hasPermission(Config.Permissions.PERM_PLAY)
                || Config.Game.PLAYER_AXE == null
        ) {
            return false;
        }

        if (!this.game.scoreboard.checkHolo()) {
            Logger.info(String.valueOf(this.game.getScoreboard().holoScores.size()));
            Logger.error(Translator.GAME_INVALIDSCOREBOARD);
            this.game.actionbarParse(player.getUniqueId(), Translator.GAME_ACTIONBAR_ERROR.Format());
            return false;
        }


        if (Econ.currencyType == Econ.Currency.NULL) {
            Logger.error(Translator.GAME_INVALIDECONOMY);
            this.game.cooldown.add(player.getUniqueId(), 10000L);
            this.game.actionbarParse(player.getUniqueId(), Translator.GAME_ACTIONBAR_ERROR.Format());
            return false;
        }


        if (!givePlayerAxe(player)) {
            player.sendMessage(Config.AppConfig.PREFIX + Translator.GAME_START_FULLINVENTORY);
            this.game.actionbarParse(player.getUniqueId(), Translator.GAME_ACTIONBAR_FULLINVENTORY.Format());
            return false;
        }

        this.player = player;
        if (this.game.settings.streakHoloLocation != null) {
            this.createStreakHolo();
        }
        if (this.game.settings.Music != null) {
            try {
                this.player.playSound(player.getLocation(), this.game.settings.Music, 1.0F, 1.0F);
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
        }
        return true;
    }

    public void Stop() {
        if (this.game.settings.Music != null) {
            this.player.stopSound(this.game.settings.Music);
        }

        this.game.grid.removeEntities();
        this.removePlayerAxe(this.player);
        this.removeStreakHolo();

        if (this.score > 0) {
            if (this.Streak > this.highestStreak) { this.highestStreak = this.Streak; }
            this.Streak = 0;
            this.game.scoreboard.add(this.player, this.score, this.molesHit, this.highestStreak);
            this.game.cooldown.add(this.player);
        }
        this.game.setState(Game.gameState.REWARDING);

        if (this.game.settings.toggleScoreboard) {
            this.game.scoreboard.updateTopHolo();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean givePlayerAxe(Player player) {
        PlayerInventory inventory = player.getInventory();
        if (!inventory.getItemInMainHand().equals(Material.AIR)) {
            if (inventory.firstEmpty() != -1) {
                inventory.setItem(inventory.firstEmpty(),
                        inventory.getItemInMainHand());
                inventory.setItemInMainHand(Config.Game.PLAYER_AXE);
                return true;
            }
        }
        return false;
    }

    private void removePlayerAxe(Player player) {
        while (player.getInventory().contains(Config.Game.PLAYER_AXE)) {
            player.getInventory().removeItem(Config.Game.PLAYER_AXE);
        }
    }



    public void moleHit(Mole mole) {
        switch (mole.type) {
            case Mole:
                this.score += this.game.settings.scorePoints;
                this.molesHit ++;
                this.Streak ++;
                this.updateStreakHolo();
                break;
            case Jackpot:
                this.score += this.game.settings.scorePoints * 3;
                this.molesHit ++;
                this.Streak ++;
                this.updateStreakHolo();
                break;
            case Null:
                break;
        }

        this.difficultyModifier++;
        if (this.difficultyModifier >= this.game.settings.difficultyScore)
            this.setSpeedScale();
    }

    public void RemovePlayerFromGame(Player player, Location from, Location to) {
        Location playerLocation = player.getLocation();
        Vector moveVector = from.toVector().subtract(to.toVector()).normalize().multiply(2).setY(1.5);
        while (this.game.grid.onGrid(playerLocation)) {
            if (moveVector.getX() == 0 && moveVector.getZ() == 0) {
                moveVector = this.game.getSettings().spawnRotation.getDirection();
            }
            playerLocation = playerLocation.add(moveVector);
        }
        if (this.teleportLocation == null) {
            player.teleport(playerLocation);
        } else player.teleport(this.teleportLocation);
        player.sendMessage(Config.AppConfig.PREFIX + Translator.MANAGER_ALREADYACTIVE);
    }

    private void setSpeedScale() {
        double Scaler = 1 + (this.game.settings.difficultyScale / 100);
        this.moleSpeed = this.moleSpeed / Scaler;
        this.interval = this.interval / Scaler;
        this.spawnChance = Math.max(0, Math.min(100, this.spawnChance * Scaler));
        this.difficultyModifier = 0;
    }
    public void createStreakHolo() {
        var settings = this.game.getSettings();
        this.streakNameHolo = (ArmorStand) settings.world.spawnEntity(settings.streakHoloLocation, EntityType.ARMOR_STAND);
        this.streakNameHolo.setVisible(true);
        this.streakNameHolo.setCustomNameVisible(true);
        this.streakNameHolo.setGravity(false);
        this.streakNameHolo.setInvisible(true);
        this.streakNameHolo.setMarker(true);
        this.streakNameHolo.isInvulnerable();
        this.streakNameHolo.setCustomName(Translator.GAME_HOLO_HITSTREAK.Format());

        this.streakScoreHolo = (ArmorStand) settings.world.spawnEntity(settings.streakHoloLocation.clone().subtract(0, 0.25, 0), EntityType.ARMOR_STAND);
        this.streakScoreHolo.setVisible(true);
        this.streakScoreHolo.setCustomNameVisible(true);
        this.streakScoreHolo.setGravity(false);
        this.streakScoreHolo.setInvisible(true);
        this.streakScoreHolo.setMarker(true);
        this.streakScoreHolo.isInvulnerable();
        this.streakScoreHolo.setCustomName(Misc.Color("&0"));
    }

    private String[] StreakColors = new String[] {
            "#fb1200&l", "#f82500&l", "#f43800&l", "#f14b00&l", "#ee5e00&l", "#ea7100&l", "#e78400&l", "#e39700&l", "#e0aa00&l", "#ddbd00&l", "#c6c300&l", "#b0ca00&l", "#9ad000&l", "#84d700&l", "#6ede00&l", "#58e400&l", "#42eb00&l", "#2cf100&l", "#16f800&l"
    };

    public void updateStreakHolo() {
        if (this.streakScoreHolo == null) return;
        var settings = this.game.getSettings();
        Location particleLocation = settings.streakHoloLocation.clone().subtract(0, 0.05, 0);

        if(this.Streak == 0)
            this.streakScoreHolo.setCustomName(Misc.Color("#ff0000&l0"));
        else if (this.Streak < 20)
            this.streakScoreHolo.setCustomName(Misc.Color(this.StreakColors[this.Streak - 1] + this.Streak));
        else if (this.Streak < 40)
            this.streakScoreHolo.setCustomName(Misc.Color("#00ff00&l" + this.Streak));
        else
            this.streakScoreHolo.setCustomName(Misc.Color("#00ff00&l" + this.Streak));
        int red   = Math.min(255, Math.max(0, 255 - (this.Streak - 40) * 12));
        int green = Math.min(255, Math.max(0, (this.Streak - 40) * 12));
        settings.world.spawnParticle(Particle.DUST, particleLocation, 1, new Particle.DustOptions(Color.fromRGB(red, green, 0), 2));


    }
    public void removeStreakHolo() {
        if (this.streakScoreHolo != null) { this.streakScoreHolo.remove(); this.streakScoreHolo = null; }
        if (this.streakNameHolo != null)  { this.streakNameHolo.remove();  this.streakNameHolo  = null; }
    }

}
