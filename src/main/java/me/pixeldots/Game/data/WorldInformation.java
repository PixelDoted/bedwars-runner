package me.pixeldots.Game.data;

import java.util.List;

import org.bukkit.entity.Player;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Utils.NPCUtils;
import me.pixeldots.Utils.PlayerUtils;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class WorldInformation {

    public int diamondLevel = 1;
    public int emeraldLevel = 1;

    public int currentWorldEvent = 0;
    public String worldEventName = "Diamond II";
    public long nextEventTime = Utils.getDateTime()+Utils.toMillisecondTime("6m");

    public void nextWorldEvent() {
        currentWorldEvent++;
        switch (currentWorldEvent) {
            case 1:
                diamondLevel++;
                worldEventName = "Emerald II";
                nextEventTime = Utils.getDateTime()+Utils.toMillisecondTime("6m");
                break;
            case 2:
                emeraldLevel++;
                worldEventName = "Diamond III";
                nextEventTime = Utils.getDateTime()+Utils.toMillisecondTime("6m");
                break;
            case 3:
                diamondLevel++;
                worldEventName = "Emerald III";
                nextEventTime = Utils.getDateTime()+Utils.toMillisecondTime("6m");
                break;
            case 4:
                emeraldLevel++;
                worldEventName = "Bed Gone";
                nextEventTime = Utils.getDateTime()+Utils.toMillisecondTime("6m");
                break;
            case 5:
                TeamUtils.breakAllBeds();
                worldEventName = "Sudden Death";
                nextEventTime = Utils.getDateTime()+Utils.toMillisecondTime("10m");
                break;
            case 6:
                spawnDragons();
                sendTitle("SUDDEN DEATH");
                worldEventName = "Game End";
                nextEventTime = Utils.getDateTime()+Utils.toMillisecondTime("10m");
                break;
            case 7:
                // Handle Game End
                sendTitle("GAME OVER");
                BedwarsRunner.endGame();
                break;
        }
    }

    public void spawnDragons() {
        int DragonCount = 0;
        for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
            TeamUpgrades upgrades = BedwarsRunner.Variables.TeamUpgrades.get(i);
            for (int j = 0; j < upgrades.DragonBuffs; j++) {
                NPCUtils.spawnEnderDragon(BedwarsRunner.Variables.SpectatorSpawn, i);
                DragonCount++;
            }
        }
        int noTeamSpawnCount = 3;
        if (DragonCount >= 8) noTeamSpawnCount = 1;
        else if (DragonCount >= 3) noTeamSpawnCount = 2;
        for (int i = 0; i < noTeamSpawnCount; i++) {
            NPCUtils.spawnEnderDragon(BedwarsRunner.Variables.SpectatorSpawn, -1);
        }
    }

    public void sendTitle(String title) {
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            PlayerUtils.sendTitle(players.get(i), title, "", TextColor.color(255, 22, 22), null);
        }
    }
    
}
