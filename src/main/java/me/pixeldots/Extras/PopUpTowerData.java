package me.pixeldots.Extras;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.util.Vector;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Utils.BlockUtils;

public class PopUpTowerData {

    // Compact Pop-Up Tower
    // 8 Blocks Tall, 5 Blocks Bottom
    public static layerData[] data = new layerData[] { 
        new layerData(2,
            new blockData(1,0,1, "wool"), new blockData(2,0,0, "wool"), new blockData(2,0,-1, "wool"),
            new blockData(1,0,-2, "wool"), new blockData(0,0,-2, "wool"), new blockData(0,0,-1, "ladder"),
            new blockData(-1,0,-2, "wool"), new blockData(-2,0,-1, "wool"), new blockData(-2,0,0, "wool"),
            new blockData(-1,0,1, "wool")),
        new layerData(3,
            new blockData(1,0,1, "wool"), new blockData(2,0,0, "wool"), new blockData(2,0,-1, "wool"),
            new blockData(1,0,-2, "wool"), new blockData(0,0,-2, "wool"), new blockData(0,0,-1, "ladder"),
            new blockData(-1,0,-2, "wool"), new blockData(-2,0,-1, "wool"), new blockData(-2,0,0, "wool"),
            new blockData(-1,0,1, "wool"), new blockData(0,0,1, "wool")),
        new layerData(1,
            new blockData(0,0,2, "wool"), new blockData(-2,0,2, "wool"), new blockData(-3,0,1, "wool"), 
            new blockData(-3,0,-2, "wool"), new blockData(-2,0,-3, "wool"), new blockData(0,0,-3, "wool"), 
            new blockData(2,0,-3, "wool"), new blockData(3,0,-2, "wool"), new blockData(3,0,1, "wool"), 
            new blockData(2,0,2, "wool"), new blockData(0,0,1, "wool"), new blockData(-1,0,1, "wool"),
            new blockData(-2,0,1, "wool"), new blockData(-2,0,0, "wool"), new blockData(-2,0,-1, "wool"),
            new blockData(-2,0,-2, "wool"), new blockData(-1,0,-2, "wool"), new blockData(0,0,-2, "wool"),
            new blockData(1,0,-2, "wool"), new blockData(2,0,-2, "wool"), new blockData(2,0,-1, "wool"), 
            new blockData(2,0,0, "wool"), new blockData(2,0,1, "wool"), new blockData(1,0,1, "wool"),
            new blockData(0,0,0, "wool"), new blockData(-1,0,0, "wool"), new blockData(-1,0,-1, "wool"),
            new blockData(0,0,-1, "ladder"), new blockData(1,0,-1, "wool"), new blockData(1,0,0, "wool")),
        new layerData(1,
            new blockData(0,0,2, "wool"), new blockData(-1,0,2, "wool"), new blockData(-2,0,2, "wool"),
            new blockData(-3,0,1, "wool"), new blockData(-3,0,0, "wool"), new blockData(-3,0,-1, "wool"), 
            new blockData(-3,0,-2, "wool"), new blockData(-2,0,-3, "wool"), new blockData(-1,0,-3, "wool"), 
            new blockData(0,0,-3, "wool"), new blockData(1,0,-3, "wool"), new blockData(2,0,-3, "wool"),
            new blockData(3,0,-2, "wool"), new blockData(3,0,-1, "wool"), new blockData(3,0,0, "wool"),
            new blockData(3,0,1, "wool"), new blockData(2,0,2, "wool"), new blockData(1,0,2, "wool")),
        new layerData(1,
            new blockData(0,0,2, "wool"), new blockData(-2,0,2, "wool"), new blockData(-3,0,1, "wool"), 
            new blockData(-3,0,-2, "wool"), new blockData(-2,0,-3, "wool"), new blockData(0,0,-3, "wool"), 
            new blockData(2,0,-3, "wool"), new blockData(3,0,-2, "wool"), new blockData(3,0,1, "wool"), 
            new blockData(2,0,2, "wool")),
    };
    public BlockFace facing;
    public Vector placedAt;

    public int teamID = 0;
    public int currentLayer = 0;
    public int currentBuildCount = 0;
    public int buildProgress = 0;
    public int YPos = 0;

    public PopUpTowerData(BlockFace _facing, int _teamID, Vector _placedAt) {
        this.placedAt = _placedAt;
        this.facing = _facing;
        this.teamID = _teamID;
        buildProgress = 0;
        currentBuildCount = 0;
        currentLayer = 0;
    }

    public void tick() {
        layerData layerData = data[currentLayer];
        blockData blockData = layerData.blocks[buildProgress];

        Vector blockPos = rotateVectorBlockFace(blockData.pos.clone(), facing);
        Vector vector = new Vector(blockPos.getBlockX()+placedAt.getBlockX(), blockPos.getBlockY()+placedAt.getBlockY()+YPos, blockPos.getBlockZ()+placedAt.getBlockZ());
        Block block = BedwarsRunner.world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
        if (block.getType() == Material.AIR && !BlockUtils.isBlockOutOfBounds(block.getLocation())) {
            BedwarsRunner.Variables.BlocksPlaced.add(vector);
            block.setType(getBlockMaterial(blockData.blockType, teamID));
            if (block.getBlockData() instanceof Ladder) {
                Ladder ladder = ((Ladder)block.getBlockData()); ladder.setFacing(facing);
                BedwarsRunner.world.setBlockData(block.getLocation(), ladder);
            }
        }
        
        buildProgress++;
        if (buildProgress >= layerData.blocks.length) {
            buildProgress = 0;
            currentBuildCount++;
            YPos++;
            if (currentBuildCount >= layerData.buildCount) {
                currentBuildCount = 0;
                currentLayer++;
                if (currentLayer >= data.length) BedwarsRunner.Variables.PopUpTowers.remove(this);
            }
        }
    }

    public static Vector rotateVectorBlockFace(Vector base, BlockFace face) {
        switch (face) {
            case NORTH:
                return base.multiply(-1);
            case EAST:
                return new Vector(base.getBlockZ(), base.getBlockY(), base.getBlockX());
            case WEST:
                return new Vector(base.getBlockZ(), base.getBlockY(), base.getBlockX()).multiply(-1);
            default:
                return base;
        }
    }

    public Material getBlockMaterial(String type, int team) {
        if (type.equalsIgnoreCase("ladder")) return Material.LADDER;
        return Material.valueOf((BedwarsRunner.Variables.Teams.get(team) + "_" + type).toUpperCase());
    }

    public static class layerData {
        public blockData[] blocks;
        public int buildCount = 0;

        public layerData(int _buildCount, blockData... _blocks) {
            this.buildCount = _buildCount;
            this.blocks = _blocks;
        }
    }
    
    public static class blockData {
        public Vector pos;
        public String blockType;
        public BlockFace facing = BlockFace.NORTH;

        public blockData(Vector _pos, String _blockType) {
            this.pos = _pos;
            this.blockType = _blockType;
        }
        public blockData(float _x, float _y, float _z, String _blockType) {
            this(new Vector(_x, _y, _z), _blockType);
        }
        public blockData(Vector _pos, String _blockType, BlockFace _facing) {
            this(_pos, _blockType);
            this.facing = _facing;
        }
        public blockData(float _x, float _y, float _z, String _blockType, BlockFace _facing) {
            this(new Vector(_x, _y, _z), _blockType, _facing);
        }
    }
    
}
