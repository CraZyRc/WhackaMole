package whackamole.whackamole.GS;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import whackamole.whackamole.DB.HologramDB;
import whackamole.whackamole.DB.HologramRow;
import whackamole.whackamole.DB.SQLite;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

import java.util.ArrayList;
import java.util.List;

public class Hologram extends HologramRow {
  public HologramDB db = SQLite.Hologram;
  private List<ArmorStand> armorstandList = new ArrayList<>();
  private Game game;
  public int holoID;
  public boolean showID;

  public Hologram(Game game, Location loc, String type, int topCount, int holoID) {
    double height = 1.75 + (0.25 * topCount);
    this.holoID = holoID;
    this.gameID = game.getID();
    this.Type = type;
    this.Location = loc.add(0, height, 0);
    this.topCount = topCount;
    this.showID = false;
    this.game = game;
  }

  public Hologram(HologramRow row, Game game) {
    this.holoID = row.holoID;
    this.gameID = row.gameID;
    this.Type = row.Type;
    this.Location = row.Location;
    this.topCount = row.topCount;
    this.showID = row.showID;
    this.game = game;


  }

  public void Create() {
    this.db.Insert(this);

    this.summonHolos(this);
  }

  public void Delete() {
    this.db.Delete(this);
    this.killHolos();
  }

  private void summonHolos(HologramRow hologram) {
    Location loc = this.Location.clone();
    // Adding top hologram
    ArmorStand armorstandMain = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
    armorstandMain = Misc.addArmorStandSettings(armorstandMain, "Top:1");
    armorstandMain = this.nameHolos(armorstandMain, ChatColor.YELLOW, ChatColor.GOLD);
    this.armorstandList.add(armorstandMain);

    // Adding second line hologram
    ArmorStand armorstandType = (ArmorStand) loc.getWorld().spawnEntity(loc.subtract(0, 0.25, 0), EntityType.ARMOR_STAND);
    armorstandType = Misc.addArmorStandSettings(armorstandType, hologram.Type);
    armorstandType.addScoreboardTag("Top:2");
    armorstandType = this.nameHolos(armorstandType, hologram.Type, ChatColor.YELLOW, ChatColor.GOLD);
    this.armorstandList.add(armorstandType);

    // Adding ranks hologram
    for (int i = 0; i < hologram.topCount; i++) {
      ArmorStand a = (ArmorStand) loc.getWorld().spawnEntity(loc.subtract(0, 0.25, 0), EntityType.ARMOR_STAND);
      a = Misc.addArmorStandSettings(a, hologram.Type);
      a.addScoreboardTag("Bottom");
      a = this.nameHolos(a, i, hologram.Type, ChatColor.DARK_AQUA, ChatColor.WHITE, ChatColor.AQUA, ChatColor.YELLOW);

      this.armorstandList.add(a);
    }

  }

  private ArmorStand nameHolos(ArmorStand a, ChatColor color1, ChatColor color2) {
    if (a.getScoreboardTags().contains("Top:1")) {
      a.setCustomName(color1 + "" + ChatColor.BOLD + "[-> " + color2 + ChatColor.BOLD + Translator.GAME_HOLO_HIGHSCORES.Format() + color1 + ChatColor.BOLD + " <-]");
      return a;
    }
    return null;
  }

  private ArmorStand nameHolos(ArmorStand a, String type, ChatColor color1, ChatColor color2) {
    if (a.getScoreboardTags().contains("Top:2")) {
      String text = "";
      if (type.equals("Score")) {
        text = Translator.HOLOGRAM_ARMORSTANDTYPE_SCORE.Format();
      } else if (type.equals("Streak")) {
        text = Translator.HOLOGRAM_ARMORSTANDTYPE_STREAK.Format();
      } else if (type.equals("molesHit")) {
        text = Translator.HOLOGRAM_ARMORSTANDTYPE_MOLESHIT.Format();
      }

      a.setCustomName(color1 + "" + ChatColor.BOLD + "[- " + color2 + ChatColor.BOLD + " " + text + color1 + ChatColor.BOLD + " -]");
      return a;
    }
    return null;
  }

