package me.pixeldots.Shops.Inventorys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.SaveData.DataHandler;
import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Shops.ShopUtils.CurrencyType;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class CompassInventory extends BaseShopInventory {

    public String category = "";

    public CompassInventory(int count, String title) {
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
        clearActions();
        if (category.equals("base")) {
            setItem(inventory, 0, ShopUtils.setItemName(new ItemStack(Material.EMERALD), "Tracker Shop"), (plr, inv) -> {
                getInventory(player, inventory, "tracker");
            });
        } else if (category.equals("tracker")) {
            if (!BedwarsRunner.Variables.PlayerStats.containsKey(player.getUniqueId())) {
                getInventory(player, inventory, category);
                return;
            }
            int slot = 0;
            int playerTeam = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team;
            
            for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
                if (i == playerTeam) continue;;
                if (!BedwarsRunner.Variables.canTeamRespawn(i) && TeamUtils.isTeamAlive(i)) {
                    String color = BedwarsRunner.Variables.Teams.get(i);
                    final int num = i;
                    ItemStack item = new ItemStack(Material.getMaterial(color.toUpperCase() + "_WOOL"));
                    setItem(inventory, slot, ShopUtils.setItemName(item, color), (plr, inv) -> {
                        if (ShopUtils.canGiveItem(plr.getInventory(), CurrencyType.EMERALD, 1)) {
                            BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).trackingTeam = num;
                            player.sendMessage(Utils.text("You purchased ", TextColor.color(0, 255, 0)).append(Utils.text("a " + color + " tracker", TextColor.color(255, 255, 0))));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1, 1);
                        } else  player.sendRawMessage(ChatColor.RED + "You need 1 EMERALD to buy a tracker");
                    }, "1 EMERALD", LoreMode.PURCHASE);
                    slot++;
                }
            }
        }
    }

    @Override
    public void runItemAction(Inventory inv, String category, int slot, Player player, boolean isShiftClick) {
        super.runItemAction(inv, category, slot, player, isShiftClick);
    }
    
}
