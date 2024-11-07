package me.pixeldots.Game.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class WorldBlockData {

    public Material blockType;
    public Location blockPos;
    public BlockData blockData;

    public WorldBlockData() {}
    public WorldBlockData(Material _blockType, Location _blockPos, BlockData _blockData) {
        this.blockType = _blockType;
        this.blockPos = _blockPos;
        this.blockData = _blockData;
    }
    
}
