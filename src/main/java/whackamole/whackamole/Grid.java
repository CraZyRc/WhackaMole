package whackamole.whackamole;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import whackamole.whackamole.Mole.*;

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

    public ArrayList<Mole> entityList = new ArrayList<>();

    public ArrayList<Block> grid;
    public World world;

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
        // TODO: Start block lookup downwards
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
            if (   this.config.MOLEBLOCK.contains(CurBlock.getType().name())
                && this.config.SUBBLOCK .contains(CurBlock.getRelative(0, -1, 0).getType().name())
            ) {
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
            Location blockLoc = block.getLocation().add(0.5, 0, 0.5);

            if( (Math.abs(blockLoc.getX() - loc.getX()) <= this.config.FiELD_MARGIN_X) // * X
            &&  (Math.abs(blockLoc.getY() - loc.getY()) <= this.config.FiELD_MARGIN_Y && blockLoc.getY() < loc.getY()) // * Y
            &&  (Math.abs(blockLoc.getZ() - loc.getZ()) <= this.config.FiELD_MARGIN_X) // * Z
            ) {
                return true;
            }
        }
        return false;
    }
    

    public void spawnArmorStands() {
        for (Block block : this.grid) {
            this.spawnEntity(block, MoleType.Debug);
        }
    }

    public void removeArmorStands() {
        this.removeEntities(MoleType.Debug);
    }


    public void spawnRandomEntity(MoleType type) {
        Random random = new Random();
        int index = random.nextInt(this.grid.size());
        this.spawnEntity(this.grid.get(index), type);
    }

    public void spawnEntity(Block block, MoleType type) {
        this.spawnEntity(block.getLocation().clone().add(0.5, 0, 0.5), type);
    }
    public void spawnEntity(Location loc, MoleType type) {
        this.entityList.add(
            new Mole(
                type,
                (ArmorStand) this.world.spawnEntity(loc, EntityType.ARMOR_STAND)
            )
        );
    }

    public void removeEntities() {
        this.entityList.clear();
    }
    public void removeEntities(MoleState state) {
        this.entityList.removeIf(mole -> {return mole.state == state;});
    }
    public void removeEntities(MoleType type) {
        this.entityList.removeIf(mole -> {return mole.type == type;});
    }


    public int entityUpdate() {
        this.removeEntities(MoleState.Hidden);
        int missedCount = 0;
        for(Mole mole : this.entityList) {
            if(mole.state == MoleState.Missed) missedCount++;
            mole.update();
        }
        return missedCount;
    }


    public boolean handleHitEvent(Entity e) {
        for(Mole mole : this.entityList) {
            if(mole.equals(e) && mole.isMoving()) {
                mole.state = MoleState.Hit;
                return true;
            }
        }
        return false;
    }


    public List<List<Integer>> Serialize() {
        List<List<Integer>> outList = new ArrayList<>();
        for (Block block : this.grid) {
            Location loc = block.getLocation();
            outList.add(
                new ArrayList<>(Arrays.asList(
                    (int) loc.getX(),
                    (int) loc.getY(),
                    (int) loc.getZ())
                )
            );
        }
        return outList;
    }

    public static Grid Deserialize(World world, List<List<Integer>> data) {
        ArrayList<Block> grid = new ArrayList<>();
        for (List<Integer> loc : data) {
            grid.add(world.getBlockAt(loc.get(0), loc.get(1), loc.get(2)));
        }
        return new Grid(world, grid);
    }
}
