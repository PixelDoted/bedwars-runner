package me.pixeldots.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.pixeldots.Shops.InventoryHandler;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class BedwarsGUICommand implements CommandExecutor, TabCompleter {
        
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String raw, @NotNull String[] args) {
        if (sender instanceof Player) {
            String shopID = args[0];
            if (shopID.equalsIgnoreCase("itemshop")) {
                InventoryHandler.getItemShop((Player)sender);
            } else if (shopID.equalsIgnoreCase("upgradeshop")) {
                InventoryHandler.getUpgradeShop((Player)sender);
            } else if (shopID.equalsIgnoreCase("upgrade")) {
                InventoryHandler.getUpgrade((Player)sender);
            } else if (shopID.equalsIgnoreCase("teamselector")) {
                InventoryHandler.getTeamSelector((Player)sender);
            }
            sender.sendMessage(Utils.text("Opened the " + shopID + " GUI"));
        } else 
            sender.sendMessage(Utils.text("You have to be a player to use this command", TextColor.color(255, 0, 0)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String current, @NotNull String[] args) {
        List<String> autoCompletions = new ArrayList<>();
        if (args.length < 1) return null;

        String[] s = new String[] {"itemshop","upgradeshop","teamselector"};
        for (int i = 0; i < s.length; i++) {
            if (s[i].toLowerCase().startsWith(args[0].toLowerCase())) autoCompletions.add(s[i]);
        }
        return autoCompletions;
    }
}
