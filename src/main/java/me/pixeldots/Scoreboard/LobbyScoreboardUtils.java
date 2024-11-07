package me.pixeldots.Scoreboard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class LobbyScoreboardUtils {

    public static void Register() {
        BuildScoreboard builder = new BuildScoreboard(Utils.text(ChatColor.BOLD + "BED WARS", TextColor.color(255, 255, 0)));
        
        builder.add(ChatColor.YELLOW + Bukkit.getIp());
        
        builder.blankLine();
        builder.add("Version: " + ChatColor.GRAY + "v1.0");
        builder.add("Mode: " + ChatColor.GREEN + BedwarsConf.modeName);

        builder.blankLine();
        builder.add("Starting in " + ChatColor.GREEN + "10s");

        builder.blankLine();
        builder.add("Players: " + ChatColor.GREEN + BedwarsRunner.world.getPlayerCount());
        builder.add("Map: " + ChatColor.GREEN + BedwarsConf.mapName);

        builder.blankLine();
        builder.add(ChatColor.GRAY + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + ChatColor.DARK_GRAY + " m000A");

        builder.build();
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            ScoreboardUtils.Sidebar.put(player.getUniqueId(), builder.objective);
            builder.send(player);
        }
    }

    public static void UpdateTimer() {
        String time = Utils.formatTimer(BedwarsRunner.StartingTime-Utils.getDateTime());

        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Objective sidebar = ScoreboardUtils.Sidebar.get(players.get(i).getUniqueId());
            BuildScoreboard.replaceScore(sidebar, 5, "Starting in " + ChatColor.GREEN + time);
        }
    }

}
