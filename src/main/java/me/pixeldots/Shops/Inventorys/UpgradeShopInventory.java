package me.pixeldots.Shops.Inventorys;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.API.APIEventCaller;
import me.pixeldots.API.Events.PurchaseUpgradeEvent;
import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Shops.ShopUtils.CurrencyType;
import me.pixeldots.Shops.data.UpgradeShopData;
import me.pixeldots.Shops.data.ItemShopData.ShopItemData;
import me.pixeldots.Shops.data.UpgradeShopData.UpgradeItemData;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.Utils;

public class UpgradeShopInventory extends BaseShopInventory {

    public UpgradeShopInventory(int count, String title) {
        super(count, title);
    }

    public void registerInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, count, Utils.text(title));
        getInventory(player, inv);
        player.openInventory(inv);
    }

    public void getInventory(Player player, Inventory inventory) {
        String teamColor = "white";
        if (BedwarsRunner.Variables.PlayerStats.containsKey(player.getUniqueId()))
            teamColor = BedwarsRunner.Variables.Teams.get(BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team);
        
        inventory.clear();
        clearActions();

        UpgradeShopData data = BedwarsRunner.Variables.upgradeShopData;
        if (data.showSpacingBar) {
            int barOffset = data.spacingBarHeight*9;
            ShopUtils.fillRow(inventory, new ItemStack(Material.GRAY_STAINED_GLASS_PANE), barOffset, barOffset+9);
        }

        List<UpgradeItemData> items = data.section;
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                UpgradeItemData item = items.get(i);
                setSlotItem(inventory, player, teamColor, item, getSlot(item.slot));
            }
        }
    }

    public int getSlot(int raw) {
        int slot = raw+1;
        if (slot >= 8) slot+=2;
        if (slot >= 17) slot+=2;
        if (slot >= 26) slot+=2;
        if (slot >= 35) slot+=2;
        return slot;
    }
    public int getRawSlot(int slot) {
        int raw = slot;
        if (slot >= 8) raw-=2;
        if (slot >= 17) raw-=2;
        if (slot >= 26) raw-=2;
        if (slot >= 35) raw-=2;
        return raw-1;
    }

    public ShopItemData getItemFromCategory(int slot, String category) {
        return BedwarsRunner.Variables.itemShopData.sections.get(category).get(slot);
    }

    public void setSlotItem(Inventory inventory, Player player, String teamColor, UpgradeItemData item, int slot) {
        int upgradeLevel = TeamUtils.getTeamUpgradeLevel(item.upgrade, player);
        
        String[] cost = item.cost.get(upgradeLevel).split(" ");
        int costAmount = cost.length >= 1 ? Integer.parseInt(cost[0]) : 1;
        CurrencyType costType = cost.length >= 2 ? CurrencyType.valueOf(cost[1].toUpperCase()) : CurrencyType.DIAMOND;

        ItemStack displayStack = UpgradeItemData.getStack(item.display, upgradeLevel, item.name, teamColor);
        String[] itemDesc = null;
        if (item.description != null) {
            itemDesc = new String[item.description.size()];
            for (int i = 0; i < itemDesc.length; i++) {
                String aboveTier = (upgradeLevel > i ? "&a" : "");
                itemDesc[i] = item.description.get(i).replace("%AboveTier%", aboveTier).replace('&', ChatColor.COLOR_CHAR);
            }
        }

        setItem(inventory, slot, displayStack, (plr, inv) -> {
            PurchaseUpgradeEvent e = APIEventCaller.playerPurchaseUpgrade(player, item.upgrade, costAmount, costType);
            if (e.isCancelled()) return;
            if (isUpgradeBuyable(plr, costType, count) && TeamUtils.canUpgradeTeam(item.upgrade, plr)) {
                int level = TeamUtils.modifyTeamUpgradeLevel(item.upgrade, plr);
                sendUpgradeMessage(plr, item.upgrade+(level == 0 ? "" : " "+Utils.toRomanNumerics(level)));
            }
            getInventory(player, inventory);
        }, (upgradeLevel < item.cost.size() ? item.cost.get(upgradeLevel) : "PURCHASED"), itemDesc);
    }

    public boolean isUpgradeBuyable(Player player, CurrencyType type, int amount) {
        PlayerInventory inv = player.getInventory();
        if (ShopUtils.canGiveItem(inv, type, amount)) return true;
        else {
            player.sendRawMessage(ChatColor.RED + "You need " + amount + " " + type.name().toLowerCase() + " to buy this upgrade");
            return false;
        }
    }

    public void sendUpgradeMessage(Player upgrader, String upgraded) {
        int team = BedwarsRunner.Variables.PlayerStats.get(upgrader.getUniqueId()).team;
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (BedwarsRunner.Variables.PlayerStats.get(players.get(i).getUniqueId()).team != team) continue;
            TeamUtils.upgradedMessage(players.get(i), upgrader, upgraded);
        }
    }
    
}