  private ArmorStand nameHolos(ArmorStand a, int i, String type, ChatColor color1, ChatColor color2, ChatColor color3, ChatColor color4) {
    switch (type) {
      case "Score": {
        var score = this.game.getScoreboard().getTop(0);
        if (i < score.length) {
          a.setCustomName(color1 + "" + (i + 1) + ". " + color2 + score[i].player.getName() + " : " + color3 + score[i].Score + color2 + ", " + color4 + score[i].Datetime.toLocalDate().toString());
          break;
        }
      }
      case "Streak": {
        var score = this.game.getScoreboard().getTop(1);
        if (i < score.length) {
          a.setCustomName(color1 + "" + (i + 1) + ". " + color2 + score[i].player.getName() + " : " + color3 + score[i].scoreStreak + color2 + ", " + color4 + score[i].Datetime.toLocalDate().toString());
          break;
        }
      }
      case "molesHit": {
        var score = this.game.getScoreboard().getTop(2);
        if (i < score.length) {
          a.setCustomName(color1 + "" + (i + 1) + ". " + color2 + score[i].player.getName() + " : " + color3 + score[i].molesHit + color2 + ", " + color4 + score[i].Datetime.toLocalDate().toString());
          break;
        }
      }
      default: {
        a.setCustomName(color1 + "" + (i + 1) + ". " + color4 + "_________________________");
      }
    }
    return a;
  }

  public void glowHolos(int i) {
    int n = 0;
    for (var h : this.armorstandList) {
      var tags = h.getScoreboardTags();

      // Even numbers
      if (i % 2 == 0) {
        // Upper color changer
        if (tags.contains("Top:1")) this.nameHolos(h, ChatColor.YELLOW, ChatColor.GOLD);

          // Second layer color changer
        else if (tags.contains("Top:2")) {
          if (tags.contains("Score")) this.nameHolos(h, "Score", ChatColor.YELLOW, ChatColor.GOLD);
          if (tags.contains("Streak")) this.nameHolos(h, "Streak", ChatColor.YELLOW, ChatColor.GOLD);
          if (tags.contains("molesHit")) this.nameHolos(h, "molesHit", ChatColor.YELLOW, ChatColor.GOLD);
        }

        // Ranklist color changer
        else if (tags.contains("Bottom")) {
          if (tags.contains("Score"))
            this.nameHolos(h, n, "Score", ChatColor.DARK_AQUA, ChatColor.WHITE, ChatColor.AQUA, ChatColor.YELLOW);
          if (tags.contains("Streak"))
            this.nameHolos(h, n, "Streak", ChatColor.DARK_AQUA, ChatColor.WHITE, ChatColor.AQUA, ChatColor.YELLOW);
          if (tags.contains("molesHit"))
            this.nameHolos(h, n, "molesHit", ChatColor.DARK_AQUA, ChatColor.WHITE, ChatColor.AQUA, ChatColor.YELLOW);

          n++;
        }
      }

      // Uneven numbers
      else {
        // Upper color changer
        if (tags.contains("Top:1")) this.nameHolos(h, ChatColor.GOLD, ChatColor.YELLOW);

          // Second layer color changer
        else if (tags.contains("Top:2")) {
          if (tags.contains("Score")) this.nameHolos(h, "Score", ChatColor.GOLD, ChatColor.YELLOW);
          if (tags.contains("Streak")) this.nameHolos(h, "Streak", ChatColor.GOLD, ChatColor.YELLOW);
          if (tags.contains("molesHit")) this.nameHolos(h, "molesHit", ChatColor.GOLD, ChatColor.YELLOW);
        }

        // Ranklist color changer
        else if (tags.contains("Bottom")) {
          if (tags.contains("Score"))
            this.nameHolos(h, n, "Score", ChatColor.AQUA, ChatColor.YELLOW, ChatColor.DARK_AQUA, ChatColor.WHITE);
          if (tags.contains("Streak"))
            this.nameHolos(h, n, "Streak", ChatColor.AQUA, ChatColor.YELLOW, ChatColor.DARK_AQUA, ChatColor.WHITE);
          if (tags.contains("molesHit"))
            this.nameHolos(h, n, "molesHit", ChatColor.AQUA, ChatColor.YELLOW, ChatColor.DARK_AQUA, ChatColor.WHITE);

          n++;
        }
      }
    }
  }

