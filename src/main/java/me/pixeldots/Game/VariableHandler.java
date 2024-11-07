package me.pixeldots.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import me.pixeldots.Extras.*;
import me.pixeldots.Game.data.*;
import me.pixeldots.Shops.data.ItemShopData;
import me.pixeldots.Shops.data.UpgradeShopData;
import me.pixeldots.Utils.Utils;

public class VariableHandler {

    public Vector AreaStart = new Vector(0,0,0);
    public Vector AreaEnd = new Vector(0,0,0);
    public Vector LobbyPosition = new Vector(0,0,0);
    public Vector SpectatorSpawn = new Vector(0,0,0);

    public List<TeamObjectData<Vector>> IronSpawners = new ArrayList<>();
    public List<Vector> DiamondSpawners = new ArrayList<>();
    public List<Vector> EmeraldSpawners = new ArrayList<>();

    public List<TeamObjectData<DirectionVector>> TeamSpawns = new ArrayList<>();
    public List<TeamObjectData<BedData>> TeamBeds = new ArrayList<>();
    public List<String> Teams = new ArrayList<>();

    public ItemShopData itemShopData;
    public UpgradeShopData upgradeShopData;
    public Map<UUID, Map<Integer, String>> perPlayerQuickBuy = new HashMap<>();

    // Game Properties
    public List<TeamObjectData<int[]>> TeamHasBed = new ArrayList<>();
    public Map<Integer, TeamUpgrades> TeamUpgrades = new HashMap<>();
    
    public List<Vector> BlocksPlaced = new ArrayList<>();
    public List<WorldBlockData> WorldBlocksDestroyed = new ArrayList<>();

    public List<UUID> PlayersInGame = new ArrayList<>();
    public Map<UUID, PlayerStatistics> PlayerStats = new HashMap<>();
    public List<UUID> Spectators = new ArrayList<>();
    
    // Extras
    public List<PopUpTowerData> PopUpTowers = new ArrayList<>();
    public List<DreamDefenderEntity> DreamDefenders = new ArrayList<>();
    public List<BedBugEntity> BedBugs = new ArrayList<>();
    public List<BridgeEggEntity> BridgeEggs = new ArrayList<>();
    public List<EnderDragonEntity> EnderDragons = new ArrayList<>();

    public WorldInformation WorldInfo = new WorldInformation();

    // NPCs
    public List<ArmorStand[]> HologramEntitys = new ArrayList<>();
    public float HologramRotation = 0;

    public Vector AreaMin = new Vector(0,0,0);
    public Vector AreaMax = new Vector(0,0,0);

    public String getTeamColor(UUID player) {
        if (!PlayerStats.containsKey(player)) return "white";
        return Teams.get(PlayerStats.get(player).team);
    }

    public DirectionVector getTeamSpawn(int team) {
        List<DirectionVector> spawns = new ArrayList<>();
        for (int i = 0; i < TeamSpawns.size(); i++) {
            if (TeamSpawns.get(i).team == team) {
                spawns.add(TeamSpawns.get(i).data);
            }
        }

        if (spawns.size() > 1) return spawns.get(Utils.randomRange(0, spawns.size()));
        return (spawns.size() == 0 ? null : spawns.get(0));
    }

    public void addTeamSpawn(int team, Vector pos, float pitch, float yaw) {
        TeamSpawns.add(new TeamObjectData<DirectionVector>(team, new DirectionVector(pos, pitch, yaw)));
    }

    public List<BedData> getTeamBeds(int team) {
        List<BedData> beds = new ArrayList<>();
        for (int i = 0; i < TeamBeds.size(); i++) {
            if (TeamBeds.get(i).team == team) {
                beds.add(TeamBeds.get(i).data);
            }
        }
        
        return beds;
    }

    public BedData getTeamBed(int team, Location location) {
        List<BedData> beds = getTeamBeds(team);
        for (int i = 0; i < beds.size(); i++) {
            Vector pos = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            BedData bed = beds.get(i);

            if (Utils.equals(bed.pos, pos)) return bed;
            
            Location location2 = location.getBlock().getRelative(BlockFace.valueOf(bed.facing).getOppositeFace()).getLocation();
            Vector pos2 = new Vector(location2.getBlockX(), location2.getBlockY(), location2.getBlockZ());
            if (Utils.equals(bed.pos, pos2)) return bed;
        }
        return null;
    }

    public void addTeamBed(int team, BedData data) {
        TeamBeds.add(new TeamObjectData<BedData>(team, data));
    }

    public int teamBedCount(int team) {
        int count = 0;
        for (int i = 0; i < TeamBeds.size(); i++) {
            if (TeamBeds.get(i).team == team) count++;
        }
        return count;
    }

    public boolean containsTeamBed(int team) {
        for (int i = 0; i < TeamBeds.size(); i++) {
            if (TeamBeds.get(i).team == team) return true;
        }
        return false;
    }

    public List<Vector> getTeamGenerators(int team) {
        List<Vector> generators = new ArrayList<>();
        for (int i = 0; i < IronSpawners.size(); i++) {
            if (IronSpawners.get(i).team == team) {
                generators.add(IronSpawners.get(i).data);
            }
        }
        
        return generators;
    }

    public void addTeamGenerator(int team, Vector pos) {
        IronSpawners.add(new TeamObjectData<Vector>(team, pos));
    }

    public boolean canTeamRespawn(int team) {
        for (int i = 0; i < TeamHasBed.size(); i++) {
            if (TeamHasBed.get(i).team == team && TeamHasBed.get(i).data[0] == 1) return true;
        }
        return false;
    }

    public void teamBedDestroyed(int team, int bedID) {
        for (int i = 0; i < TeamHasBed.size(); i++) {
            TeamObjectData<int[]> data = TeamHasBed.get(i);
            if (data.team == team && data.data[1] == bedID) { data.data[0] = 0; return; }
        }
    }

    public void addTeamHasBed(int team, boolean has, int id) {
        int hasInt = (has == true ? 1 : 0);
        TeamHasBed.add(new TeamObjectData<int[]>(team, new int[] {hasInt, id}));
    }
    
}
