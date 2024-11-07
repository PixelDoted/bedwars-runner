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
import me.pixeldots.SaveData.DataHandler;

public class BedwarsGameCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String raw, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player)sender;

        if (args[0].equalsIgnoreCase("start")) {
            if (args.length > 1 && args[1].equalsIgnoreCase("test")) BedwarsRunner.isTesting = true;
            else BedwarsRunner.isTesting = false;

            player.sendMessage("Starting Bedwars match" + (BedwarsRunner.isTesting ? " (Testing)" : ""));
            BedwarsRunner.world = player.getWorld();
            BedwarsRunner.startGame();
        } else if (args[0].equalsIgnoreCase("stop")) {
            player.sendMessage("Stopping Bedwars match");
            BedwarsRunner.endGame();
        } else if (args[0].equalsIgnoreCase("run")) {
            if (args.length > 1 && args[1].equalsIgnoreCase("test")) BedwarsRunner.isTesting = true;
            else BedwarsRunner.isTesting = false;

            player.sendMessage("Running Bedwars match" + (BedwarsRunner.isTesting ? " (Testing)" : ""));
            BedwarsRunner.world = player.getWorld();
            BedwarsRunner.runGame();
        } else if (args[0].equalsIgnoreCase("save")) {
            player.sendMessage("Saving Bedwars match");
            DataHandler.Save(BedwarsRunner.savePath);
        } else if (args[0].equalsIgnoreCase("setAreaStart")) {
            Location pos = player.getLocation();
            Vector vec = new Vector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            BedwarsRunner.Variables.AreaStart = vec;
        } else if (args[0].equalsIgnoreCase("setAreaEnd")) {
            Location pos = player.getLocation();
            Vector vec = new Vector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            BedwarsRunner.Variables.AreaEnd = vec;   
        } else if (args[0].equalsIgnoreCase("setLobby")) {
            Location pos = player.getLocation();
            Vector vec = new Vector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            BedwarsRunner.Variables.LobbyPosition = vec;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String current, @NotNull String[] args) {
        List<String> autoCompletions = new ArrayList<>();
        if (args.length < 1) return null;
        if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("run")) {
            if (args.length < 2) return null;
            if ("test".startsWith(args[1].toLowerCase())) autoCompletions.add("test");
        } else {
            String[] s = new String[] {"start","stop","run","save","setAreaStart","setAreaEnd","setLobby"};
            for (int i = 0; i < s.length; i++) {
                if (s[i].toLowerCase().startsWith(args[0].toLowerCase())) autoCompletions.add(s[i]);
            }
        }
        return autoCompletions;
    }
    
}
