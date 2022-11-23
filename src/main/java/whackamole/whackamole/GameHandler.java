package whackamole.whackamole;


import com.google.protobuf.StringValue;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.*;

import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Score;


public class GameHandler {

    private boolean debug = false;
    private Logger logger = Logger.getInstance();
    private Config config = Config.getInstance();
    public GamesManager gameManager;
    private Econ econ = Econ.getInstance();
    public Player gamePlayer;
    public int Score = 0;
    public static ItemStack axe;
    private ArmorStand mole;
    public ArrayList<Mole> moleList = new ArrayList<>();
    private YMLFile gameConfig;
    private World world;
    public Grid grid;

    public String gameName;
    public String gameType = "";
    public String scoreObjective = "";
    public Boolean cashHats = true;
    public Long Interval = 20L;
    public Integer pointsPerKill = 1;
<<<<<<< HEAD
<<<<<<< HEAD
    private World world;
=======
    private int ID = -1;
=======
    private int ID1 = -1;
>>>>>>> c865bc2 (Game starter:)
    private boolean Running = false;
>>>>>>> e262bdb (Game starter:)

    public GameHandler(YMLFile configFile) throws Exception {
        this.gameConfig = configFile;
        this.loadGame();
        this.loadNBTData();
    }
    public GameHandler(File configFile) throws Exception {
        this.gameConfig = new YMLFile(configFile);
        this.loadGame();
        this.loadNBTData();
    }

    public GameHandler(String gameName, Grid grid) throws FileNotFoundException {
        this.gameName = gameName;
        this.gameConfig = new YMLFile(this.config.gamesData, gameName + ".yml");
        this.grid = grid;
        this.saveGame();
        this.loadNBTData();
    }

    public boolean onGrid(Player player) {
        return this.grid.onGrid(player);
    }

    public boolean onGrid(Location loc) {
        return this.grid.onGrid(loc);
    }

    public void toggleArmorStands() {
        this.debug = !this.debug;
        if (this.debug) {
            this.grid.spawnArmorStands();
        } else {
            this.grid.removeArmorStands();
        }

    }

    Runnable Runner = new Runnable() {
        @Override
        public void run() {
            Player player = gamePlayer;
            BaseComponent[] actionMessage = new ComponentBuilder().append(ComponentSerializer.parse(config.ACTIONTEXT)).append((Config.color("&2&l ") + Score)).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionMessage );

            Random random = new Random();
            int index = random.nextInt(grid.grid.size());
            Location location = grid.grid.get(index).getLocation().clone().add(0.5, -1.5, 0.5);
            mole = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            moleList.add(new Mole(mole));
            mole.setGravity(false);
            mole.setInvisible(true);
            mole.getEquipment().setHelmet(getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIxMjUwM2Q2MWM0OWY3MDFmZWU4NjdkNzkzZjFkY2M1MjJlNGQ3YzVjNDFhNjhmMjk1MTU3OWYyNGU3Y2IyYSJ9fX0="));
        }
    };

