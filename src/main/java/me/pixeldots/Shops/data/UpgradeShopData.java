package me.pixeldots.Shops.data;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Shops.data.UpgradeShopData.UpgradeItemData.DisplayItemData.ItemEffectData;
import me.pixeldots.Shops.data.UpgradeShopData.UpgradeItemData.DisplayItemData.ItemEnchantmentData;
import me.pixeldots.Utils.Utils;

public class UpgradeShopData {
    
    public int spacingBarHeight = 1;
    public boolean showSpacingBar = true;

    public List<UpgradeItemData> section;

    public static class UpgradeItemData {
        public int slot;
        public String name;
        public List<String> cost;
        public DisplayItemData display;

        public String upgrade;
        public List<String> description;

        public static ItemStack getStack(DisplayItemData data, int level, String name, String teamColor) {
            if (data == null) return ShopUtils.setItemName(new ItemStack(Material.BARRIER), "Item Error: Item Data not found");
            Material mat = Material.getMaterial(data.item.toUpperCase().replace(" ", "_").replace("%TEAMCOLOR%", teamColor.toUpperCase()));

            if (mat == null) return ShopUtils.setItemName(new ItemStack(Material.BARRIER), "Item Error: Invalid material");
            ItemStack stack = new ItemStack(mat, (data.count < 1 ? 1 : data.count));
            ItemMeta meta = stack.getItemMeta();
            if (name != "" && name != null) 
                meta.displayName(Utils.text(name.replace("%UpgradeLevel%", Utils.toRomanNumerics(level)).replace('&', ChatColor.COLOR_CHAR)));
            
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
            s = s.replace("%ItemName%", Utils.getItemName(mat)).replace("%UpgradeTier%", Utils.toRomanNumerics(tier));
            return s;
        }

        public static class DisplayItemData {
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
