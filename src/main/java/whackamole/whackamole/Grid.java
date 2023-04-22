package whackamole.whackamole;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import whackamole.whackamole.DB.SQLite;
import whackamole.whackamole.DB.GridDB;
import whackamole.whackamole.Mole.*;

public class Grid {
    private static final GridDB SQL = SQLite.getGridDB();
    private static ArrayList<Vector> neighborList = new ArrayList<>() {
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

    public ArrayList<Mole> entityList = new ArrayList<>();

    public ArrayList<Block> grid;
    public World world;

    public Grid() {
    }

    public Grid(World world, ArrayList<Block> grid) {
        this.world = world;
        this.grid = grid;
    }

    public Grid(World world, Player player) throws Exception {
        this.world = world;
        Block startBlock = world.getBlockAt(player.getLocation().subtract(0, 1, 0));
        this.grid = this.findGrid(startBlock);
    }

    public Grid(Game.Settings game) {
        this.grid = new ArrayList<>();
        this.world = game.world;
        var blockList = SQL.Select(game.ID);
        for (var block : blockList) {
            grid.add(game.world.getBlockAt(block.X, block.Y, block.Z));
        }
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
            queueAdd.removeIf(returnList::contains);
            queue.addAll(queueAdd);
        }

        if (returnList.isEmpty()) {
            throw new Exception(Translator.GRID_EMPTYGRID.toString());
        } else if (returnList.size() > Config.Game.FIELD_MAX_SIZE) {
            throw new Exception(Translator.GRID_INVALIDSIZE.toString());
        }
        return returnList;
    }

    private ArrayList<Block> getNeighbors(Block MiddleBlock) {
        ArrayList<Block> returnlist = new ArrayList<>();
        for (Vector vector : neighborList) {
            Block CurBlock = this.world.getBlockAt(MiddleBlock.getLocation().add(vector));
            if (Config.Game.MOLEBLOCK.contains(CurBlock.getType().name())) {
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

            if ((Math.abs(blockLoc.getX() - loc.getX()) <= Config.Game.FiELD_MARGIN_X) // * X
                    && (Math.abs(blockLoc.getY() - loc.getY()) <= Config.Game.FiELD_MARGIN_Y
                            && blockLoc.getY() < loc.getY()) // * Y
                    && (Math.abs(blockLoc.getZ() - loc.getZ()) <= Config.Game.FiELD_MARGIN_X) // * Z
            ) {
                return true;
            }
        }
        return false;
    }

    public void spawnRandomEntity(MoleType type, double moleSpeed, BlockFace Rotation) {
        Random random = new Random();
        int index = random.nextInt(this.grid.size());
        this.spawnEntity(this.grid.get(index), type, moleSpeed, Rotation);
    }

    public void spawnEntity(Block block, MoleType type, double moleSpeed, BlockFace Rotation) {
        this.spawnEntity(block.getLocation().clone().add(0.5, -1.5, 0.5).setDirection(Rotation.getDirection()), type,
                moleSpeed);
    }

    public void spawnEntity(Location loc, MoleType type, double moleSpeed) {
        this.entityList.add(
                new Mole(
                        type,
                        (ArmorStand) this.world.spawnEntity(loc, EntityType.ARMOR_STAND),
                        moleSpeed));
    }

    public void removeEntities() {
        for (int i = this.entityList.size() - 1; i >= 0; i--) {
            this.entityList.get(i).unload();
        }
        this.entityList.clear();
    }

    public void removeEntities(MoleState state) {
        for (int i = this.entityList.size() - 1; i >= 0; i--) {
            if (this.entityList.get(i).state == state) {
                this.entityList.get(i).unload();
                this.entityList.remove(i);
            }
        }
    }

    public int entityUpdate() {
        this.removeEntities(MoleState.Hidden);
        int missedCount = 0;
        for (Mole mole : this.entityList) {
            if (mole.state == MoleState.Missed)
                missedCount++;
            mole.update();
        }
        return missedCount;
    }

    public Mole handleHitEvent(Entity e) {
        for (Mole mole : this.entityList) {
            if (mole.equals(e) && mole.isMoving()) {
                return mole;
            }
        }
        return null;
    }

    public void Save(int gameID) {
        SQL.Insert(this, gameID);
    }
    
    public void Delete(int gameID) {
        SQL.Delete(gameID);
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

    public static Grid Deserialize(World world, List<?> data) {
        ArrayList<Block> grid = new ArrayList<>();
        for (Object datum : data) {
            List<?> loc = (List<?>) datum;
            grid.add(world.getBlockAt(
                    (Integer) loc.get(0),
                    (Integer) loc.get(1),
                    (Integer) loc.get(2)));
        }
        return new Grid(world, grid);
    }
}
