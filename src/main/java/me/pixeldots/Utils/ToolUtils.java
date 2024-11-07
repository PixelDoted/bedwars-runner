package me.pixeldots.Utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Shops.ShopUtils.CurrencyType;

public class ToolUtils {

    public static ToolItem getUpgradedPickaxe(Player player, Inventory inv) {
        if (ShopUtils.playerHasItem(player, Material.GOLDEN_PICKAXE)) {
            return new ToolItem(ShopUtils.enchantItem(new ItemStack(Material.DIAMOND_PICKAXE, 1), Enchantment.DIG_SPEED, 3), CurrencyType.GOLD, 6, 4);
        } else if (ShopUtils.playerHasItem(player, Material.IRON_PICKAXE)) {
            return new ToolItem(ShopUtils.enchantItem(new ItemStack(Material.GOLDEN_PICKAXE, 1), Enchantment.DIG_SPEED, 2), CurrencyType.GOLD, 3, 3);
        } else if (ShopUtils.playerHasItem(player, Material.WOODEN_PICKAXE)) {
            return new ToolItem(ShopUtils.enchantItem(new ItemStack(Material.IRON_PICKAXE, 1), Enchantment.DIG_SPEED, 1), CurrencyType.IRON, 10, 2);
        } else { 
            return new ToolItem(ShopUtils.enchantItem(new ItemStack(Material.WOODEN_PICKAXE, 1), Enchantment.DIG_SPEED, 1), CurrencyType.IRON, 10, 1);
        }
    }
    public static ToolItem getUpgradedAxe(Player player, Inventory inv) {
        if (ShopUtils.playerHasItem(player, Material.GOLDEN_AXE)) {
            return new ToolItem(ShopUtils.enchantItem(new ItemStack(Material.DIAMOND_AXE, 1), Enchantment.DIG_SPEED, 3), CurrencyType.GOLD, 6, 4);
        } else if (ShopUtils.playerHasItem(player, Material.IRON_AXE)) {
            return new ToolItem(ShopUtils.enchantItem(new ItemStack(Material.GOLDEN_AXE, 1), Enchantment.DIG_SPEED, 2), CurrencyType.GOLD, 3, 3);
        } else if (ShopUtils.playerHasItem(player, Material.WOODEN_AXE)) {
            return new ToolItem(ShopUtils.enchantItem(new ItemStack(Material.IRON_AXE, 1), Enchantment.DIG_SPEED, 1), CurrencyType.IRON, 10, 2);
        } else { 
            return new ToolItem(ShopUtils.enchantItem(new ItemStack(Material.WOODEN_AXE, 1), Enchantment.DIG_SPEED, 1), CurrencyType.IRON, 10, 1);
        }
    }

    public static ItemStack getDowngradedPickaxe(Player player, Inventory inv) {
        if (ShopUtils.playerHasItem(player, Material.DIAMOND_PICKAXE)) {
            return ShopUtils.enchantItem(new ItemStack(Material.GOLDEN_PICKAXE, 1), Enchantment.DIG_SPEED, 2);
        } else if (ShopUtils.playerHasItem(player, Material.GOLDEN_PICKAXE)) {
            return ShopUtils.enchantItem(new ItemStack(Material.IRON_PICKAXE, 1), Enchantment.DIG_SPEED, 1);
        } else {
            return ShopUtils.enchantItem(new ItemStack(Material.WOODEN_PICKAXE, 1), Enchantment.DIG_SPEED, 1);
        }
    }
    public static ItemStack getDowngradedAxe(Player player, Inventory inv) {
        if (ShopUtils.playerHasItem(player, Material.DIAMOND_AXE)) {
            return ShopUtils.enchantItem(new ItemStack(Material.GOLDEN_AXE, 1), Enchantment.DIG_SPEED, 2);
        } else if (ShopUtils.playerHasItem(player, Material.GOLDEN_AXE)) {
            return ShopUtils.enchantItem(new ItemStack(Material.IRON_AXE, 1), Enchantment.DIG_SPEED, 1);
        } else {
            return ShopUtils.enchantItem(new ItemStack(Material.WOODEN_AXE, 1), Enchantment.DIG_SPEED, 1);
        }
    }

    public static ItemStack setUnbreakable(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta(); meta.setUnbreakable(true); stack.setItemMeta(meta);
        return stack;
    }

    public static class ToolItem {
        public ItemStack stack;
        public CurrencyType type;
        public int amount;
        public int tier = 0;

        public ToolItem(ItemStack _stack, CurrencyType _type, int _amount, int _tier) {
            this.stack = _stack;
            this.type = _type;
            this.amount = _amount;
            this.tier = _tier;
        }
    }

}
