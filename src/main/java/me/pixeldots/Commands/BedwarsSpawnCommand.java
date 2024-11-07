package me.pixeldots.Commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Extras.PopUpTowerData;
import me.pixeldots.Utils.BlockUtils;
import me.pixeldots.Utils.NPCUtils;
import me.pixeldots.Utils.ToolUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class BedwarsSpawnCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String raw, @NotNull String[] args) {
        if (sender instanceof Player) {
            String entityID = args[0];
            Player player = (Player)sender;

            int count = 1;
            if (args.length >= 2) count = Integer.parseInt(args[1]);
            if (entityID.equalsIgnoreCase("dreamdefender")) {
                for (int i = 0; i < count; i++) {
                    NPCUtils.spawnDreamDefender(player.getLocation(), player);
                }
            } else if (entityID.equalsIgnoreCase("bedbug")) {
                for (int i = 0; i < count; i++) {
                    NPCUtils.spawnBedBug(player.getLocation(), player);
                }
            } else if (entityID.equalsIgnoreCase("compactpopuptower")) {
                int TeamID = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team;
                Location location = player.getLocation();
                Vector pos = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());

                BlockFace facing = player.getFacing();
                PopUpTowerData PopUpTower = new PopUpTowerData(facing, TeamID, pos);
                BedwarsRunner.Variables.PopUpTowers.add(PopUpTower);
            } else if (entityID.equalsIgnoreCase("dragon")) {
                //int TeamID = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team;
                NPCUtils.spawnEnderDragon(BlockUtils.getVector(player.getLocation()), -1);
            } else if (entityID.equalsIgnoreCase("itemshop")) {
                NPCUtils.spawnItemShop(player.getLocation());
            } else if (entityID.equalsIgnoreCase("upgradeshop")) {
                NPCUtils.spawnUpgradeShop(player.getLocation());
            } else if (entityID.equalsIgnoreCase("generatoriron")) {
                for (int i = 0; i < count; i++) {
                    BedwarsRunner.world.dropItem(player.getLocation(), ToolUtils.setUnbreakable(new ItemStack(Material.IRON_INGOT))); // drop iron
                }
            } else if (entityID.equalsIgnoreCase("generatorgold")) {
                for (int i = 0; i < count; i++) {
                    BedwarsRunner.world.dropItem(player.getLocation(), ToolUtils.setUnbreakable(new ItemStack(Material.GOLD_INGOT))); // drop gold
                }
            }
            sender.sendMessage(Utils.text("Spawned entity " + entityID));
        } else 
            sender.sendMessage(Utils.text("You have to be a player to use this command", TextColor.color(255, 0, 0)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String current, @NotNull String[] args) {
        List<String> autoCompletions = new ArrayList<>();
        if (args.length < 1) return null;
        
        String[] s = new String[] {"dreamdefender", "bedbug", "compactpopuptower", "dragon", "itemshop", "upgradeshop", "generatoriron", "generatorgold"};
        for (int i = 0; i < s.length; i++) {
            if (s[i].toLowerCase().startsWith(args[0].toLowerCase())) autoCompletions.add(s[i]);
        }
        return autoCompletions;
    }
    
}
