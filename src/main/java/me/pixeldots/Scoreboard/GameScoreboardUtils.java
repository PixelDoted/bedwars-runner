package me.pixeldots.Scoreboard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Game.data.PlayerStatistics;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.TextUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class GameScoreboardUtils {

    public static int teamPosDifference = -1;
    public static int timerPosDifference = -1;
    public static int statisticsPosDifferenceOLD = -1;

    public static void Register() {
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            BuildScoreboard builder = new BuildScoreboard(Utils.text(ChatColor.BOLD + "BED WARS", TextColor.color(255, 255, 0)));
            UpdateSideboard(player, builder);

            {
                Scoreboard scoreboard = builder.build().scoreboard();

                Objective BelowNameBoard = scoreboard.registerNewObjective("nameHealth", "health", Utils.text("Health"));
                BelowNameBoard.setDisplaySlot(DisplaySlot.BELOW_NAME);

                Objective ListBoard = scoreboard.registerNewObjective("listHealth", "health", Utils.text("Bedwars"));
                ListBoard.setDisplaySlot(DisplaySlot.PLAYER_LIST);

                builder.scoreboard(scoreboard);
            }

            ScoreboardUtils.Sidebar.put(player.getUniqueId(), builder.objective);
            builder.send(player);
        }
    }
    
    public static void UpdateStatistics(Player player) {
        if (BedwarsRunner.Variables.Teams.size() > 6) return;
        int kills = statisticsPosDifferenceOLD;
        int finalkills = statisticsPosDifferenceOLD+1;
        int bedsBroken = statisticsPosDifferenceOLD+2;

        PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId());
        Objective sidebar = ScoreboardUtils.Sidebar.get(player.getUniqueId());
        BuildScoreboard.replaceScore(sidebar, kills, "Kills: " + ChatColor.GREEN + stats.kills);
        BuildScoreboard.replaceScore(sidebar, finalkills, "Final Kills: " + ChatColor.GREEN + stats.finalKills);
        BuildScoreboard.replaceScore(sidebar, bedsBroken, "Beds Broken: " + ChatColor.GREEN + stats.bedsBroken);
    }
    
    public static void UpdateTimer() {
        String time = Utils.formatTimer(BedwarsRunner.Variables.WorldInfo.nextEventTime-Utils.getDateTime());
 
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Objective sidebar = ScoreboardUtils.Sidebar.get(players.get(i).getUniqueId());
            BuildScoreboard.replaceScore(sidebar, timerPosDifference, BedwarsRunner.Variables.WorldInfo.worldEventName + " in " + ChatColor.GREEN + time);
       }
    }
    public static void UpdateTeamsboard() {
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            UUID uuid = players.get(i).getUniqueId();
            if (!BedwarsRunner.Variables.PlayerStats.containsKey(uuid)) continue;
            
            int playerTeam = BedwarsRunner.Variables.PlayerStats.get(uuid).team;
            for (int j = BedwarsRunner.Variables.Teams.size()-1; j >= 0; j--) {
                String color = BedwarsRunner.Variables.Teams.get(j);
                String name = "" + ChatColor.BOLD + Utils.getChatColor(color) + TextUtils.getStringIndex(color, 0) + ChatColor.RESET + " " + TextUtils.upperCaseFirst(color) + ": ";
                String value = "";
                
                if (!BedwarsRunner.Variables.canTeamRespawn(j)) {
                    if (TeamUtils.isTeamAlive(j)) {
                        int playerCount = 0;
                        for (int k = 0; k < BedwarsRunner.Variables.PlayersInGame.size(); k++) {
                            PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(BedwarsRunner.Variables.PlayersInGame.get(k));
                            if (stats.isDead != null && !stats.isDead.hasBed) continue;
                            if (stats.team == j)
                                playerCount++;
                        }
                        value = ChatColor.GREEN + "" + playerCount;
                    } else value = ChatColor.RED + "X";
                } else {
                    value = ChatColor.GREEN + Character.toString('\u2714');//"🗸";
                }
                BuildScoreboard.replaceScore(ScoreboardUtils.Sidebar.get(uuid), (BedwarsRunner.Variables.Teams.size()-j-1)+teamPosDifference, name + value + (playerTeam == j ? ChatColor.GRAY + " YOU" : ""));
            }
        }
    }
    
    public static void UpdateSideboard(Player player, BuildScoreboard builder) {
        UUID uuid = player.getUniqueId();

        builder.add(ChatColor.YELLOW + Bukkit.getIp());
        builder.blankLine();

        if (BedwarsRunner.Variables.Teams.size() <= 6) {
            PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(uuid);
            builder.add("Kills: " + ChatColor.GREEN + stats.kills);
            builder.add("Final Kills: " + ChatColor.GREEN + stats.finalKills);
            builder.add("Beds Broken: " + ChatColor.GREEN + stats.bedsBroken);
            builder.blankLine();
            statisticsPosDifferenceOLD = builder.lines.size()-4;
            teamPosDifference = 6;
        } else teamPosDifference = 2;

        generateTeams(builder, uuid);

        builder.blankLine();

        String time = Utils.formatTimer(BedwarsRunner.Variables.WorldInfo.nextEventTime-Utils.getDateTime());
        builder.add(BedwarsRunner.Variables.WorldInfo.worldEventName + " in " + ChatColor.GREEN + time);
        timerPosDifference = builder.lines.size()-1;

        builder.blankLine();
        builder.add(ChatColor.GRAY + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + ChatColor.DARK_GRAY + " m000A");
    }

    public static void generateTeams(BuildScoreboard builder, UUID uuid) {
        int playerTeam = BedwarsRunner.Variables.PlayerStats.get(uuid).team;
        for (int i = BedwarsRunner.Variables.Teams.size()-1; i >= 0; i--) {
            String color = BedwarsRunner.Variables.Teams.get(i);
            String name = "" + ChatColor.BOLD + Utils.getChatColor(color) + TextUtils.getStringIndex(color, 0) + ChatColor.RESET + " " + TextUtils.upperCaseFirst(color) + ": ";
            String value = "";
            
            if (!BedwarsRunner.Variables.canTeamRespawn(i)) {
                if (TeamUtils.isTeamAlive(i)) {
                    int players = 0;
                    for (int j = 0; j < BedwarsRunner.Variables.PlayersInGame.size(); j++) {
                        if (BedwarsRunner.Variables.PlayerStats.get(BedwarsRunner.Variables.PlayersInGame.get(j)).team == i)
                            players++;
                    }
                    value = ChatColor.GREEN + "" + players;
                } else value = ChatColor.RED + "X";
            } else {
                value = ChatColor.GREEN + Character.toString('\u2714');//"🗸";
            }

            builder.add(name + value + (playerTeam == i ? ChatColor.GRAY + " YOU" : ""));
        }
    }

}
