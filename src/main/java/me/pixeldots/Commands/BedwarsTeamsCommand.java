package me.pixeldots.Commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Game.BedData;
import me.pixeldots.Utils.BlockUtils;
import me.pixeldots.Utils.Utils;

public class BedwarsTeamsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String raw, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player)sender;
        
        if (args[0].equalsIgnoreCase("addTeam")) {
            String team = args[1];
            if (!BedwarsRunner.Variables.Teams.contains(team)) { 
                BedwarsRunner.Variables.Teams.add(team);
                player.sendMessage(Utils.text("Successfully added ").append(Utils.text(team, Utils.getTextColor(team))).append(Utils.text("Team")));
            } else player.sendMessage(Utils.text("Failed to add Team (this Team already exsists, or is an invalid Team)"));
        } else if (args[0].equalsIgnoreCase("setTeamBed")) {
            try {
                BedwarsRunner.world = player.getWorld();
                String team = args[1];
                Location pos = player.getLocation();

                BlockState state = BedwarsRunner.world.getBlockState(new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
                if (BlockUtils.isBedBlock(state.getBlock().getType())) {
                    if (!BedwarsRunner.Variables.Teams.contains(team)) BedwarsRunner.Variables.Teams.add(team);
                    for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
                        if (!BedwarsRunner.Variables.Teams.get(i).equalsIgnoreCase(team)) continue;
                        BedData data = new BedData();
                        data.color = team;
                        data.pos = new Vector(pos.getX(), pos.getY(), pos.getZ());
                        data.facing = ((Bed)state.getBlockData()).getFacing().name();
                        data.id = BedwarsRunner.Variables.teamBedCount(i);

                        BedwarsRunner.Variables.addTeamBed(i, data);
                    }
                } else {
                    player.sendMessage("You need to stand on a bed");
                }
            } catch (Exception e) { e.printStackTrace(); }
        } else if (args[0].equalsIgnoreCase("setSpawn")) {
            String team = args[1];
            if (!BedwarsRunner.Variables.Teams.contains(team)) BedwarsRunner.Variables.Teams.add(team);
            for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
                if (!BedwarsRunner.Variables.Teams.get(i).equalsIgnoreCase(team)) continue;
                Location pos = player.getLocation();
                BedwarsRunner.Variables.addTeamSpawn(i, new Vector(pos.getX(), pos.getY(), pos.getZ()), pos.getPitch(), pos.getYaw());
            }
        } else if (args[0].equalsIgnoreCase("setSpectatorSpawn")) {
            Location pos = player.getLocation();
            BedwarsRunner.Variables.SpectatorSpawn = new Vector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        } else if (args[0].equalsIgnoreCase("teams")) {
            player.sendMessage("Teams --");
            for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
                player.sendMessage(BedwarsRunner.Variables.Teams.get(i));
            }
            player.sendMessage("-- Teams");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String current, @NotNull String[] args) {
        List<String> autoCompletions = new ArrayList<>();
        if (args.length < 1) return null;
        if (args[0].equalsIgnoreCase("addteam") || args[0].equalsIgnoreCase("setteambed") || args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setspectatorspawn")) {
            if (args.length < 2) return null;
            List<String> teams = BedwarsRunner.Variables.Teams;
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).toLowerCase().startsWith(args[1].toLowerCase())) autoCompletions.add(teams.get(i));
            }
        } else {
            String[] s = new String[] {"addteam", "setteambed", "setspawn", "setspectatorspawn", "teams"};
            for (int i = 0; i < s.length; i++) {
                if (s[i].toLowerCase().startsWith(args[0].toLowerCase())) autoCompletions.add(s[i]);
            }
        }
        return autoCompletions;
    }
    
}
