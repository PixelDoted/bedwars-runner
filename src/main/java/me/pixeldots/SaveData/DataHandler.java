package me.pixeldots.SaveData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bukkit.util.Vector;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Game.BedData;
import me.pixeldots.Game.data.DirectionVector;
import me.pixeldots.Game.data.TeamObjectData;
import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Shops.data.ItemShopData;
import me.pixeldots.Shops.data.UpgradeShopData;
import me.pixeldots.Utils.Utils;

public class DataHandler {

    public static void Save(String path) {
        BufferedWriter writer = null;
        try {
            Gson gson = new Gson();
            writer = new BufferedWriter(new FileWriter(new File(path)));
            
            VariablesData data = new VariablesData(BedwarsRunner.Variables);
            writer.write(gson.toJson(data));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { writer.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void Load(String path) {
        if (!new File(path).exists()) return;
        BufferedReader reader = null;
        try {
            Gson gson = new Gson();
            reader = new BufferedReader(new FileReader(new File(path)));

            VariablesData data = gson.fromJson(reader.readLine(), VariablesData.class);

            BedwarsRunner.Variables.AreaStart = new Vector(data.AreaStart[0], data.AreaStart[1], data.AreaStart[2]);
            BedwarsRunner.Variables.AreaEnd = new Vector(data.AreaEnd[0], data.AreaEnd[1], data.AreaEnd[2]);
            BedwarsRunner.Variables.LobbyPosition = new Vector(data.LobbyPosition[0], data.LobbyPosition[1], data.LobbyPosition[2]);
            BedwarsRunner.Variables.SpectatorSpawn = new Vector(data.SpectatorSpawn[0], data.SpectatorSpawn[1], data.SpectatorSpawn[2]);

            BedwarsRunner.Variables.DiamondSpawners.clear();
            for (int i = 0; i < data.DiamondSpawners.size(); i++) {
                float[] v = data.DiamondSpawners.get(i);
                BedwarsRunner.Variables.DiamondSpawners.add(new Vector(v[0], v[1], v[2]));
            }
            BedwarsRunner.Variables.EmeraldSpawners.clear();
            for (int i = 0; i < data.EmeraldSpawners.size(); i++) {
                float[] v = data.EmeraldSpawners.get(i);
                BedwarsRunner.Variables.EmeraldSpawners.add(new Vector(v[0], v[1], v[2]));
            }
            BedwarsRunner.Variables.IronSpawners.clear();
            for (int i = 0; i < data.IronSpawners.size(); i++) {
                if (data.IronSpawners.size() <= i) break;
                float[] v = data.IronSpawners.get(i);
                BedwarsRunner.Variables.IronSpawners.add(new TeamObjectData<Vector>((int)v[0], new Vector(v[1], v[2], v[3])));
            }
            BedwarsRunner.Variables.Teams = data.Teams;
            BedwarsRunner.Variables.TeamSpawns.clear();
            for (int i = 0; i < data.TeamSpawns.size(); i++) {
                if (data.TeamSpawns.size() <= i) break;
                float[] v = data.TeamSpawns.get(i);
                BedwarsRunner.Variables.TeamSpawns.add(new TeamObjectData<DirectionVector>((int)v[0], new DirectionVector(v[1], v[2], v[3], v[4], v[5])));
            }
            BedwarsRunner.Variables.TeamBeds.clear();
            for (int i = 0; i < data.TeamBeds.size(); i++) {
                if (data.TeamBeds.size() <= i) break;
                BedSaveData v = new Gson().fromJson(data.TeamBeds.get(i), BedSaveData.class);
                BedData bed = new BedData();
                
                bed.color = v.color;
                bed.facing = v.facing;
                bed.pos = new Vector(v.pos[0], v.pos[1], v.pos[2]);
                bed.id = v.id;
                
                BedwarsRunner.Variables.TeamBeds.add(new TeamObjectData<BedData>(v.team, bed));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { reader.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void LoadItemShops() {
        Utils.Logger().info("Loading Item Shop");
        BedwarsRunner.Variables.itemShopData = LoadItemShop(BedwarsRunner.itemShopPath);
        
        Utils.Logger().info("Loading Upgrade Shop");
        BedwarsRunner.Variables.upgradeShopData = LoadUpgradeShop(BedwarsRunner.upgradeShopPath);
    }

    public static ItemShopData LoadItemShop(String path) {
        if (!Files.exists(Path.of(path))) return null;
        InputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            return ShopUtils.getItemShopFromJSON(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (input != null) try { input.close(); } catch (IOException e) {}
        }
        return null;
    }

    public static UpgradeShopData LoadUpgradeShop(String path) {
        if (!Files.exists(Path.of(path))) return null;
        InputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            return ShopUtils.getUpgradeShopFromJSON(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (input != null) try { input.close(); } catch (IOException e) {}
        }
        return null;
    }

    public static Map<Integer, String> LoadPlayerQuickBuy(UUID uuid) {
        String path = BedwarsRunner.playerQuickBuyPath.replace("%PlayerUUID%", uuid.toString());
        if (!Files.exists(Path.of(path))) return new HashMap<>();

        InputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            Type type = new TypeToken<Map<Integer, String>>(){}.getType();
            return new Gson().fromJson(new InputStreamReader(input), type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { input.close(); } catch (IOException e) {}
        }
        return new HashMap<>();
    }
    public static void SavePlayerQuickBuy(UUID uuid, Map<Integer, String> data) {
        String path = BedwarsRunner.playerQuickBuyPath.replace("%PlayerUUID%", uuid.toString());
        
        OutputStreamWriter output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(new File(path)));
            output.write(new Gson().toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) try { output.close(); } catch (IOException e) {}
        }
    }

}
