package me.pixeldots.Utils;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.Extras.BedBugEntity;
import me.pixeldots.Extras.DreamDefenderEntity;
import me.pixeldots.Game.data.PlayerStatistics;
import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Shops.ShopUtils.CurrencyType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;

public class PlayerUtils {

    public static String getTeamColor(Player player) {
        return BedwarsRunner.Variables.Teams.get(BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team);
    }

    public static boolean dontRemoveItemsOnDeath(Material mat) {
        String name = mat.name();
        return mat == Material.SHEARS || mat == Material.COMPASS || name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS");
    }

    public static List<Player> getPlayersInRange(Player player) {
        int team = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team;
        List<Player> playersInRange = new ArrayList<>();
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player check = players.get(i);
            if (check == player) continue;
            if (team != BedwarsRunner.Variables.PlayerStats.get(check.getUniqueId()).team) continue;
            if (player.getLocation().distance(check.getLocation()) <= 2)
                playersInRange.add(check);
        }
        return playersInRange;
    }

    // Handler

    public static void setPlayerNameColor(Player player, String color) {
        TextColor textColor = Utils.getTextColor(color);
        String colorID = TextUtils.getStringIndex(color, 0) + " ";
        Component name = Utils.text(colorID + Utils.text(player.displayName()), textColor);
        
        player.playerListName(name);
        player.customName(name);
        //BedwarsRunner.invisibilityHandler.updatePlayerNameTag(player, Utils.text(name));
    }

    public static void giveArmor(PlayerArmor type, Player player) {
        PlayerInventory inv = player.getInventory();
        inv.setBoots(new ItemStack(Material.getMaterial(type.name() + "_BOOTS")));
        inv.setLeggings(new ItemStack(Material.getMaterial(type.name() + "_LEGGINGS")));
    }

    public static void giveArmor(PlayerArmor type, Player player, CurrencyType currency, int currencyCount) {
        PlayerInventory inv = player.getInventory();
        if (ShopUtils.canGiveItem(inv, currency, currencyCount)) {
            ItemStack boots = new ItemStack(Material.getMaterial(type.name() + "_BOOTS"));
            ItemMeta bootsMeta = boots.getItemMeta(); bootsMeta.setUnbreakable(true);
            bootsMeta = TeamUtils.addUpgradeMeta(bootsMeta, boots, player); boots.setItemMeta(bootsMeta);

            ItemStack leggings = new ItemStack(Material.getMaterial(type.name() + "_LEGGINGS"));
            ItemMeta leggingsMeta = boots.getItemMeta(); leggingsMeta.setUnbreakable(true);
            leggingsMeta = TeamUtils.addUpgradeMeta(leggingsMeta, leggings, player); leggings.setItemMeta(leggingsMeta);

            inv.setBoots(boots);
            inv.setLeggings(leggings);
            String armor = TextUtils.upperCaseFirst(type.name().toLowerCase()) + " Armor";
            player.sendMessage(Utils.text("You purchased ", TextColor.color(0, 255, 0)).append(Utils.text(armor, TextColor.color(255, 255, 0))));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1, 1);
        }
        else player.sendRawMessage(ChatColor.RED + "You need " + currencyCount + " " + currency.name().toLowerCase() + " to buy this item");
    }

    public static void giveItem(Inventory inv, ItemStack item) {
        inv.addItem(item);
    }
    public static boolean giveItem(Player player, Inventory inv, ItemStack item, CurrencyType currency, int currencyCount) {
        Inventory playerInv = player.getInventory();
        if (item.getType().name().endsWith("AXE") || item.getType().name().endsWith("PICKAXE") || item.getType().name().endsWith("SWORD")) {
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            meta = TeamUtils.addUpgradeMeta(meta, item, player);
            item.setItemMeta(meta);
        }
        if (ShopUtils.canGiveItem(playerInv, currency, currencyCount, item)) { 
            playerInv.addItem(item);
            String itemName = "";
            if (item.getItemMeta().displayName() != null) itemName = Utils.text(item.getItemMeta().displayName());
            else itemName = Utils.getItemName(item);

            if (itemName.equalsIgnoreCase("tnt")) itemName = itemName.toUpperCase();
            player.sendMessage(Utils.text("You purchased ", TextColor.color(0, 255, 0)).append(Utils.text(itemName, TextColor.color(255, 255, 0))));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1, 1);
            return true;
        }
        else player.sendRawMessage(ChatColor.RED + "You need " + currencyCount + " " + currency.name().toLowerCase() + " to buy this item");
        return false;
    }
    public static boolean giveItem(Player player, Inventory inv, ItemStack item, CurrencyType currency, int currencyCount, String replace) {
        boolean status = giveItem(player, inv, item, currency, currencyCount);
        if (status) {
            clearItemFromPlayer(player, replace, item.getType());
        }
        return status;
    }

    public static void clearItemFromPlayer(Player player, String replace, Material ignore) {
        Inventory inv = player.getInventory();
        if (replace.equalsIgnoreCase("pickaxe")) {
            if (ignore != Material.WOODEN_PICKAXE) inv.remove(Material.WOODEN_PICKAXE);
            if (ignore != Material.IRON_PICKAXE) inv.remove(Material.IRON_PICKAXE);
            if (ignore != Material.GOLDEN_PICKAXE) inv.remove(Material.GOLDEN_PICKAXE);
            if (ignore != Material.DIAMOND_PICKAXE) inv.remove(Material.DIAMOND_PICKAXE);
        } else if (replace.equalsIgnoreCase("axe")) {
            if (ignore != Material.WOODEN_AXE) inv.remove(Material.WOODEN_AXE);
            if (ignore != Material.IRON_AXE) inv.remove(Material.IRON_AXE);
            if (ignore != Material.GOLDEN_AXE) inv.remove(Material.GOLDEN_AXE);
            if (ignore != Material.DIAMOND_AXE) inv.remove(Material.DIAMOND_AXE);
        }
    }

    public static void sendTitle(Player player, String title, String subtitle, @Nullable TextColor titleColor, @Nullable TextColor subtitleColor) {
        sendTitle(player, Component.text(title, titleColor), Component.text(subtitle, subtitleColor));
    }
    public static void sendTitle(Player player, Component title, Component subtitle) {
        PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId());
        long date = Utils.getDateTime();
        boolean canSendTitle = true;
        if (stats != null && stats.lastTitle != null) {
            String[] s = stats.lastTitle.split(":");
            if (s[0].equals(Utils.text(title)) && s[1].equals(Utils.text(subtitle)) && Long.parseLong(s[2]) >= date-2*1000) canSendTitle = false;
        }
        if (canSendTitle) {
            player.showTitle(Title.title(title, subtitle));
            if (stats != null) stats.lastTitle = Utils.text(title) + ":" + Utils.text(subtitle) + ":" + date;
        }
    }

    public static void titleDied(Player player, long time, long died) {
        if (time >= died) {
            if (time < died+200) sendTitle(player, Utils.text("RESPAWNED!", TextColor.color(0, 255, 0)), Utils.text(""));
            return;
        }
        TextColor base = TextColor.color(255, 255, 0);
        TextColor secondary = TextColor.color(255, 15, 15);
        Component subtitle = Utils.text("You will respawn in ", base).append(Utils.text(((died-time)/1000+1) + "", secondary)).append(Utils.text(" seconds!", base));
        sendTitle(player, Utils.text("YOU DIED!", secondary), subtitle);
    }

    public static void giveLeatherArmor(Player player, PlayerInventory inventory) {
        ItemStack bootsStack = new ItemStack(Material.LEATHER_BOOTS);
        ItemStack leggingsStack = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack chestplateStack = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack helmetStack = new ItemStack(Material.LEATHER_HELMET);

        LeatherArmorMeta bootsMeta = (LeatherArmorMeta)bootsStack.getItemMeta();
        bootsMeta.setColor(Utils.getColorFromName(BedwarsRunner.Variables.getTeamColor(player.getUniqueId()).toLowerCase()));
        bootsMeta.setUnbreakable(true);
        bootsStack.setItemMeta(bootsMeta);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta)leggingsStack.getItemMeta();
        leggingsMeta.setColor(Utils.getColorFromName(BedwarsRunner.Variables.getTeamColor(player.getUniqueId()).toLowerCase()));
        leggingsMeta.setUnbreakable(true);
        leggingsStack.setItemMeta(leggingsMeta);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta)chestplateStack.getItemMeta();
        chestplateMeta.setColor(Utils.getColorFromName(BedwarsRunner.Variables.getTeamColor(player.getUniqueId()).toLowerCase()));
        chestplateMeta.setUnbreakable(true);
        chestplateStack.setItemMeta(chestplateMeta);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta)helmetStack.getItemMeta();
        helmetMeta.setColor(Utils.getColorFromName(BedwarsRunner.Variables.getTeamColor(player.getUniqueId()).toLowerCase()));
        helmetMeta.setUnbreakable(true);
        helmetStack.setItemMeta(helmetMeta);

        ItemStack woodensword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = TeamUtils.addUpgradeMeta(woodensword.getItemMeta(), woodensword, player);
        meta.setUnbreakable(true);
        woodensword.setItemMeta(meta);

        inventory.setItem(0, woodensword);
        inventory.setItem(8, ShopUtils.setItemName(new ItemStack(Material.COMPASS), "Compass"));
        inventory.setBoots(bootsStack);
        inventory.setLeggings(leggingsStack);
        inventory.setChestplate(chestplateStack);
        inventory.setHelmet(helmetStack);
    }

    public static Component getDeathMessage(String player, String killer, boolean isFinal) {
        if (killer != null)
            return Utils.text(player + ChatColor.WHITE + " was killed by " + killer + (isFinal ? ". " + ChatColor.BOLD + ChatColor.AQUA + "FINAL KILL!" : "."));
        return Utils.text(player + ChatColor.WHITE + " has died" + (isFinal ? ". " + ChatColor.BOLD + ChatColor.AQUA + "FINAL KILL!" : "."));
    }

    public enum PlayerArmor { CHAINMAIL, IRON, DIAMOND };

    public static void takePlayerItems(Player killer, Player player) {
        PlayerInventory inv = player.getInventory();
        
        int ironCount = 0;
        int goldCount = 0;
        int diamondCount = 0;
        int emeraldCount = 0;

        if (inv.getItemInOffHand() != null) {
            ItemStack offHandItem = inv.getItemInOffHand();
            if (offHandItem.getType() == Material.IRON_INGOT) ironCount += offHandItem.getAmount();
            else if (offHandItem.getType() == Material.GOLD_INGOT) goldCount += offHandItem.getAmount();
            else if (offHandItem.getType() == Material.DIAMOND) diamondCount += offHandItem.getAmount();
            else if (offHandItem.getType() == Material.EMERALD) emeraldCount += offHandItem.getAmount();
            inv.setItemInOffHand(null);
        }

        ItemStack[] items = inv.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) continue;
            Material type = items[i].getType();
            if (type == Material.IRON_INGOT) ironCount += items[i].getAmount();
            else if (type == Material.GOLD_INGOT) goldCount += items[i].getAmount();
            else if (type == Material.DIAMOND) diamondCount += items[i].getAmount();
            else if (type == Material.EMERALD) emeraldCount += items[i].getAmount();
            else if (type.name().endsWith("_SWORD")) {
                inv.remove(type);
                ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
                ItemMeta meta = TeamUtils.addUpgradeMeta(sword.getItemMeta(), sword, player); 
                meta.setUnbreakable(true); sword.setItemMeta(meta);
                inv.addItem(sword);
            } else if (type.name().endsWith("_PICKAXE")) {
                ItemStack pickaxe = ToolUtils.getDowngradedPickaxe(player, inv);
                inv.remove(type);
                ItemMeta meta = pickaxe.getItemMeta(); meta.setUnbreakable(true); pickaxe.setItemMeta(meta);
                inv.addItem(pickaxe);
            } else if (type.name().endsWith("_AXE")) {
                ItemStack axe = ToolUtils.getDowngradedAxe(player, inv);
                inv.remove(type);
                ItemMeta meta = axe.getItemMeta(); meta.setUnbreakable(true); axe.setItemMeta(meta);
                inv.addItem(axe);
            } else if (!PlayerUtils.dontRemoveItemsOnDeath(type)) {
                inv.remove(type);
            }
        }

        if (killer != null) {

            PlayerInventory killerInv = killer.getInventory();
            killerInv.addItem(new ItemStack(Material.IRON_INGOT, ironCount));
            killerInv.addItem(new ItemStack(Material.GOLD_INGOT, goldCount));
            killerInv.addItem(new ItemStack(Material.DIAMOND, diamondCount));
            killerInv.addItem(new ItemStack(Material.EMERALD, emeraldCount));

            if (ironCount > 0) killer.sendMessage(Utils.text(ChatColor.GRAY + "+" + ironCount + " iron"));
            if (goldCount > 0) killer.sendMessage(Utils.text(ChatColor.GOLD + "+" + goldCount + " gold"));
            if (diamondCount > 0) killer.sendMessage(Utils.text(ChatColor.AQUA + "+" + diamondCount + " diamond"));
            if (emeraldCount > 0) killer.sendMessage(Utils.text(ChatColor.DARK_GREEN + "+" + emeraldCount + " emerald"));
        } else {
            Location location = player.getLocation();
            if (ironCount > 0) BedwarsRunner.world.dropItem(location, new ItemStack(Material.IRON_INGOT, ironCount));
            if (goldCount > 0) BedwarsRunner.world.dropItem(location, new ItemStack(Material.GOLD_INGOT, goldCount));
            if (diamondCount > 0) BedwarsRunner.world.dropItem(location, new ItemStack(Material.DIAMOND, diamondCount));
            if (emeraldCount > 0) BedwarsRunner.world.dropItem(location, new ItemStack(Material.EMERALD, emeraldCount));
        }
        inv.remove(Material.IRON_INGOT);
        inv.remove(Material.GOLD_INGOT);
        inv.remove(Material.DIAMOND);
        inv.remove(Material.EMERALD);
    }

    public static boolean canPlayerAttackBedBug(Player player, Silverfish entity) {
        for (int i = 0; i < BedwarsRunner.Variables.BedBugs.size(); i++) {
            BedBugEntity bedbug = BedwarsRunner.Variables.BedBugs.get(i);
            if (bedbug.entity != entity) continue;
            if (!BedwarsConf.friendlyFire && bedbug.teamID == BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team) {
                return false;
            }
        }
        return true;
    }
    public static boolean canPlayerAttackDreamDefender(Player player, IronGolem entity) {
        for (int i = 0; i < BedwarsRunner.Variables.DreamDefenders.size(); i++) {
            DreamDefenderEntity obj = BedwarsRunner.Variables.DreamDefenders.get(i);
            if (obj.entity != entity) continue;
            if (!BedwarsConf.friendlyFire && obj.teamID == BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team) {
                return false;
            }
        }
        return true;
    }
    
}
