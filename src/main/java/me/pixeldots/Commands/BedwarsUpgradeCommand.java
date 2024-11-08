package me.pixeldots.Commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.TextUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class BedwarsUpgradeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String raw, @NotNull String[] args) {
        if (sender instanceof Player) {
            String teamName = args[0];
            String upgradeType = args[1];
            int team = BedwarsRunner.Variables.Teams.indexOf(teamName);
            if (upgradeType.equalsIgnoreCase("ironforge")) {
                if (TeamUtils.getTeamUpgrades(team).ForgeLevel < 4) {
                    TeamUtils.getTeamUpgrades(team).ForgeLevel++;
                    sender.sendMessage(Utils.text("Successfully upgraded Iron Forge for ").append(Utils.text(TextUtils.upperCaseFirst(teamName), Utils.getTextColor(teamName))));
                } else sender.sendMessage(Utils.text("Failed to upgrade Iron Forge for ").append(Utils.text(TextUtils.upperCaseFirst(teamName), Utils.getTextColor(teamName))));
            } else if (upgradeType.equalsIgnoreCase("sharpness")) {
                if (TeamUtils.getTeamUpgrades(team).SharpnessLevel == 0) {
                    TeamUtils.upgradeSharpness(team);
                    sender.sendMessage(Utils.text("Successfully upgraded Sharpness for ").append(Utils.text(TextUtils.upperCaseFirst(teamName), Utils.getTextColor(teamName))));
                } else sender.sendMessage(Utils.text("Failed to upgrade Sharpness for ").append(Utils.text(TextUtils.upperCaseFirst(teamName), Utils.getTextColor(teamName))));
            } else if (upgradeType.equalsIgnoreCase("protection")) {
                if (TeamUtils.getTeamUpgrades(team).ProtectionLevel < 4) {
                    TeamUtils.upgradeProtection(team);
                    sender.sendMessage(Utils.text("Successfully upgraded Protection for ").append(Utils.text(TextUtils.upperCaseFirst(teamName), Utils.getTextColor(teamName))));
                } else sender.sendMessage(Utils.text("Failed to upgrade Protection for ").append(Utils.text(TextUtils.upperCaseFirst(teamName), Utils.getTextColor(teamName))));
            }
        } else 
            sender.sendMessage(Utils.text("You have to be a player to use this command", TextColor.color(255, 0, 0)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String current, @NotNull String[] args) {
        List<String> autoCompletions = new ArrayList<>();
        List<String> teams = BedwarsRunner.Variables.Teams;
        if (args.length < 1) return null;

        if (teams.contains(args[0])) {
            if (args.length < 2) return null;
            String[] s = new String[] {"ironforge", "sharpness", "protection"};
            for (int i = 0; i < s.length; i++) {
                if (s[i].toLowerCase().startsWith(args[1].toLowerCase())) autoCompletions.add(s[i]);
            }
        } else {
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).toLowerCase().startsWith(args[0].toLowerCase())) autoCompletions.add(teams.get(i));
            }
        }
        return autoCompletions;
    }
    
}
