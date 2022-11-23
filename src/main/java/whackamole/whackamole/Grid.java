package whackamole.whackamole;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

class Grid {
    private static ArrayList<Vector> neighborList = new ArrayList<Vector>() {
        {
            add(new Vector(1, 0, -1)); // * topleft
            add(new Vector(1, 0, 0)); // * top
            add(new Vector(1, 0, 1)); // * topright

            add(new Vector(0, 0, 1)); // * left
            add(new Vector(0, 0, -1)); // * right

            add(new Vector(-1, 0, 1)); // * bottomleft
            add(new Vector(-1, 0, 0)); // * bottom
            add(new Vector(-1, 0, -1)); // * bottomright
        }
    };

    private Config config = Config.getInstance();
    private Logger logger = Logger.getInstance();
    public ArrayList<Block> grid;
    private World world;
    private ArrayList<Entity> debuglist = new ArrayList<>();

    public Grid(World world, ArrayList<Block> grid) {
        this.world = world;
        this.grid = grid;
    }

    public Grid(World world, Block startBlock) throws Exception {
        this.world = world;
        this.grid = this.findGrid(startBlock);
    }

    public Grid(World world, Player player) throws Exception {
        this.world = world;
        Block startBlock = world.getBlockAt(player.getLocation().subtract(0, 1, 0));
        this.grid = this.findGrid(startBlock);
    }

    public Grid(World world, Location loc) throws Exception {
        this.world = world;
        Block startBlock = world.getBlockAt(loc);
        this.grid = this.findGrid(startBlock);
    }

    private ArrayList<Block> findGrid(Block startBlock) throws Exception {
        ArrayList<Block> returnList = new ArrayList<>();
        ArrayList<Block> queue = this.getNeighbors(startBlock);
        while (!queue.isEmpty()) {
            Block block = queue.remove(0);
            if (!returnList.contains(block)) {
                returnList.add(block);
            } else {
                continue;
            }
            ArrayList<Block> queueAdd = this.getNeighbors(block);
            queueAdd.removeIf(s -> returnList.contains(s));
            queue.addAll(queueAdd);
        }
        if (returnList.isEmpty()) {
            throw new Exception("Failed to find a grid, make sure you're standing on the game field");
        }
        return returnList;
    }

    private ArrayList<Block> getNeighbors(Block MiddleBlock) {
        ArrayList<Block> returnlist = new ArrayList<>();
        for (Vector vector : neighborList) {
            Block CurBlock = this.world.getBlockAt(MiddleBlock.getLocation().add(vector));
            if (this.config.MOLEBLOCK.contains(CurBlock.getType().name())
                    && this.config.SUBBLOCK.contains(CurBlock.getRelative(0, -1, 0).getType().name())) {
                returnlist.add(CurBlock);
            }
        }
        return returnlist;
    }

    public boolean onGrid(Player player) {
        return this.onGrid(player.getLocation());
    }

    public boolean onGrid(Location loc) {
        for (Block block : this.grid) {
            if (block.getLocation().add(0.5, 1, 0.5).distance(loc) < 1) {
                return true;
            }
        }
        return false;
    }

    public void spawnArmorStands() {
        for (Block block : this.grid) {
            Entity armorStand = this.world.spawnEntity(block.getLocation().clone().add(0.5, 1, 0.5), EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            debuglist.add(armorStand);
        }
    }

    public void removeArmorStands() {
        for (Entity e : this.debuglist) {
            e.remove();
        }
        this.debuglist.clear();
    }

    public List<List<Integer>> Serialize() {
        List<List<Integer>> outList = new ArrayList<>();
        for (Block block : this.grid) {
            Location loc = block.getLocation();
            outList.add(
                    new ArrayList<>(Arrays.asList(
                            (int) loc.getX(),
                            (int) loc.getY(),
                            (int) loc.getZ())));
        }
        return outList;
    }

    public void Deserialize(List<List<Integer>> grid) {
        for (List<Integer> loc : grid) {
            this.grid.add(this.world.getBlockAt(loc.get(0), loc.get(1), loc.get(2)));
        }
    }
}
