package me.pixeldots.Shops.Inventorys;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Game.data.PlayerStatistics;
import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class TeamSelectorInventory extends BaseShopInventory {

    public TeamSelectorInventory(int count, String title) {
        super(count , title);
    }

    public void registerInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, count, Utils.text(title));
        Base(inv);
        player.openInventory(inv);
    }

    public void Base(Inventory inventory) {
        List<String> teams = BedwarsRunner.Variables.Teams;
        for (int i = 0; i < teams.size(); i++) {
            final int num = i;
            ItemStack item = new ItemStack(Material.getMaterial(teams.get(num).toUpperCase() + "_WOOL"));
            setItem(inventory, i, ShopUtils.setItemName(item, teams.get(num)), (player, inv) -> {
                BedwarsRunner.Variables.PlayerStats.put(player.getUniqueId(), new PlayerStatistics(num));
                String team = BedwarsRunner.Variables.Teams.get(num);
                player.sendRawMessage(ChatColor.WHITE + "You have joined " + Utils.getChatColor(team) + team + ChatColor.WHITE + " team");
            }, "Joins " + BedwarsRunner.Variables.Teams.get(num), LoreMode.NONE);
        }
        setItem(inventory, teams.size(), ShopUtils.setItemName(new ItemStack(Material.GLASS), "Spectator"), (player, inv) -> {
            BedwarsRunner.Variables.Spectators.add(player.getUniqueId());
            player.sendRawMessage(ChatColor.WHITE + "You are now a Spectator");
        }, "This will make you a spectator when the game starts", LoreMode.NONE);
    }
    
}
