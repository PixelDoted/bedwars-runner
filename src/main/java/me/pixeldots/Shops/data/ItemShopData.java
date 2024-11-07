package me.pixeldots.Shops.data;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Shops.data.ItemShopData.ShopItemData.GiveItemData.ItemEffectData;
import me.pixeldots.Shops.data.ItemShopData.ShopItemData.GiveItemData.ItemEnchantmentData;
import me.pixeldots.Utils.Utils;

public class ItemShopData {
    
    public int selectedBarHeight = 1;
    public boolean showSelectedBar = true;

    public int quickBuySize = 21;
    public List<CategoryData> categories;
    public Map<String, List<ShopItemData>> sections;
    
    public static class CategoryData {
        public int slot = -1;
        public String item;
        public String name;
        public String category;

        public int selectedBarHeight = 1;
        public boolean hasIcon = true;
        public boolean isQuickBuy = false;
    }

    public static class ShopItemData {
        public int slot;
        public String name;
        public String cost;
        public GiveItemData give;
        public String replace;

        public List<TierData> tiers;
        public List<String> description;

        public static ItemStack getStack(GiveItemData data, String teamColor) {
            if (data == null) return ShopUtils.setItemName(new ItemStack(Material.BARRIER), "Item Error: Item Data not found");
            Material mat = null;
            if (data.armor) mat = Material.getMaterial(data.item.toUpperCase() + "_BOOTS");
            else mat = Material.getMaterial(data.item.toUpperCase().replace(" ", "_").replace("%TEAMCOLOR%", teamColor.toUpperCase()));

            if (mat == null) return ShopUtils.setItemName(new ItemStack(Material.BARRIER), "Item Error: Invalid material");
            ItemStack stack = new ItemStack(mat, (data.count < 1 ? 1 : data.count));
            ItemMeta meta = stack.getItemMeta();
            if (data.name != "" && data.name != null) meta.displayName(Utils.text(data.name.replace('&', ChatColor.COLOR_CHAR)));
            
            if (data.enchantments != null) {
                for (int i = 0; i < data.enchantments.size(); i++) {
                    ItemEnchantmentData ench = data.enchantments.get(i);
                    meta.addEnchant(Enchantment.getByKey(NamespacedKey.fromString(ench.enchantment)), ench.level, true);
                }
            }
            if (meta instanceof PotionMeta) {
                if (data.potioncolor != "") { 
                    String[] color = data.potioncolor.split(",");
                    ((PotionMeta)meta).setColor(Color.fromRGB(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                }
                if (data.effects != null) {
                    for (int i = 0; i < data.effects.size(); i++) {
                        ItemEffectData effect = data.effects.get(i);
                        PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.fromString(effect.effect));
                        ((PotionMeta)meta).addCustomEffect(new PotionEffect(effectType, Utils.toTickTime(effect.duration), effect.level), true);
                    }
                }
            }

            stack.setItemMeta(meta);
            return stack;
        }
        public String getDisplayName(Material mat, int tier) {
            String s = Utils.getItemName(mat);
            if (tiers != null && tiers.get(tier).name != null) s = tiers.get(tier).name;
            else if (name != null) s = name;
            s = s.replace("%ItemName%", Utils.getItemName(mat));
            return s;
        }
        public int getTier(PlayerInventory inventory) {
            ItemStack[] stacks = inventory.getContents();
            for (int i = 0; i < stacks.length; i++) {
                if (stacks[i] == null) continue;
                for (int j = 0; j < tiers.size(); j++) {
                    if (stacks[i].getType().name().equalsIgnoreCase(tiers.get(j).replace)) {
                        return j;
                    }
                }
            }
            return 0;
        }

        public static class TierData {
            public String cost;
            public String name;
            public GiveItemData give;
            public String replace;
        }

        public static class GiveItemData {
            public String name;
            public boolean armor;
            public String item;
            public int count;
            public String potioncolor;
            public List<ItemEnchantmentData> enchantments;
            public List<ItemEffectData> effects;

            public static class ItemEnchantmentData {
                public String enchantment;
                public int level;
            }
            public static class ItemEffectData {
                public String effect;
                public int level;
                public String duration;
            }
        }
    }

}
