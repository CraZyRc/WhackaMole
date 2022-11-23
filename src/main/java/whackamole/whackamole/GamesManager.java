package whackamole.whackamole;

<<<<<<< HEAD
=======
import java.io.File;
<<<<<<< HEAD
import java.security.spec.ECField;
>>>>>>> 1e62199 (change :))
=======
>>>>>>> 328aa81 (change :))
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.BaseComponentSerializer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.w3c.dom.Text;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;


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
        Player player = event.getPlayer();
        ItemStack axe = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        ((Damageable) axeMeta).setDamage(31);
        axeMeta.setUnbreakable(true);
        axeMeta.addEnchant(Enchantment.LURE, 1, true);
        axeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        axeMeta.setDisplayName(color("&0&l[&6&lWAM&0&l] #28c00&lS#2fca00&lMASHER"));
        axe.setItemMeta(axeMeta);

        String actionName = this.config.getNext();
        BaseComponent[] list = ComponentSerializer.parse(actionName);
        BaseComponent[] sendList = new ComponentBuilder().append(list).create();


        for (GameHandler gameHandler : games) {
            if (gameHandler.onGrid(event.getTo())) {
                if (!(player.getInventory().contains(axe))) {
                    player.getInventory().addItem(axe);
                }
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, sendList);
                for (int i = 0; i == gameHandler.Interval; i++) {
//                    if (i == gameHandler.Interval) {
//                        Location l = g;
//                        double x = l.getX();
//                        double y = l.getY();
//                        double z = l.getZ();
//                        event.getPlayer().getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
//                    }
                }


            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                player.getInventory().removeItem(axe);
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

    public static String color(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#','x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
