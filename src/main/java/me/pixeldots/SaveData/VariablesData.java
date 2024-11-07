package me.pixeldots.SaveData;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import org.bukkit.util.Vector;

import me.pixeldots.Game.BedData;
import me.pixeldots.Game.VariableHandler;
import me.pixeldots.Game.data.DirectionVector;
import me.pixeldots.Game.data.TeamObjectData;

public class VariablesData {

    public int[] AreaStart;
    public int[] AreaEnd;
    public int[] LobbyPosition;
    public int[] SpectatorSpawn;

    public List<float[]> IronSpawners = new ArrayList<>();
    public List<float[]> DiamondSpawners = new ArrayList<>();
    public List<float[]> EmeraldSpawners = new ArrayList<>();

    public List<float[]> TeamSpawns = new ArrayList<>();
    public List<String> TeamBeds = new ArrayList<>();
    public List<String> Teams = new ArrayList<>();

    public VariablesData(VariableHandler handler) {
        this.AreaStart = new int[] {handler.AreaStart.getBlockX(), handler.AreaStart.getBlockY(), handler.AreaStart.getBlockZ()};
        this.AreaEnd = new int[] {handler.AreaEnd.getBlockX(), handler.AreaEnd.getBlockY(), handler.AreaEnd.getBlockZ()};
        this.LobbyPosition = new int[] {handler.LobbyPosition.getBlockX(), handler.LobbyPosition.getBlockY(), handler.LobbyPosition.getBlockZ()};
        this.SpectatorSpawn = new int[] {handler.SpectatorSpawn.getBlockX(), handler.SpectatorSpawn.getBlockY(), handler.SpectatorSpawn.getBlockZ()};

        this.DiamondSpawners.clear();
        for (int i = 0; i < handler.DiamondSpawners.size(); i++) {
            Vector v = handler.DiamondSpawners.get(i);
            this.DiamondSpawners.add(new float[] {(float)v.getX(), (float)v.getY(), (float)v.getZ()});
        }
        this.EmeraldSpawners.clear();
        for (int i = 0; i < handler.EmeraldSpawners.size(); i++) {
            Vector v = handler.EmeraldSpawners.get(i);
            this.EmeraldSpawners.add(new float[] {(float)v.getX(), (float)v.getY(), (float)v.getZ()});
        }
        
        this.TeamSpawns.clear();
        for (int i = 0; i < handler.TeamSpawns.size(); i++) {
            if (handler.TeamSpawns.isEmpty()) break;
            TeamObjectData<DirectionVector> v = handler.TeamSpawns.get(i);
            this.TeamSpawns.add(new float[] {v.team, v.data.pos.getBlockX(), v.data.pos.getBlockY(), v.data.pos.getBlockZ(), v.data.pitch, v.data.yaw});
        }
        this.TeamBeds.clear();
        for (int i = 0; i < handler.TeamBeds.size(); i++) {
            if (handler.TeamBeds.isEmpty()) break;
            TeamObjectData<BedData> v = handler.TeamBeds.get(i);
            BedSaveData bed = new BedSaveData();
            bed.color = v.data.color;
            bed.facing = v.data.facing;
            bed.pos = new int[] {v.data.pos.getBlockX(), v.data.pos.getBlockY(), v.data.pos.getBlockZ()};
            bed.team = v.team;
            bed.id = v.data.id;
            this.TeamBeds.add(new Gson().toJson(bed));
        }
        this.IronSpawners.clear();
        for (int i = 0; i < handler.IronSpawners.size(); i++) {
            TeamObjectData<Vector> v = handler.IronSpawners.get(i);
            this.IronSpawners.add(new float[] {v.team, (float)v.data.getX(), (float)v.data.getY(), (float)v.data.getZ()});
        }
        this.Teams = handler.Teams;
    }
    
}
