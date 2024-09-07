package whackamole.whackamole.GS;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import whackamole.whackamole.DB.HologramDB;
import whackamole.whackamole.DB.HologramRow;
import whackamole.whackamole.DB.SQLite;
import whackamole.whackamole.Utils.Misc;
import whackamole.whackamole.Utils.Translator;

import java.util.ArrayList;
import java.util.List;

public class Hologram extends HologramRow {
    public HologramDB db = SQLite.Hologram;
    public List<HologramRow> holograms = new ArrayList<>();
    private List<ArmorStand> armorstandList = new ArrayList<>();
    private Game game;
    public int holoID;

    public Hologram(Game game, int holoID) {
        this.game = game;
        this.holoID = holoID;
    }

    public void Create(int holoID, String type, Location loc, int topCount) {
        var hologram = new HologramRow();
        double height = 1.75 + (0.25 * topCount);
        hologram.holoID = holoID;
        hologram.gameID = game.getID();
        hologram.Type = type;
        hologram.Location = loc.add(0, height, 0);
        hologram.topCount = topCount;


        this.holograms.add(hologram);
        this.db.Insert(hologram);

        this.summonHolos(hologram);
    }

    public void Delete() {
        this.killHolos();
        for (var row : holograms) {
            this.db.Delete(row);
        }
    }

    private void summonHolos(HologramRow hologram) {
        ArmorStand armorstandMain = (ArmorStand) hologram.Location.getWorld().spawnEntity(hologram.Location, EntityType.ARMOR_STAND);
        armorstandMain = this.addArmorStandSettings(armorstandMain);
        armorstandMain.setCustomName(Translator.GAME_HOLO_HIGHSCORES.Format());
        this.armorstandList.add(armorstandMain);

        ArmorStand armorstandType = (ArmorStand) hologram.Location.getWorld().spawnEntity(hologram.Location.subtract(0,0.25,0), EntityType.ARMOR_STAND);
        armorstandType = this.addArmorStandSettings(armorstandType);



        for (int i = 0; i < hologram.topCount; i++) {
            ArmorStand a = (ArmorStand) hologram.Location.getWorld().spawnEntity(hologram.Location.subtract(0, 0.25, 0), EntityType.ARMOR_STAND);
            a = this.addArmorStandSettings(a);

            switch (hologram.Type) {
                case "Score"    : {
                    var score = this.game.getScoreboard().getTop(0);
                    armorstandType.setCustomName(Translator.HOLOGRAM_ARMORSTANDTYPE_SCORE.Format());
                    if (i < score.length) {
                        a.setCustomName(ChatColor.DARK_AQUA + "" + (i+1) + ". " + ChatColor.WHITE + score[i].player.getName() + " : " + ChatColor.AQUA + score[i].Score + ChatColor.WHITE + ", " + ChatColor.YELLOW + score[i].Datetime.toLocalDate().toString());
                    } else {
                        a.setCustomName(ChatColor.DARK_AQUA + "" + (i+1) + ". " + ChatColor.YELLOW + "_________________________");
                    }
                    break;
                }
                case "Streak"   : {
                    var score = this.game.getScoreboard().getTop(1);
                    armorstandType.setCustomName(Translator.HOLOGRAM_ARMORSTANDTYPE_STREAK.Format());
                    if (i < score.length) {
                        a.setCustomName(Misc.Color("&3" + (i+1) + ". &f" + score[i].player.getName() + " : &b" + score[i].scoreStreak + "&f, &e" + score[i].Datetime.toLocalDate().toString()));
                    } else {
                        a.setCustomName(ChatColor.DARK_AQUA + "" + (i+1) + ". " + ChatColor.YELLOW + "_________________________");
                    }
                    break;
                }
                case "molesHit" : {
                    var score = this.game.getScoreboard().getTop(2);
                    armorstandType.setCustomName(Translator.HOLOGRAM_ARMORSTANDTYPE_MOLESHIT.Format());
                    if (i < score.length) {
                        a.setCustomName("&3" + (i+1) + ". &f" + score[i].player.getName() + " : &b" + score[i].molesHit + "&f, &e" + score[i].Datetime.toLocalDate().toString());
                    } else {
                        a.setCustomName(ChatColor.DARK_AQUA + "" + (i+1) + ". " + ChatColor.YELLOW + "_________________________");
                    }
                    break;
                }
            }

            this.armorstandList.add(a);
        }
        this.armorstandList.add(armorstandType);

    }

    private void killHolos() {
        for (var e : this.armorstandList) {
            e.remove();
        }
    }

    private ArmorStand addArmorStandSettings(ArmorStand armorStand) {
        armorStand.setVisible(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.isInvulnerable();
        return armorStand;
    }
}
