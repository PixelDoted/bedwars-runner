package me.pixeldots;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BedwarsConf {
    
    // config
    public static int requiredPlayersToStart = 0;
    public static boolean autoStartWithServer = false;
    public static String autoStartDelay = "5s";
    public static String mapName = "<Map Name>";
    public static String modeName = "<Mode Name>";
    
    public static boolean canDestroyWorld = false;
    public static boolean friendlyFire = false;

    public static int healPoolDistance = 15;
    public static int teamTrapDistance = 10;
    public static int respawnTime = 5;
    public static int playerPVPTime = 5;
    public static int dreamDefenderTargetDistance = 15;
    public static int dreamDefenderBaseDamage = 2;

    public static String magicMilkDuration = "30s";
    public static String dreamDefenderAttackedMultiplyer = "10s";
    public static String dreamDefenderDuration = "200s";
    public static String bedBugDuration = "15s";

    public static boolean teamGeneratorItemsHaveVelocity = true;
    public static int TeamGeneratorMaxIronCount = 64;
    public static int TeamGeneratorMaxGoldCount = 16;
    public static int TeamGeneratorMaxEmeraldCount = 2;
    public static int GeneratorMaxDiamondCount = 4;
    public static int GeneratorMaxEmeraldCount = 2;
    public static String ironGenTime = "1.5s";
    public static String diamondGenTime = "30s";
    public static String emeraldGenTime = "60s";

    public static int maxTeamProtection = 4;
    public static int maxTeamSharpness = 1;
    public static int maxTeamForgeLevel = 4;
    public static int maxTeamHasteLevel = 2;
    public static int maxTeamDragonBuffs = 1;
    public static int maxTeamHealPoolLevel = 1;

    public static FileConfiguration config;
    // config

    public static void loadConf(JavaPlugin plugin) {
        config = plugin.getConfig();
        
        config.addDefault("requiredPlayersToStart", requiredPlayersToStart);
        config.addDefault("autoStartWithServer", autoStartWithServer);
        config.addDefault("autoStartDelay", autoStartDelay);
        config.addDefault("mapName", mapName);
        config.addDefault("modeName", modeName);

        config.addDefault("canDestroyWorld", canDestroyWorld);

        config.addDefault("healPoolDistance", healPoolDistance);
        config.addDefault("teamTrapDistance", teamTrapDistance);
        config.addDefault("respawnTime", respawnTime);
        config.addDefault("dreamDefenderTargetDistance", dreamDefenderTargetDistance);
        config.addDefault("dreamDefenderBaseDamage", dreamDefenderBaseDamage);

        config.addDefault("magicMilkDuration", magicMilkDuration);
        config.addDefault("dreamDefenderAttackedMultiplyer", dreamDefenderAttackedMultiplyer);
        config.addDefault("dreamDefenderDuration", dreamDefenderDuration);
        config.addDefault("bedBugDuration", bedBugDuration);

        config.addDefault("teamGeneratorItemsHaveVelocity", teamGeneratorItemsHaveVelocity);
        config.addDefault("teamGeneratorMaxIronCount", TeamGeneratorMaxIronCount);
        config.addDefault("teamGeneratorMaxGoldCount", TeamGeneratorMaxGoldCount);
        config.addDefault("teamGeneratorMaxEmeraldCount", TeamGeneratorMaxEmeraldCount);
        config.addDefault("generatorMaxDiamondCount", GeneratorMaxDiamondCount);
        config.addDefault("generatorMaxEmeraldCount", GeneratorMaxEmeraldCount);

        config.addDefault("ironGeneratorTime", ironGenTime);
        config.addDefault("diamondGeneratorTime", diamondGenTime);
        config.addDefault("emeraldGeneratorTime", emeraldGenTime);
        plugin.saveConfig();

        requiredPlayersToStart = config.getInt("requiredPlayersToStart");
        autoStartWithServer = config.getBoolean("autoStartWithServer");
        autoStartDelay = config.getString("autoStartDelay");
        mapName = config.getString("mapName");
        modeName = config.getString("modeName");

        canDestroyWorld = config.getBoolean("canDestroyWorld");

        healPoolDistance = config.getInt("healPoolDistance");
        teamTrapDistance = config.getInt("teamTrapDistance");
        respawnTime = config.getInt("respawnTime");
        dreamDefenderTargetDistance = config.getInt("dreamDefenderTargetDistance");
        dreamDefenderBaseDamage = config.getInt("dreamDefenderBaseDamage");

        magicMilkDuration = config.getString("magicMilkDuration");
        dreamDefenderAttackedMultiplyer = config.getString("dreamDefenderAttackedMultiplyer");
        dreamDefenderDuration = config.getString("dreamDefenderDuration");
        bedBugDuration = config.getString("bedBugDuration");

        teamGeneratorItemsHaveVelocity = config.getBoolean("teamGeneratorItemsHaveVelocity");
        TeamGeneratorMaxIronCount = config.getInt("teamGeneratorMaxIronCount");
        TeamGeneratorMaxGoldCount = config.getInt("teamGeneratorMaxGoldCount");
        TeamGeneratorMaxEmeraldCount = config.getInt("teamGeneratorMaxEmeraldCount");
        GeneratorMaxDiamondCount = config.getInt("generatorMaxDiamondCount");
        GeneratorMaxEmeraldCount = config.getInt("generatorMaxEmeraldCount");

        ironGenTime = config.getString("ironGeneratorTime");
        diamondGenTime = config.getString("diamondGeneratorTime");
        emeraldGenTime = config.getString("emeraldGeneratorTime");
    }

}
