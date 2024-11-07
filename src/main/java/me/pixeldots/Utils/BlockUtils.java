package me.pixeldots.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import me.pixeldots.BedwarsRunner;

public class BlockUtils {

    public static Vector getVector(Location location) {
        return new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static boolean isBedBlock(Material mat) {
        if (mat.name().endsWith("_BED")) return true;
        return false;
    }

    public static boolean isWoolBlock(Material mat) {
        if (mat.name().endsWith("_WOOL")) return true;
        return false;
    }

    public static boolean isTerracottaBlock(Material mat) {
        if (mat.name().endsWith("_TERRACOTTA")) return true;
        return false;
    }

    public static boolean isGlassBlock(Material mat) {
        if (mat.name().endsWith("_STAINED_GLASS")) return true;
        return false;
    }

    public static boolean isBlockOutOfBounds(Location location) {
        Vector pos = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Vector min = BedwarsRunner.Variables.AreaMin;
        Vector max = BedwarsRunner.Variables.AreaMax;

        return pos.getBlockX() < min.getBlockX() || pos.getBlockY() < min.getBlockY() || pos.getBlockZ() < min.getBlockZ() || pos.getBlockX() > max.getBlockX() || pos.getBlockY() > max.getBlockY() || pos.getBlockZ() > max.getBlockZ();
    }
    
}