    public void Start(Player player) {
        if (!this.Running && this.ID1 == -1) {
            this.gamePlayer = player;
            this.ID1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, Runner, this.Interval * 20L, this.Interval * 20L);
            this.Running = true;
            player.getInventory().addItem(axe);
        }
    }

    public void Stop() {
        if (this.Running) {
            Bukkit.getScheduler().cancelTask(ID1);
            this.Running = false;
            this.ID1 = -1;
            this.gamePlayer.getInventory().removeItem(this.axe);
            this.gamePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            if (Objects.equals(this.gameType, "VAULT")) {
                if (!(econ.econ == null)) {
                    econ.econ.depositPlayer(gamePlayer, this.Score);
                    gamePlayer.sendMessage(this.config.PREFIX + "You've been rewarded " + ChatColor.AQUA + this.config.SYMBOL + this.Score + ChatColor.WHITE + " " + this.config.CURRENCY);
                } else {
                    logger.error("Vault dependency not found, please check if you have the Vault plugin and a Economy plugin");
                    gamePlayer.sendMessage(this.config.PREFIX + ChatColor.DARK_RED + "ERROR: Vault");
                }
            } else if (Objects.equals(this.gameType, "SCORE")) {
                if (!(this.scoreObjective == null)) {
                    org.bukkit.scoreboard.Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(this.scoreObjective);
                    @SuppressWarnings("deprecation")
                    Score score = obj.getScore(gamePlayer);
                    score.setScore(this.Score);
                    gamePlayer.sendMessage(this.config.PREFIX + "You've been rewarded " + ChatColor.AQUA + this.config.SYMBOL + this.Score + ChatColor.WHITE + " " + this.config.CURRENCY);
                } else {
                    logger.error("The scoreboard objective either doesn't exist or isn't correct.");
                    gamePlayer.sendMessage(this.config.PREFIX + ChatColor.DARK_RED + "ERROR: Objective");
                }
            } else {
                gamePlayer.sendMessage(this.config.PREFIX + ChatColor.DARK_RED + "'" + this.gameType + "'" + (Objects.equals(this.gameType, "vault")));
                logger.error("No payment method has been set, please see the game file");
            }
            this.Score = 0;
        }
    }
    public void saveGame() {

        this.gameConfig.FileConfig.options().setHeader(Arrays.asList(
                "###########################################################",
                " ^------------------------------------------------------^ #",
                " |                       GameFile                       | #",
                " <------------------------------------------------------> #",
                "###########################################################",
                "NOTE: you can edit the Properties to change the game rules",
                "NOTE: Do not touch the Field Data!",
                "",
                "EXPLANATION:",
                "Name = Gamename (make sure this is the same as the Filename)",
                "Type = set this either to 'VAULT' or to 'SCORE'",
                "    - VAULT means that the points get on your economy account (this requires Vault and a Economy plugin)",
                "    - SCORE means that the points will de added to a scoreboard objective (in this case, make sure to add the objective name)",
                "CashHats = a chance of 0.1% that a special mole will spawn giving a bigger reward",
                "Interval = XXXXXXXXXXXXXXXXXXXXXXXX",
                "Points per Kill = how many points should be rewarded per kill",
                "That is all, enjoy messing around :)",
                ""));
        this.gameConfig.set("Properties.Name", this.gameName);
        this.gameConfig.set("Properties.Type", this.gameType);
        this.gameConfig.set("Properties.Objective", this.scoreObjective);
        this.gameConfig.set("Properties.CashHats", this.cashHats);
        this.gameConfig.set("Properties.Interval", this.Interval);
        this.gameConfig.set("Properties.Points per Kill", this.pointsPerKill);
        this.gameConfig.set("Field Data.World", this.grid.world.getName());
        this.gameConfig.set("Field Data.Grid", this.grid.Serialize());
        this.gameConfig.save();
    }

    public void loadGame() {
        this.world           = Bukkit.getWorld(this.gameConfig.getString("Field Data.World"));
        this.grid           = Grid.Deserialize(this.world, (List<List<Integer>>) this.gameConfig.getList("Field Data.Grid"));
        this.gameName       = this.gameConfig.getString("Properties.Name");
        this.gameType       = this.gameConfig.getString("Properties.Type");
        this.scoreObjective = this.gameConfig.getString("Properties.Objective");
        this.cashHats       = this.gameConfig.getBoolean("Properties.CashHats");
        this.Interval       = this.gameConfig.getLong("Properties.Interval");
        this.pointsPerKill  = this.gameConfig.getInt("Properties.Points per Kill");
    }

    public void deleteSave() {
        this.gameConfig.remove();
    }
    public void loadNBTData() {
        ItemStack axe = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        ((Damageable) axeMeta).setDamage(31);
        axeMeta.setUnbreakable(true);
        axeMeta.addEnchant(Enchantment.LURE, 1, true);
        axeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        axeMeta.setDisplayName(this.config.HAMMERNAME);
        axe.setItemMeta(axeMeta);

        this.axe = axe;

    }

    public ItemStack getSkull(String url) {
        ItemStack moleHead = new ItemStack(Material.PLAYER_HEAD);
        if (url.isEmpty()) return moleHead;
        SkullMeta moleMeta = (SkullMeta) moleHead.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", url));
        try
        {
            Field profileField = moleMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(moleMeta, profile);
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        moleHead.setItemMeta(moleMeta);
        return moleHead;
    }


}
