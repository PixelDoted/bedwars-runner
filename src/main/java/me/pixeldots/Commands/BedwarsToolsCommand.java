package me.pixeldots.Commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.API.APIUtils;
import me.pixeldots.Utils.TeamUtils;

public class BedwarsToolsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String raw, @NotNull String[] args) {
        if (args[0].equalsIgnoreCase("config")) {
            if (args[1].equalsIgnoreCase("reload")) {
                BedwarsConf.loadConf(BedwarsRunner.instance);
            }
        } else if (args[0].equalsIgnoreCase("skipEvent")) {
            BedwarsRunner.Variables.WorldInfo.nextWorldEvent();
        } else if (args[0].equalsIgnoreCase("breakBed")) {
            int teamID = BedwarsRunner.Variables.Teams.indexOf(args[1]);
            for (int i = 0; i < BedwarsRunner.Variables.getTeamBeds(teamID).size(); i++) {
                TeamUtils.breakTeamBed(teamID, i);
            }
        } else if (args[0].equalsIgnoreCase("trackTeam")) {
            String team = args[1];
            Player player = (Player)sender;
            
            if (BedwarsRunner.Variables.PlayerStats.containsKey(player.getUniqueId()))
                BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).trackingTeam = BedwarsRunner.Variables.Teams.indexOf(team);
        } else if (args[0].equalsIgnoreCase("addPlayerToTeam")) {
            String team = args[1];
            String player = args[2];
            APIUtils.setPlayerTeamID(Bukkit.getPlayer(player).getUniqueId(), BedwarsRunner.Variables.Teams.indexOf(team));
            sender.sendMessage("Successfully set " + player + "'s team to " + team);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String current, @NotNull String[] args) {
        List<String> autoCompletions = new ArrayList<>();
        if (args.length < 1) return null;
        if (args[0].equalsIgnoreCase("breakbed") || args[0].equalsIgnoreCase("trackteam") || args[0].equalsIgnoreCase("addplayertoteam")) {
            if (args.length < 2) return null;
            List<String> teams = BedwarsRunner.Variables.Teams;
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).toLowerCase().startsWith(current.toLowerCase())) autoCompletions.add(teams.get(i));
            }
        } else if (args[0].equalsIgnoreCase("config")) {
            if (args.length < 2) return null;
            String[] s = new String[] {"reload"};
            for (int i = 0; i < s.length; i++) {
                if (s[i].toLowerCase().startsWith(args[1].toLowerCase())) autoCompletions.add(s[i]);
            }
        } else {
            String[] s = new String[] {"config", "skipevent", "breakbed", "trackteam", "addplayertoteam"};
            for (int i = 0; i < s.length; i++) {
                if (s[i].toLowerCase().startsWith(args[0].toLowerCase())) autoCompletions.add(s[i]);
            }
        }
        return autoCompletions;
    }
    
}
