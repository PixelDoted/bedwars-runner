package me.pixeldots.Shops.Inventorys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;

public class BaseShopInventory {

    public int count = 9;
    public String title = "";
    public Map<Integer, ItemSlotAction> actions = new HashMap<>();

    public BaseShopInventory(int count, String title) {
        this.count = count;
        this.title = title;
    }

    public void clearActions() {
        actions.clear();
    }

    public void setItem(Inventory inv, int slot, ItemStack item, ItemSlotAction action) {
        inv.setItem(slot, item);
        actions.put(slot, action);
    }

    public void setItem(Inventory inv, int slot, ItemStack item, ItemSlotAction action, String cost) {
        setItem(inv, slot, item, action, cost, LoreMode.ALL);
    }
    public void setItem(Inventory inv, int slot, ItemStack item, ItemSlotAction action, String cost, LoreMode loreMode) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        if (cost.equalsIgnoreCase("purchased")) lore.add(Utils.text(getCostColor(cost) + cost));
        else if (!cost.equals("")) lore.add(Utils.text("Cost: " + getCostColor(cost) + cost, TextColor.color(169, 169, 169))); 

        if (meta.lore() != null) {
            List<Component> metaLore = meta.lore();
            for (int i = 0; i < metaLore.size(); i++) {
                lore.add(metaLore.get(i));
            }
        }

        lore.add(Utils.text("")); meta.lore(addShopItemLore(lore, loreMode)); item.setItemMeta(meta);

        inv.setItem(slot, item);
        actions.put(slot, action);
    }

    public void setItem(Inventory inv, int slot, ItemStack item, ItemSlotAction action, String cost, String[] loreArray) {
        if (loreArray == null) setItem(inv, slot, item, action, cost);
        else setItem(inv, slot, item, action, cost, loreArray, false, LoreMode.ALL);
    }
    public void setItem(Inventory inv, int slot, ItemStack item, ItemSlotAction action, String cost, String[] loreArray, boolean isTierList, LoreMode loreMode) {
        ItemMeta meta = item.getItemMeta(); 
        List<Component> lore = new ArrayList<>();
        for (int i = 0; i < loreArray.length; i++) {
            if (isTierList) lore.add(Utils.text("Tier " + (i+1) + ": " + loreArray[i], TextColor.color(169, 169, 169)));
            else lore.add(Utils.text(loreArray[i], TextColor.color(169, 169, 169)));
        } 
        meta.lore(lore); item.setItemMeta(meta);

        setItem(inv, slot, item, action, cost, loreMode);
    }

    public List<Component> addShopItemLore(List<Component> lore, LoreMode loreMode) {
        if (loreMode == LoreMode.ALL || loreMode == LoreMode.QUICKBUY) lore.add(Utils.text("Sneak Click to add to Quick Buy", TextColor.color(0, 255, 255)));
        if (loreMode == LoreMode.ALL || loreMode == LoreMode.PURCHASE) lore.add(Utils.text("Click to purchase!", TextColor.color(255, 255, 0)));
        return lore;
    }

    public ChatColor getCostColor(String cost) {
        cost = cost.toLowerCase();
        if (cost.equals("purchased")) return ChatColor.GREEN;
        else if (cost.endsWith("emerald") || cost.endsWith("emeralds")) return ChatColor.DARK_GREEN;
        else if (cost.endsWith("diamond") || cost.endsWith("diamonds")) return ChatColor.AQUA;
        else if (cost.endsWith("gold")) return ChatColor.GOLD;
        else return ChatColor.GRAY;
    }
    public void runItemAction(Inventory inv, String category, int slot, Player player, boolean isShiftClick) {
        if (!actions.containsKey(slot)) return;
        actions.get(slot).run(player, inv);
    }

    public interface ItemSlotAction {
        void run(Player player, Inventory inv);
    }

    public class QuickBuyMemory {
        public int slot;
        public String category;
    }

    public static enum LoreMode { ALL, PURCHASE, QUICKBUY, NONE };
    
}
