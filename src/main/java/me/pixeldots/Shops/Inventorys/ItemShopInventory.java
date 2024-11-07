package me.pixeldots.Shops.Inventorys;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.API.APIEventCaller;
import me.pixeldots.API.Events.PurchaseItemEvent;
import me.pixeldots.SaveData.DataHandler;
import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Shops.ShopUtils.CurrencyType;
import me.pixeldots.Shops.data.ItemShopData;
import me.pixeldots.Shops.data.ItemShopData.CategoryData;
import me.pixeldots.Shops.data.ItemShopData.ShopItemData;
import me.pixeldots.Shops.data.ItemShopData.ShopItemData.GiveItemData;
import me.pixeldots.Utils.PlayerUtils;
import me.pixeldots.Utils.Utils;
import me.pixeldots.Utils.PlayerUtils.PlayerArmor;

public class ItemShopInventory extends BaseShopInventory {

    public String category = "";
    public boolean isQuickBuyCategory = false;
    public QuickBuyMemory quickBuyData = null;

    public ItemShopInventory(int count, String title) {
        super(count, title);
    }

    public void registerInventory(Player player) {
        if (!BedwarsRunner.Variables.perPlayerQuickBuy.containsKey(player.getUniqueId()))
            BedwarsRunner.Variables.perPlayerQuickBuy.put(player.getUniqueId(), DataHandler.LoadPlayerQuickBuy(player.getUniqueId()));
        Inventory inv = Bukkit.createInventory(null, count, Utils.text(title));
        getInventory(player, inv, "base");
        player.openInventory(inv);
    }

