package me.pixeldots.Shops;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Shops.Inventorys.TeamSelectorInventory;
import me.pixeldots.Shops.Inventorys.BaseShopInventory;
import me.pixeldots.Shops.Inventorys.CompassInventory;
import me.pixeldots.Shops.Inventorys.ItemShopInventory;
import me.pixeldots.Shops.Inventorys.UpgradeInventory;
import me.pixeldots.Shops.Inventorys.UpgradeShopInventory;

public class InventoryHandler {

    public static Map<UUID, BaseShopInventory> playerInventorys = new HashMap<>();

    // Compass
    public static void getCompass(Player player) {
        CompassInventory inventory;
        if (playerInventorys.containsKey(player.getUniqueId()) && playerInventorys.get(player.getUniqueId()) instanceof CompassInventory) {
            inventory = (CompassInventory)playerInventorys.get(player.getUniqueId());
        } else {
            inventory = new CompassInventory(27, "Compass");
            playerInventorys.put(player.getUniqueId(), inventory);
        }
        inventory.registerInventory(player);
    }

    public static void handleCompass(Player player, Inventory inv, int slot, boolean isShiftClick) {
        CompassInventory getInv = (CompassInventory)playerInventorys.get(player.getUniqueId());
        getInv.runItemAction(inv, getInv.category, slot, player, isShiftClick);
    }

    // Item Shop
    public static void getItemShop(Player player) {
        ItemShopInventory inventory;
        if (playerInventorys.containsKey(player.getUniqueId()) && playerInventorys.get(player.getUniqueId()) instanceof ItemShopInventory) {
            inventory = (ItemShopInventory)playerInventorys.get(player.getUniqueId());
        } else {
            inventory = new ItemShopInventory(54, "Item Shop");
            playerInventorys.put(player.getUniqueId(), inventory);
        }
        inventory.registerInventory(player);
    }

    public static void handleItemShop(Player player, Inventory inv, int slot, boolean isShiftClick) {
        ItemShopInventory getInv = (ItemShopInventory)playerInventorys.get(player.getUniqueId());
        getInv.runItemAction(inv, getInv.category, slot, player, isShiftClick);
    }

    // New Upgrade Shop
    public static void getUpgradeShop(Player player) {
        UpgradeShopInventory inventory;
        if (playerInventorys.containsKey(player.getUniqueId()) && playerInventorys.get(player.getUniqueId()) instanceof UpgradeShopInventory) {
            inventory = (UpgradeShopInventory)playerInventorys.get(player.getUniqueId());
        } else {
            inventory = new UpgradeShopInventory(45, "Upgrade Shop");
            playerInventorys.put(player.getUniqueId(), inventory);
        }
        inventory.registerInventory(player);
    }

    public static void handleUpgradeShop(Player player, Inventory inv, int slot, boolean isShiftClick) {
        UpgradeShopInventory getInv = (UpgradeShopInventory)playerInventorys.get(player.getUniqueId());
        getInv.runItemAction(inv, "", slot, player, isShiftClick);
    }

    // Upgrade Shop
    public static void getUpgrade(Player player) {
        UpgradeInventory inventory;
        if (playerInventorys.containsKey(player.getUniqueId()) && playerInventorys.get(player.getUniqueId()) instanceof UpgradeInventory) { 
            inventory = (UpgradeInventory)playerInventorys.get(player.getUniqueId());
        }else { 
            inventory = new UpgradeInventory(45, "Upgrade");
            playerInventorys.put(player.getUniqueId(), inventory);
        }
        inventory.registerInventory(player);
    }

    public static void handleUpgrade(Player player, Inventory inv, int slot, boolean isShiftClick) {
        UpgradeInventory getInv = (UpgradeInventory)playerInventorys.get(player.getUniqueId());
        getInv.runItemAction(inv, "", slot, player, isShiftClick);
    }

    // Team Selector
    public static void getTeamSelector(Player player) {
        TeamSelectorInventory inventory;
        if (playerInventorys.containsKey(player.getUniqueId()) && playerInventorys.get(player.getUniqueId()) instanceof TeamSelectorInventory) {
            inventory = (TeamSelectorInventory)playerInventorys.get(player.getUniqueId());
        } else {
            int teamCount = BedwarsRunner.Variables.Teams.size();
            if (teamCount%9 == 9) teamCount++;
            
            inventory = new TeamSelectorInventory((int)Math.ceil(teamCount/9f)*9, "Team Selector");
            playerInventorys.put(player.getUniqueId(), inventory);
        }
        inventory.registerInventory(player);
    }

    public static void handleTeamSelector(Player player, Inventory inv, int slot, boolean isShiftClick) {
        TeamSelectorInventory getInv = (TeamSelectorInventory)playerInventorys.get(player.getUniqueId());
        getInv.runItemAction(inv, "", slot, player, isShiftClick);
    }

    public static void giveItem(PlayerInventory inv, ItemStack item, int count) {
        for (int i = 0; i < count; i++) {
            inv.addItem(item);
        }
    }

}
