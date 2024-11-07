package me.pixeldots.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.pixeldots.BedwarsRunner;

public class BedwarsGeneratorsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String raw, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player)sender;

        Location pos = player.getLocation();
        if (args[0].equalsIgnoreCase("iron")) {
            if (BedwarsRunner.Variables.Teams.contains(args[1])) {
                BedwarsRunner.Variables.addTeamGenerator(BedwarsRunner.Variables.Teams.indexOf(args[1]), new Vector(pos.getX(), pos.getY(), pos.getZ()));
            }
        } else if (args[0].equalsIgnoreCase("diamond")) {
            BedwarsRunner.Variables.DiamondSpawners.add(new Vector(pos.getX(), pos.getY(), pos.getZ()));
        } else if (args[0].equalsIgnoreCase("emerald")) {
            BedwarsRunner.Variables.EmeraldSpawners.add(new Vector(pos.getX(), pos.getY(), pos.getZ()));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String current, @NotNull String[] args) {
        List<String> autoCompletions = new ArrayList<>();
        if (args.length < 1) return null;

        if (args[0].equalsIgnoreCase("iron")) {
            if (args.length < 2) return null;
            List<String> teams = BedwarsRunner.Variables.Teams;
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).toLowerCase().startsWith(args[1].toLowerCase())) autoCompletions.add(teams.get(i));
            }
        } else {
            String[] s = new String[] {"iron", "diamond", "emerald"};
            for (int i = 0; i < s.length; i++) {
                if (s[i].toLowerCase().startsWith(args[0].toLowerCase())) autoCompletions.add(s[i]);
            }
        }
        return autoCompletions;
    }
    
}