    public void getInventory(Player player, Inventory inventory, String category) {
        this.category = category;
        String teamColor = "white";
        if (BedwarsRunner.Variables.PlayerStats.containsKey(player.getUniqueId()))
            teamColor = BedwarsRunner.Variables.Teams.get(BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team);
        
        inventory.clear();
        clearActions();

        ItemShopData data = BedwarsRunner.Variables.itemShopData;
        int catIndex = 0;
        for (int i = 0; i < data.categories.size(); i++) {
            CategoryData cat = data.categories.get(i);
            if (!cat.hasIcon) continue;
            ItemStack stack = ShopUtils.setItemName(new ItemStack(Material.getMaterial(cat.item.toUpperCase())), cat.name.replace('&', ChatColor.COLOR_CHAR));
            setItem(inventory, cat.slot, stack, (plr, inv) -> {
                getInventory(plr, inv, cat.category);
            });
            if (cat.category == category) catIndex = cat.slot;
        }
        if (data.showSelectedBar) {
            int barOffset = data.selectedBarHeight*9;
            if (catIndex == -1) {
                ShopUtils.fillRow(inventory, new ItemStack(Material.GRAY_STAINED_GLASS_PANE), barOffset, barOffset+9);
            } else {
                if (catIndex > 0) ShopUtils.fillRow(inventory, new ItemStack(Material.GRAY_STAINED_GLASS_PANE), barOffset, catIndex+barOffset);
                setItem(inventory, catIndex+barOffset, new ItemStack(Material.LIME_STAINED_GLASS_PANE), (plr, inv) -> {});
                if (catIndex < 8) ShopUtils.fillRow(inventory, new ItemStack(Material.GRAY_STAINED_GLASS_PANE), catIndex+(barOffset+1), barOffset+9);
            }
        }

        isQuickBuyCategory = ShopUtils.isQuickBuyCategory(category);
        if (isQuickBuyCategory) {
            Map<Integer, String> quickBuy =  BedwarsRunner.Variables.perPlayerQuickBuy.get(player.getUniqueId());
            for (int i = 0; i < data.quickBuySize; i++) {
                int slot = getSlot(i);
                if (quickBuy.containsKey(i)) {
                    String[] s = quickBuy.get(i).split(",");
                    setSlotItem(inventory, player, teamColor, getItemFromCategory(Integer.parseInt(s[0]), s[1]), slot);
                }
                else setItem(inventory, slot, new ItemStack(Material.RED_STAINED_GLASS_PANE), (plr, inv) -> {}, "");
            }
        } else {
            List<ShopItemData> items = data.sections.get(category);
            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    ShopItemData item = items.get(i);
                    setSlotItem(inventory, player, teamColor, item, getSlot(item.slot));
                }
            }
        }
    }

    public int getSlot(int raw) {
        int slot = raw+19;
        if (slot >= 26) slot+=2;
        if (slot >= 35) slot+=2;
        return slot;
    }
    public int getRawSlot(int slot) {
        int raw = slot;
        if (raw >= 26) raw-=2;
        if (raw >= 35) raw-=2;
        return raw-19;
    }

    @Override
    public void runItemAction(Inventory inv, String category, int slot, Player player, boolean isShiftClick) {
        if (quickBuyData != null && isQuickBuyCategory) {
            if (slot >= 19 && slot < 45) {
                String quickSlot = quickBuyData.slot + "," + quickBuyData.category;
                if (BedwarsRunner.Variables.perPlayerQuickBuy.containsKey(player.getUniqueId()))
                    BedwarsRunner.Variables.perPlayerQuickBuy.get(player.getUniqueId()).put(getRawSlot(slot), quickSlot);
            }
            quickBuyData = null;
            DataHandler.SavePlayerQuickBuy(player.getUniqueId(), BedwarsRunner.Variables.perPlayerQuickBuy.get(player.getUniqueId()));
            getInventory(player, inv, category);
        } else if (isShiftClick && !isQuickBuyCategory) {
            if (inv.getItem(slot) == null || inv.getItem(slot).getType() == Material.AIR) return;
            quickBuyData = new QuickBuyMemory();
            quickBuyData.slot = getRawSlot(slot);
            quickBuyData.category = category;

            String quickBuyCat = ShopUtils.getQuickBuyCategory().category;
            if (quickBuyCat == "") { quickBuyData = null; return; }
            getInventory(player, inv, quickBuyCat);
        }
        else super.runItemAction(inv, category, slot, player, isShiftClick);
    }

    public ShopItemData getItemFromCategory(int slot, String category) {
        return BedwarsRunner.Variables.itemShopData.sections.get(category).get(slot);
    }

    public void setSlotItem(Inventory inventory, Player player, String teamColor, ShopItemData item, int slot) {
        int tier = item.tiers != null ? item.getTier(player.getInventory()) : -1;
        GiveItemData give = tier != -1 ? item.tiers.get(tier).give : item.give;

        String totalCost = tier != -1 ? item.tiers.get(tier).cost : item.cost;
        String[] cost = totalCost.split(" ");
        int costAmount = Integer.parseInt(cost[0]);
        CurrencyType costType = CurrencyType.valueOf(cost[1].toUpperCase());

        ItemStack stack = ShopItemData.getStack(give, teamColor);
        String displayName = item.getDisplayName(stack.getType(), tier);
        String[] itemDesc = null;
        if (item.description != null) {
            itemDesc = new String[item.description.size()];
            for (int i = 0; i < itemDesc.length; i++) {
                itemDesc[i] = item.description.get(i);
                if (tier != -1) itemDesc[i] = itemDesc[i].replace("%Tier%", Utils.toRomanNumerics(tier+1)).replace('&', ChatColor.COLOR_CHAR);
            }
        }

        ItemStack displayStack = ShopUtils.setItemName(stack.clone(), displayName.replace('&', ChatColor.COLOR_CHAR));
        setItem(inventory, slot, displayStack, (plr, inv) -> {

            if (give.armor) PlayerUtils.giveArmor(PlayerArmor.valueOf(give.item.toUpperCase()), player, costType, costAmount);
            else {
                PurchaseItemEvent e = APIEventCaller.playerPurchaseItem(player, stack, costAmount, costType, isQuickBuyCategory);
                if (e.isCancelled()) return;
                
                if (PlayerUtils.giveItem(plr, inv, e.getItem(), costType, costAmount)) {
                    String replace = tier != -1 ? item.tiers.get(tier).replace : item.replace;
                    if (replace != null && replace != "")
                        plr.getInventory().remove(Material.getMaterial(replace.toUpperCase()));
                }
            }
            getInventory(player, inventory, category);
        }, totalCost, itemDesc);
    }
    
}