  public void toggleHoloID() {
    ArmorStand H = null;
    for (var h : this.armorstandList) {
      if (this.showID) {
        if (h.getScoreboardTags().contains("Top:1")) {
          ArmorStand armorstandID = (ArmorStand) h.getLocation().getWorld().spawnEntity(h.getLocation().add(0, 0.25, 0), EntityType.ARMOR_STAND);
          armorstandID = Misc.addArmorStandSettings(armorstandID, "showID");
          armorstandID.setCustomName(Misc.Color("&b&l[-> &3&l" + this.holoID + "&b&l <-]"));
          H = armorstandID;

        }
      } else {
        if (h.getScoreboardTags().contains("showID")) {
          h.remove();
        }
      }
    }

    if (H != null) this.armorstandList.add(H);
  }

  public void updateHolos() {
    int n = 0;
    for (var h : this.armorstandList) {
      var tags = h.getScoreboardTags();

      if (tags.contains("Bottom")) {
        if (tags.contains("Score"))
          this.nameHolos(h, n, "Score", ChatColor.DARK_AQUA, ChatColor.WHITE, ChatColor.AQUA, ChatColor.YELLOW);
        if (tags.contains("Streak"))
          this.nameHolos(h, n, "Streak", ChatColor.DARK_AQUA, ChatColor.WHITE, ChatColor.AQUA, ChatColor.YELLOW);
        if (tags.contains("molesHit"))
          this.nameHolos(h, n, "molesHit", ChatColor.DARK_AQUA, ChatColor.WHITE, ChatColor.AQUA, ChatColor.YELLOW);

        n++;
      }
    }
  }

  public void Update() {
    this.db.update(this);
  }

  private void killHolos() {
    for (var e : this.armorstandList) {
      e.remove();
    }
  }

  public int getID() {
    return this.holoID;
  }


  public void Teleport(Player player) {
    double height = 1.75 + (0.25 * this.topCount);
    Location loc = player.getLocation().add(0, height, 0);
    Location showID = loc.clone().add(0, 0.25, 0);
    this.Location = loc;
    this.Update();

    for (var H : this.armorstandList) {
      if (H.getScoreboardTags().contains("Top:1")) {
        H.teleport(loc);
      } else if (H.getScoreboardTags().contains("showID")) {
        H.teleport(showID);
      } else {
        H.teleport(loc.subtract(0, 0.25, 0));
      }
    }

  }

  /**
   * Loads all the holos in order when executing a command
   * Adds the holos to the list in order from first (most upper) to last (lowest) hologram.
   * This is to keep the list ordered and not random
   * @return true/false
   */
  // * This gets run everytime a holo command is executed. this because loading the holos during worldload gave errors (due to the armorstands not being loaded)
  public boolean loadHolos() {
    if (!this.armorstandList.isEmpty()) {
      return true;
    }

    Location loc;
    if (this.showID) {
      loc = this.Location.clone().add(0, 0.5, 0);
    } else {
      loc = this.Location.clone().add(0, 0.25, 0);
    }
    for (int i = (this.showID ? -3 : -2); i < this.topCount; i++) {
      List<Entity> entities = (List<Entity>) loc.getWorld().getNearbyEntities(loc.subtract(0, 0.25, 0), 0.1, 0.1, 0.1);
      if (!entities.isEmpty()) {
        ArmorStand A = (ArmorStand) entities.get(0);
        this.armorstandList.add(A);
      }
    }

    return !this.armorstandList.isEmpty();
  }
}
