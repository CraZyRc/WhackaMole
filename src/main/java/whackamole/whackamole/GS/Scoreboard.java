package whackamole.whackamole.GS;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import whackamole.whackamole.DB.Model.Row;
import whackamole.whackamole.DB.SQLite;
import whackamole.whackamole.DB.ScoreboardDB;
import whackamole.whackamole.DB.ScoreboardRow;
import whackamole.whackamole.Utils.Logger;
import whackamole.whackamole.Utils.Translator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Scoreboard {
    private Game game;

    List<ArmorStand> holoScores = new ArrayList<>();
    ArmorStand highScore, Score, Streak, molesHit;

    public Scoreboard(Game game) {
        this.game = game;
    }

    public class Score extends ScoreboardRow {
        public OfflinePlayer player;
        private void onLoad() {
            this.player = Bukkit.getOfflinePlayer(playerID);
        }
    }

    public List<Score> scores = new ArrayList<>();
    public ScoreboardDB db = SQLite.Scoreboard;

    public void add(Player player, int score, int molesHit, int scoreStreak) {
        var scoreItem = new Score();
        scoreItem.player = player;
        scoreItem.Score = score;
        scoreItem.molesHit = molesHit;
        scoreItem.scoreStreak = scoreStreak;
        scoreItem.Datetime = LocalDateTime.now();
        scoreItem.gameID = this.game.getID();
        scoreItem.playerID = player.getUniqueId();
        this.scores.add(scoreItem);
        this.db.Insert(scoreItem);
    }

    public void onLoad() {
        this.scores.clear();
        var dbScores = db.Select(this.game.getID());
        for(var i : dbScores) {
            Score score = new Score();
            Row.upCast(i, score);
            score.onLoad();
            this.scores.add(score);
        }
    }

    public void Delete() {
        this.killTopHolo();
        for (var row : scores) {
            db.Delete(row);
        }
    }

    public Score[] getTop(int scoreType) {
        return getTop(10, scoreType);
    }

    public Score[] getTop(int count, int scoreType) {
        Score[] template = new Score[]{};
        if (scores.isEmpty()) {
            return this.scores.toArray(template);
        }
        count = Math.min(this.scores.size(), count);
        if (scoreType == 0) {
            this.scores.sort((a, b) -> b.Score - a.Score);
        } else if (scoreType == 1) {
            this.scores.sort((a, b) -> b.scoreStreak - a.scoreStreak);
        } else if (scoreType == 2) {
            this.scores.sort((a, b) -> b.molesHit - a.molesHit);
        } else { Logger.error("getTop scoreType = " + scoreType + " unknown, please report this bug."); }

        return this.scores.subList(0, count).toArray(template);
    }

    public boolean checkHolo() {
        if (!this.holoScores.isEmpty()) {
            this.holoScores.clear();
        }
        for (Entity a : Objects.requireNonNull(this.game.settings.scoreLocation.getWorld()).getNearbyEntities(this.game.settings.scoreLocation, 1,2,1)) {
            if (a.getType() == EntityType.ARMOR_STAND) {
                if (a.getScoreboardTags().contains("highScore")) {
                    this.highScore  = (ArmorStand) a;
                } else if (a.getScoreboardTags().contains("Score")) {
                    this.Score  = (ArmorStand) a;
                } else if (a.getScoreboardTags().contains("Streak")) {
                    this.Streak  = (ArmorStand) a;
                } else if (a.getScoreboardTags().contains("molesHit")) {
                    this.molesHit  = (ArmorStand) a;
                }
            }
        }

        if (this.holoScores.isEmpty()) {
            this.holoScores.add(this.highScore);
            this.holoScores.add(this.Score);
            this.holoScores.add(this.Streak);
            this.holoScores.add(this.molesHit);
        }
        return this.holoScores.size() == 4;
    }

    public ArmorStand addArmorStandSettings(ArmorStand armorStand) {
        armorStand.setVisible(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.isInvulnerable();
        return armorStand;
    }

    public void createTopHolo() {
        var settings = this.game.getSettings();
        Location spawnloc = settings.scoreLocation.clone().add(0,1.75,0);
        this.highScore  = (ArmorStand) settings.world.spawnEntity(spawnloc, EntityType.ARMOR_STAND);
        this.Score      = (ArmorStand) settings.world.spawnEntity(spawnloc.subtract(0,0.25,0), EntityType.ARMOR_STAND);
        this.Streak     = (ArmorStand) settings.world.spawnEntity(spawnloc.subtract(0,0.25,0), EntityType.ARMOR_STAND);
        this.molesHit   = (ArmorStand) settings.world.spawnEntity(spawnloc.subtract(0,0.25,0), EntityType.ARMOR_STAND);
        this.holoScores.add(this.addArmorStandSettings(this.highScore));
        this.holoScores.add(this.addArmorStandSettings(this.Score));
        this.holoScores.add(this.addArmorStandSettings(this.Streak));
        this.holoScores.add(this.addArmorStandSettings(this.molesHit));

        this.highScore.addScoreboardTag("highScore");
        this.Score.addScoreboardTag("Score");
        this.Streak.addScoreboardTag("Streak");
        this.molesHit.addScoreboardTag("molesHit");

        var ScoreSTR = getTop(1, 0);
        var StreakSTR = getTop(1, 1);
        var molesHitSTR = getTop(1, 2);

        this.highScore.setCustomName(Translator.GAME_HOLO_HIGHSCORES.Format());
        if (ScoreSTR.length > 0) this.Score.setCustomName(Translator.GAME_HOLO_SCORE1.Format(ScoreSTR[0].player.getName(), String.valueOf(ScoreSTR[0].Score)));
        else this.Score.setCustomName(Translator.GAME_HOLO_SCORE2.Format());
        if (StreakSTR.length > 0) this.Streak.setCustomName(Translator.GAME_HOLO_STREAK1.Format(StreakSTR[0].player.getName(), String.valueOf(StreakSTR[0].scoreStreak)));
        else this.Streak.setCustomName(Translator.GAME_HOLO_STREAK2.Format());
        if (molesHitSTR.length > 0) this.molesHit.setCustomName(Translator.GAME_HOLO_MOLESHIT1.Format(molesHitSTR[0].player.getName(), String.valueOf(molesHitSTR[0].molesHit)));
        else this.molesHit.setCustomName(Translator.GAME_HOLO_MOLESHIT2.Format());

    }
    public void updateTopHolo() {
        var Score = getTop(1, 0);
        var Streak = getTop(1, 1);
        var molesHit = getTop(1, 2);
        if (Score.length > 0) this.Score.setCustomName(Translator.GAME_HOLO_SCORE1.Format(Score[0].player.getName(), String.valueOf(Score[0].Score)));
        if (Streak.length > 0) this.Streak.setCustomName(Translator.GAME_HOLO_STREAK1.Format(Streak[0].player.getName(), String.valueOf(Streak[0].scoreStreak)));
        if (molesHit.length > 0) this.molesHit.setCustomName(Translator.GAME_HOLO_MOLESHIT1.Format(molesHit[0].player.getName(), String.valueOf(molesHit[0].molesHit)));
    }

    public void tpTopHolo(Location loc) {
        highScore.teleport(loc.add(0,0.5,0));
        Score.teleport(loc.subtract(0,0.25,0));
        Streak.teleport(loc.subtract(0,0.25,0));
        molesHit.teleport(loc.subtract(0,0.25,0));
    }
    public void killTopHolo() {
        if (this.checkHolo()) {
            for (ArmorStand armorStand : this.holoScores) {
                armorStand.remove();
            }
        }
    }
}
