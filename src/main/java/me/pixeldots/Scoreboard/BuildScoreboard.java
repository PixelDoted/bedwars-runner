package me.pixeldots.Scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.Component;

public class BuildScoreboard {
    
    public List<String> lines = new ArrayList<>();

    public Component title;
    public Scoreboard scoreboard;
    public Objective objective;

    public int blankLineIndex = 0;

    public BuildScoreboard(String _title) {
        this.title = Utils.text(_title);
    }
    public BuildScoreboard(Component _title) {
        this.title = _title;
    }

    public void add(String score) {
        lines.add(score);
    }

    public void blankLine() {
        lines.add(ChatColor.values()[blankLineIndex] + "");
        blankLineIndex++;
    }

    public BuildScoreboard build() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("board", "dummy", title);
        objective.displayName(title);

        for (int i = 0; i < lines.size(); i++) {
            objective.getScore(lines.get(i)).setScore(i);
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        return this;
    }

    public void send(Player player) {
        player.setScoreboard(scoreboard);
    }

    public Scoreboard scoreboard() {
        return scoreboard;
    }
    public void scoreboard(Scoreboard _scoreboard) {
        this.scoreboard = _scoreboard;
    }

    // Credit: https://www.spigotmc.org/threads/scoreboard-flickering.213720/
    public static String getEntryFromScore(Objective objective, int score) {
        if (objective == null) return null;
        if (!hasScoreTaken(objective, score)) return null;
        for (String s : objective.getScoreboard().getEntries()) {
            if (objective.getScore(s).getScore() == score) return objective.getScore(s).getEntry();
        }
        return null;
    }
    
    public static boolean hasScoreTaken(Objective objective, int score) {
        for (String s : objective.getScoreboard().getEntries()) {
            if(objective.getScore(s).getScore() == score) return true;
        }
        return false;
    }
    
    public static void replaceScore(Objective objective, int score, String name) {
        if (hasScoreTaken(objective, score)) {
            if(getEntryFromScore(objective, score).equalsIgnoreCase(name)) return;
            if(!(getEntryFromScore(objective, score).equalsIgnoreCase(name))) { 
                String entry = getEntryFromScore(objective, score);
                if (entry != null) objective.getScoreboard().resetScores(entry);
            }
        }
        objective.getScore(name).setScore(score);
    }

}
