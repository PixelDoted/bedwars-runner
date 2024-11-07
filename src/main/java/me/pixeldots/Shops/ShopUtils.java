package me.pixeldots.Shops;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Shops.data.ItemShopData;
import me.pixeldots.Shops.data.UpgradeShopData;
import me.pixeldots.Shops.data.ItemShopData.CategoryData;
import me.pixeldots.Utils.Utils;

public class ShopUtils {
    
    public static void fillRow(Inventory inv, ItemStack item, int begin, int end) {
        for (int i = 0; i < end-begin; i++) {
            inv.setItem(i+begin, item);
        }
    }

    public static boolean playerHasItem(Player player, Material type) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] items = inv.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == type) return true;
        }
        return false;
    }

    public static boolean playerHasItem(Player player, String type) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] items = inv.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType().name().endsWith(type)) return true;
        }
        return false;
    }

    public static ItemStack setItemName(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta(); meta.displayName(Utils.text(name)); stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack enchantItem(ItemStack stack, Enchantment ench, int lvl) {
        ItemMeta meta = stack.getItemMeta(); meta.addEnchant(ench, lvl, true); stack.setItemMeta(meta);
        return stack;
    }

    public static boolean hasItemCount(Inventory inv, Material item, int count, boolean autoRemove) {
        ItemStack[] items = inv.getContents();
        int totalCount = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) continue;
            if (items[i].getType() == item) {
                if (items[i].getAmount() >= count) {
                    if (autoRemove) inv.removeItem(new ItemStack(item, count));
                    return true;
                } else {
                    totalCount += items[i].getAmount();
                    if (totalCount >= count) {
                        if (autoRemove) inv.removeItem(new ItemStack(item, count));
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static boolean canGiveItem(Inventory inv, CurrencyType type, int count) {
        Material mat = getCurrencyMaterial(type);
        if (mat != null && hasItemCount(inv, mat, count, true)) return true;
        return false;
    }
    public static boolean canGiveItem(Inventory inv, CurrencyType type, int count, ItemStack stack) {
        Material mat = getCurrencyMaterial(type);
        if (isInventoryFull(inv, stack, new ItemStack(mat, count))) return false;
        if (mat != null && hasItemCount(inv, mat, count, true)) return true;
        return false;
    }

    public static boolean isShopInventory(String title) {
        return title.equalsIgnoreCase("item shop") || title.equalsIgnoreCase("upgrade") || title.equalsIgnoreCase("team selector") || title.equalsIgnoreCase("compass");
    }

    public static void handleShop(Inventory inv, Player player, int slot, String title, boolean isShiftClick) {
        if (title.startsWith("item")) InventoryHandler.handleItemShop(player, inv, slot, isShiftClick);
        else if (title.startsWith("upgrade")) InventoryHandler.handleUpgrade(player, inv, slot, isShiftClick);
        else if (title.startsWith("team selector")) InventoryHandler.handleTeamSelector(player, inv, slot, isShiftClick);
        else if (title.startsWith("compass")) InventoryHandler.handleCompass(player, inv, slot, isShiftClick);
    }

    public static boolean isInventoryFull(Inventory inv, ItemStack give, ItemStack remove) {
        ItemStack[] stacks = inv.getContents();
        int holdItemCount = 0;
        for (int i = 0; i < stacks.length; i++) {
            if (holdItemCount >= give.getAmount()) return false;
            else if (stacks[i] == null) return false;
            else if (stacks[i].getType() == give.getType()) holdItemCount = 64-stacks[i].getAmount();
            else if (stacks[i].getType() == remove.getType() && stacks[i].getAmount()-remove.getAmount() <= 0) return false;
        }
        return true;
    }

    public static Material getCurrencyMaterial(CurrencyType type) {
        switch (type) {
            case IRON:
                return Material.IRON_INGOT;
            case GOLD:
                return Material.GOLD_INGOT;
            case DIAMOND:
                return Material.DIAMOND;
            case EMERALD:
                return Material.EMERALD;
        }
        return null;
    }

    public static ItemShopData getItemShopFromJSON(InputStream input) {
        InputStreamReader reader = new InputStreamReader(input);
        return new Gson().fromJson(reader, ItemShopData.class);
    }
    public static UpgradeShopData getUpgradeShopFromJSON(InputStream input) {
        InputStreamReader reader = new InputStreamReader(input);
        return new Gson().fromJson(reader, UpgradeShopData.class);
    }

    public static CategoryData getQuickBuyCategory() {
        ItemShopData data = BedwarsRunner.Variables.itemShopData;
        for (int i = 0; i < data.categories.size(); i++) {
            if (data.categories.get(i).isQuickBuy) return data.categories.get(i);
        }
        return null;
    }
    public static boolean isQuickBuyCategory(String category) {
        ItemShopData data = BedwarsRunner.Variables.itemShopData;
        for (int i = 0; i < data.categories.size(); i++) {
            if (data.categories.get(i).category.equals(category)) return data.categories.get(i).isQuickBuy;
        }
        return false;
    }

    public enum CurrencyType { IRON, GOLD, DIAMOND, EMERALD }

}
