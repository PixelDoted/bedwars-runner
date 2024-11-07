package me.pixeldots.API;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.pixeldots.API.Events.BedDestroyedEvent;
import me.pixeldots.API.Events.GameEndEvent;
import me.pixeldots.API.Events.PurchaseItemEvent;
import me.pixeldots.API.Events.PurchaseUpgradeEvent;
import me.pixeldots.API.Events.TeamEliminatedEvent;
import me.pixeldots.Shops.ShopUtils.CurrencyType;

public class APIEventCaller {

    public static boolean gameEnd(int teamWon, List<UUID> won, List<UUID> lost) {
        GameEndEvent data = new GameEndEvent(teamWon, won, lost);
        Bukkit.getServer().getPluginManager().callEvent(data);
        return data.isCancelled();
    }

    public static boolean teamEliminated(int teamID, List<UUID> players) {
        TeamEliminatedEvent data = new TeamEliminatedEvent(teamID, players);
        Bukkit.getServer().getPluginManager().callEvent(data);
        return data.isCancelled();
    }

    public static boolean bedDestroyed(int teamID, int bedID, Location location) {
        BedDestroyedEvent data = new BedDestroyedEvent(teamID, bedID, location);
        Bukkit.getServer().getPluginManager().callEvent(data);
        return data.isCancelled();
    }

    public static PurchaseItemEvent playerPurchaseItem(Player player, ItemStack item, int costAmount, CurrencyType currency, boolean fromQuickBuy) {
        PurchaseItemEvent data = new PurchaseItemEvent(player, item, costAmount, currency, fromQuickBuy);
        Bukkit.getServer().getPluginManager().callEvent(data);
        return data;
    }

    public static PurchaseUpgradeEvent playerPurchaseUpgrade(Player player, String upgrade, int costAmount, CurrencyType currency) {
        PurchaseUpgradeEvent data = new PurchaseUpgradeEvent(player, upgrade, costAmount, currency);
        Bukkit.getServer().getPluginManager().callEvent(data);
        return data;
    }

}
