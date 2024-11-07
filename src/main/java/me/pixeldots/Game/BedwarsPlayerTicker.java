package me.pixeldots.Game;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.Extras.DreamDefenderEntity;
import me.pixeldots.Game.data.PlayerStatistics;
import me.pixeldots.Game.data.TeamUpgrades.TeamTraps;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class BedwarsPlayerTicker {
    
    public static void updateDreamDefenderTarget(float currentTime, Player player, PlayerStatistics stats) {
        int TeamID = stats.team;
        for (int j = BedwarsRunner.Variables.DreamDefenders.size()-1; j >= 0; j--) { // Dream Defender Handler
            DreamDefenderEntity dreamDefender = BedwarsRunner.Variables.DreamDefenders.get(j); // get a dream defender
            if (currentTime >= dreamDefender.spawnTime) { // check if the dream defender ran out of time
                dreamDefender.entity.remove(); // remove the dream defender
                BedwarsRunner.Variables.DreamDefenders.remove(dreamDefender); // remove the dream defender from the DreamDefenders list
            } else {
                int teamID = dreamDefender.teamID; // get the dream defenders teamID
                float timer = Math.round((dreamDefender.spawnTime-currentTime)/100f)/10f; // get the dream defenders time left
                dreamDefender.entity.customName( Utils.text("Dream Defender [ " + timer + "s ]", Utils.getTextColor(BedwarsRunner.Variables.Teams.get(teamID))) ); // update the dream defenders name
            }
            if (dreamDefender.teamID == TeamID) continue; // ignore the rest of this code if the dream defender is on the players team

            Location location = dreamDefender.entity.getLocation(); // get the dream defenders location
            if (location.distance(player.getLocation()) <= BedwarsConf.dreamDefenderTargetDistance) { // check if the player is in range of the dream defender
                dreamDefender.entity.setTarget(player); // target the player
            }
        }
    }

    public static void updateTrapTrigger(float currentTime, Player player, PlayerStatistics stats) {
        int TeamID = stats.team;
        if (currentTime >= stats.magicMilkTime) { // Magic Milk/Trap Handler
            for (int j = 0; j < BedwarsRunner.Variables.Teams.size(); j++) {
                if (j == TeamID) continue; // ignore the trap if you are on that team
                List<BedData> beds = BedwarsRunner.Variables.getTeamBeds(j); // get all of the beds on that team
                for (int k = 0; k < beds.size(); k++) {
                    Vector pos = beds.get(k).pos; // get one of the beds position
                    Location bedLocation = new Location(BedwarsRunner.world, pos.getX(), pos.getY(), pos.getZ()); // create Location from the position
                    if (player.getLocation().distance(bedLocation) <= BedwarsConf.teamTrapDistance) { // check if the player is in range
                        if (TeamUtils.getTeamUpgrades(j).Traps[0] == TeamTraps.None) continue; // don't trigger if there are no traps
                        TeamUtils.triggerTeamTrap(j, player); // trigger the trap
                    }
                }
            }
        }
    }

    public static void updatePlayerTracker(Player player, PlayerStatistics stats) {
        if (stats.trackingTeam != -1) {
            Player closestPlayer = null;
            double closestDistance = -1;

            boolean isTeamAlive = false;
            for (int i = 0; i < BedwarsRunner.Variables.PlayersInGame.size(); i++) {
                UUID uuid = BedwarsRunner.Variables.PlayersInGame.get(i);
                PlayerStatistics trackedStats = BedwarsRunner.Variables.PlayerStats.get(uuid);
                if (trackedStats.team != stats.trackingTeam) continue;

                Player tracked = Bukkit.getPlayer(uuid);
                double dist = tracked.getLocation().distance(player.getLocation());
                if (trackedStats.isDead == null) {
                    isTeamAlive = true;
                    if (closestDistance == -1 || dist < closestDistance) {
                        closestDistance = dist;
                        closestPlayer = tracked;
                    }
                }
            }
            if (!isTeamAlive) { 
                stats.trackingTeam = -1;
                player.setCompassTarget(player.getWorld().getSpawnLocation());
            }

            if (closestPlayer != null) {
                player.sendActionBar(Utils.text("Tracking: ").append(Utils.text(Utils.text(player.displayName()), Utils.getTextColor(Utils.getTeamColor(stats.trackingTeam)))).append(Utils.text(" - Distance: ")).append(Utils.text(Math.floor(closestDistance) + "m", TextColor.color(0, 255, 0))));
                player.setCompassTarget(closestPlayer.getLocation());
            }
        }
    }

}
