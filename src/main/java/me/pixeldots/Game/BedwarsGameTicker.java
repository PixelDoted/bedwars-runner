package me.pixeldots.Game;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.Utils.PlayerUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class BedwarsGameTicker extends BukkitRunnable {

    @Override
    public void run() {
        long currentTime = Utils.getDateTime();
        if (BedwarsRunner.isStarting) {
            if (BedwarsRunner.world.getPlayerCount() < BedwarsConf.requiredPlayersToStart) {
                BedwarsRunner.StartingTime += currentTime-BedwarsRunner.StartingTime;
                return;
            }
            if (currentTime+(10*1000) >= BedwarsRunner.StartingTime && currentTime+(11*1000) < BedwarsRunner.StartingTime) {
                List<Player> players = BedwarsRunner.world.getPlayers();
                for (int i = 0; i < players.size(); i++) {
                    PlayerUtils.sendTitle(players.get(i), "10", "", TextColor.color(255, 255, 255), TextColor.color(255, 255, 255));
                }
            } else if (currentTime+(5*1000) >= BedwarsRunner.StartingTime) {
                List<Player> players = BedwarsRunner.world.getPlayers();
                for (int i = 0; i < players.size(); i++) {
                    PlayerUtils.sendTitle(players.get(i), (BedwarsRunner.StartingTime-currentTime)/1000+1 + "", "", TextColor.color(255, 255, 255), TextColor.color(255, 255, 255));
                }
            }
            if (currentTime > BedwarsRunner.StartingTime) {
                BedwarsRunner.isStarting = false;
                BedwarsRunner.StartingTime = 0;
                BedwarsGame.run();
            }
        }

        if (!(BedwarsRunner.isRunning && BedwarsRunner.world != null)) return;
        try {
            BedwarsGame.SpawnItems(currentTime);
            BedwarsGame.TickPlayers(currentTime);
            BedwarsGame.TickExtras(currentTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
