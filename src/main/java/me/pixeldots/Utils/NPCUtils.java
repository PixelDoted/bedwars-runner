package me.pixeldots.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.Extras.BedBugEntity;
import me.pixeldots.Extras.DreamDefenderEntity;
import me.pixeldots.Extras.EnderDragonEntity;
import me.pixeldots.Game.BedwarsGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class NPCUtils {

    public static ArmorStand[] createHologram(Vector pos, World world, Component[] text) {
        Location location = new Location(world, pos.getX(), pos.getY(), pos.getZ());
        ArmorStand[] holograms = new ArmorStand[text.length];
        for (int i = 0; i < text.length; i++) {
            ArmorStand hologram = (ArmorStand)world.spawnEntity(location.subtract(0, .3f, 0), EntityType.ARMOR_STAND);
            hologram.setInvisible(true);
            hologram.customName(text[i]);
            hologram.setCustomNameVisible(true);
            hologram.setInvulnerable(true);
            hologram.setGravity(false);
            holograms[i] = hologram;
        }
        return holograms;
    }

    public static void createDiamondHologram(Vector vector, World world) {
        Component line1 = Utils.text("Tier 1");
        Component line2 = Utils.text("Diamond", TextColor.color(0, 0, 188));
        Component line3 = Utils.text("Insert Timer Here", TextColor.color(255, 255, 0));
        Vector pos = new Vector(vector.getX(), vector.getY()+4, vector.getZ());

        ArmorStand[] holograms = createHologram(pos, world, new Component[] { line1, line2, line3 });
        holograms[2].setItem(EquipmentSlot.HEAD, new ItemStack(Material.DIAMOND_BLOCK));
        BedwarsRunner.Variables.HologramEntitys.add(holograms);
    }
    
    public static void createEmeraldHologram(Vector vector, World world) {
        Component line1 = Utils.text("Tier 1");
        Component line2 = Utils.text("Emerald", TextColor.color(0, 188, 0));
        Component line3 = Utils.text("Insert Timer Here", TextColor.color(255, 255, 0));
        Vector pos = new Vector(vector.getX(), vector.getY()+4, vector.getZ());

        ArmorStand[] holograms = createHologram(pos, world, new Component[] { line1, line2, line3 });
        holograms[2].setItem(EquipmentSlot.HEAD, new ItemStack(Material.EMERALD_BLOCK));
        BedwarsRunner.Variables.HologramEntitys.add(holograms);
    }

    public static void updateDiamondHologram(ArmorStand[] holograms) {
        holograms[0].customName(Utils.text("Tier " + BedwarsRunner.Variables.WorldInfo.diamondLevel));
        Component spawnsIn = Utils.text("Spawns in ", TextColor.color(255, 255, 0));
        Component time = Utils.text("" + Math.round(((BedwarsGame.LastDiamondSpawn-Utils.getDateTime())/1000)+1), TextColor.color(255, 0, 0));
        Component seconds = Utils.text(" seconds!", TextColor.color(255, 255, 0));
        holograms[2].customName(spawnsIn.append(time).append(seconds));
    } 

    public static void updateEmeraldHologram(ArmorStand[] holograms) {
        holograms[0].customName(Utils.text("Tier " + BedwarsRunner.Variables.WorldInfo.emeraldLevel));
        Component spawnsIn = Utils.text("Spawns in ", TextColor.color(255, 255, 0));
        Component time = Utils.text("" + Math.round(((BedwarsGame.LastEmeraldSpawn-Utils.getDateTime())/1000)+1), TextColor.color(255, 0, 0));
        Component seconds = Utils.text(" seconds!", TextColor.color(255, 255, 0));
        holograms[2].customName(spawnsIn.append(time).append(seconds));
    }

    public static DreamDefenderEntity spawnDreamDefender(Location location, Player player) {
        IronGolem golem = (IronGolem)BedwarsRunner.world.spawnEntity(location, EntityType.IRON_GOLEM);
        int playerTeam = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team;
        golem.customName(Utils.text("Dream Defender", Utils.getTextColor(BedwarsRunner.Variables.Teams.get(playerTeam))));
        golem.setCustomNameVisible(true);

        DreamDefenderEntity dreamDefender = new DreamDefenderEntity();
        dreamDefender.entity = golem;
        dreamDefender.spawnTime = Utils.getDateTime()+Utils.toMillisecondTime(BedwarsConf.dreamDefenderDuration);
        dreamDefender.teamID = playerTeam;
        BedwarsRunner.Variables.DreamDefenders.add(dreamDefender);
        return dreamDefender;
    }

    public static BedBugEntity spawnBedBug(Location location, Player player) {
        Silverfish silverFish = (Silverfish)player.getWorld().spawnEntity(location, EntityType.SILVERFISH);
        BedBugEntity bedBug = new BedBugEntity();
        bedBug.entity = silverFish;
        bedBug.spawnTime = Utils.getDateTime()+Utils.toMillisecondTime(BedwarsConf.bedBugDuration);
        bedBug.teamID = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team;

        bedBug.entity.customName(Utils.text("Bed Bug", Utils.getTextColor(BedwarsRunner.Variables.Teams.get(bedBug.teamID))));
        bedBug.entity.setCustomNameVisible(true);
        BedwarsRunner.Variables.BedBugs.add(bedBug);
        return bedBug;
    }

    public static EnderDragon spawnEnderDragon(Vector pos, int teamID) {
        String teamColor = teamID == -1 ? "white" : BedwarsRunner.Variables.Teams.get(teamID);
        Location location = new Location(BedwarsRunner.world, pos.getX(), pos.getY(), pos.getZ());
        EnderDragon dragon = (EnderDragon)BedwarsRunner.world.spawnEntity(location, EntityType.ENDER_DRAGON);

        dragon.customName(Utils.text("LOOK IT'S A DRAGON, AHHH...!", Utils.getTextColor(teamColor)));
        dragon.setCustomNameVisible(true);
        
        int playerID = Utils.randomRange(0, BedwarsRunner.world.getPlayerCount()-1);
        Player player = BedwarsRunner.world.getPlayers().get(playerID);
        dragon.setPhase(Phase.CHARGE_PLAYER);
        dragon.setTarget(player);

        EnderDragonEntity data = new EnderDragonEntity();
        data.entity = dragon;
        data.teamID = teamID;
        BedwarsRunner.Variables.EnderDragons.add(data);
        return dragon;
    }

    public static void spawnItemShop(Location location) {
        Villager npc = (Villager)location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        npc.customName(Utils.text("Item Shop", TextColor.color(255, 255, 0)));
        npc.setCustomNameVisible(true);
        npc.setInvulnerable(true);
        npc.setAI(false);
    }

    public static void spawnUpgradeShop(Location location) {
        Villager npc = (Villager)location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        npc.customName(Utils.text("Upgrade Shop", TextColor.color(255, 255, 0)));
        npc.setCustomNameVisible(true);
        npc.setInvulnerable(true);
        npc.setAI(false);
    }

}
