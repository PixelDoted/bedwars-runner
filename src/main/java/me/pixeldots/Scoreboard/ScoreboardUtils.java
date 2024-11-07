package me.pixeldots.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardUtils {

    public static Map<UUID, Objective> Sidebar = new HashMap<>();
    public static Scoreboard TeamBoard = null;

    public static void RegisterLobbyBoard() {
        LobbyScoreboardUtils.Register();
    }

    public static void UpdateLobbyTimer() {
        LobbyScoreboardUtils.UpdateTimer();
    }

    public static void RegisterGameBoard() {
        GameScoreboardUtils.Register();
    }

    public static void UpdateTeamsboard() {
        GameScoreboardUtils.UpdateTeamsboard();
    }

    public static void UpdateGameTimer() {
        GameScoreboardUtils.UpdateTimer();
    }

}
