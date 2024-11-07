package me.pixeldots.API;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Game.data.PlayerStatistics;
import me.pixeldots.Game.data.WorldBlockData;
import me.pixeldots.Shops.ShopUtils.CurrencyType;
import me.pixeldots.Utils.PlayerUtils;
import me.pixeldots.Utils.Utils;

public class APIUtils {

    public static void worldBlockDestroyed(Block block) {
        WorldBlockData data = new WorldBlockData(block.getType(), block.getLocation(), block.getBlockData());
        BedwarsRunner.Variables.WorldBlocksDestroyed.add(data);
    }

    public static void addPlayerPlacedBlock(Location pos) {
        addPlayerPlacedBlock(new Vector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()));
    }
    public static void addPlayerPlacedBlock(Vector pos) {
        BedwarsRunner.Variables.BlocksPlaced.add(pos);
    }

    public static void playerPurchaseItem(Player player, ItemStack give, CurrencyType currency, int currencyCount) {
        PlayerUtils.giveItem(player, player.getInventory(), give, currency, currencyCount);
    }

    /**
     * sets the players team
     * @param uuid the players uuid
     * @param teamID the team to join
     * @return returns debug messages
     */
    public static String setPlayerTeamID(UUID uuid, int teamID) {
        if (isGameRunning()) return "The game is already running.";
        if (BedwarsRunner.Variables.Teams.size()-1 < teamID) return "That team does not exsist.";
        if (BedwarsRunner.Variables.PlayerStats.containsKey(uuid)) return "The player is already on a team.";
        PlayerStatistics stats = new PlayerStatistics(); stats.team = teamID;
        BedwarsRunner.Variables.PlayerStats.put(uuid, stats);
        return "The player is now on " + getTeamName(teamID) + " team";
    }
    /**
     * gets the Players team ID
     * @param uuid the players uuid
     * @return returns the players team ID
     */
    public static int getPlayerTeamID(UUID uuid) { return BedwarsRunner.Variables.PlayerStats.get(uuid).team; }
    /**
     * checks if a player is a spectator
     * @param uuid the players uuid
     * @return returns if the player is a spectator
     */
    public static boolean isPlayerSpectator(UUID uuid) {
        return BedwarsRunner.Variables.PlayerStats.get(uuid).isDead != null;
    }
    /**
     * checks if a player can respawn
     * @param uuid the players uuid
     * @return returns if the player can respawn, returns true if the player is alive
     */
    public static boolean canSpectatorRespawn(UUID uuid) {
        PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(uuid);
        if (stats.isDead == null) return true;
        return stats.isDead.hasBed;
    }

    /**
     * gets the team name from id
     * @param ID the team id
     * @return returns the team name
     */
    public static String getTeamName(int ID) { return BedwarsRunner.Variables.Teams.get(ID); }
    /**
     * gets how many teams are on this server
     * @return returns the team count
     */
    public static int getTeamCount() { return BedwarsRunner.Variables.Teams.size(); }

    /**
     * checks if the game is runnning
     * @return returns if the game is running or not
     */
    public static boolean isGameRunning() { return BedwarsRunner.isRunning; }
    /**
     * checks if the game is starting
     * @return returns if the game is starting or not
     */
    public static boolean isGameStarting() { return BedwarsRunner.isStarting; }

    // Statistics
    public static int getPlayerKills(UUID uuid) {
        if (!BedwarsRunner.Variables.PlayerStats.containsKey(uuid)) return 0;
        return BedwarsRunner.Variables.PlayerStats.get(uuid).kills;
    }
    public static int getPlayerFinalKills(UUID uuid) {
        if (!BedwarsRunner.Variables.PlayerStats.containsKey(uuid)) return 0;
        return BedwarsRunner.Variables.PlayerStats.get(uuid).finalKills;
    }
    public static int getPlayerBedsBroken(UUID uuid) {
        if (!BedwarsRunner.Variables.PlayerStats.containsKey(uuid)) return 0;
        return BedwarsRunner.Variables.PlayerStats.get(uuid).bedsBroken;
    }

}
