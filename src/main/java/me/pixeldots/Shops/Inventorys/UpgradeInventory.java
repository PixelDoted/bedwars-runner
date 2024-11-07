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
import me.pixeldots.Game.data.TeamUpgrades;
import me.pixeldots.Game.data.TeamUpgrades.TeamTraps;
import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Shops.ShopUtils.CurrencyType;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class UpgradeInventory extends BaseShopInventory {

    public UpgradeInventory(int count, String title) {
        super(count, title);
    }

    public void registerInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, count, Utils.text(title));
        Base(inv, player);
        player.openInventory(inv);
    }

    public void Base(Inventory inventory, Player player) {
        inventory.clear();
        clearActions();
        TeamUpgrades upgrades = BedwarsRunner.isRunning ? TeamUtils.getTeamUpgrades(player) : new TeamUpgrades();
        String ProtectionName = "Reinforced Armor " + Utils.toRomanNumerics(upgrades.ProtectionLevel);
        String IronForgeName = "Iron Forge " + Utils.toRomanNumerics(upgrades.ForgeLevel);
        String ManiacMinerName = "Maniac Miner " + Utils.toRomanNumerics(upgrades.HasteLevel);

        if (upgrades.ProtectionLevel == 4) ProtectionName = "Reinforced Armor " + Utils.toRomanNumerics(4);
        if (upgrades.ForgeLevel == 4) IronForgeName = "Iron Forge " + Utils.toRomanNumerics(4);
        if (upgrades.HasteLevel == 2) ManiacMinerName = "Maniac Miner " + Utils.toRomanNumerics(2);

        setItem(inventory, 10, ShopUtils.setItemName(new ItemStack(Material.IRON_SWORD), "Sharpened Swords"), (plr, inv) -> {
            if (upgrades.SharpnessLevel == 0 && isUpgradeBuyable(plr, CurrencyType.DIAMOND, 8)) {
                TeamUtils.upgradeSharpness(plr);
                sendUpgradeMessage(plr, "Sharpened Swords");
            }
            Base(inventory, player);
        }, getPrice((upgrades.SharpnessLevel-1)*-1*8), LoreMode.PURCHASE);
        setItem(inventory, 11, ShopUtils.setItemName(new ItemStack(Material.IRON_CHESTPLATE, upgrades.ProtectionLevel+1), ProtectionName), (plr, inv) -> {
            if (upgrades.ProtectionLevel < 4 && isUpgradeBuyable(plr, CurrencyType.DIAMOND, TeamUtils.ProtectionPrices[upgrades.ProtectionLevel])) {
                TeamUtils.upgradeProtection(plr);
                sendUpgradeMessage(plr, "Reinforced Armor " + Utils.toRomanNumerics(upgrades.ProtectionLevel));
            }
            Base(inventory, player);
        }, getPrice(TeamUtils.ProtectionPrices[upgrades.ProtectionLevel]), new String[] { "Protection I, 2 Diamonds", "Protection II,  Diamonds", "Protection III, 8 Diamonds", "Protection IV, 16 Diamonds" }, true, LoreMode.PURCHASE);
        setItem(inventory, 12, ShopUtils.setItemName(new ItemStack(Material.GOLDEN_PICKAXE, upgrades.HasteLevel+1), ManiacMinerName), (plr, inv) -> {
            if (upgrades.HasteLevel < 2 && isUpgradeBuyable(plr, CurrencyType.DIAMOND, TeamUtils.HastePrices[upgrades.HasteLevel])) {
                TeamUtils.getTeamUpgrades(plr).HasteLevel++;
                sendUpgradeMessage(plr, "Maniac Miner " + Utils.toRomanNumerics(upgrades.HasteLevel));
            }
            Base(inventory, player);
        }, getPrice(TeamUtils.HastePrices[upgrades.HasteLevel]), new String[] { "Haste I, 2 Diamonds", "Haste II, 4 Diamonds" }, true, LoreMode.PURCHASE);
        setItem(inventory, 13, ShopUtils.setItemName(new ItemStack(Material.FURNACE, upgrades.ForgeLevel+1), IronForgeName), (plr, inv) -> {
            if (upgrades.ForgeLevel < 4 && isUpgradeBuyable(plr, CurrencyType.DIAMOND, TeamUtils.IronForgePrices[upgrades.ForgeLevel])) {
                int forgeLevel = TeamUtils.getTeamUpgrades(plr).ForgeLevel++;
                sendUpgradeMessage(plr, "Iron Forge " + Utils.toRomanNumerics(forgeLevel));
            }
            Base(inventory, player);
        }, getPrice(TeamUtils.IronForgePrices[upgrades.ForgeLevel]), new String[] { "+50% Resources, 2 Diamonds", "+100% Resources, 4 Diamonds", "Spawn emeralds, 6 Diamonds", "+200% Resources, 8 Diamonds"}, true, LoreMode.PURCHASE);
        setItem(inventory, 14, ShopUtils.setItemName(new ItemStack(Material.BEACON), "Heal Pool"), (plr, inv) -> {
            if (upgrades.healPoolLevel == 0 && isUpgradeBuyable(plr, CurrencyType.DIAMOND, 4)) {
                TeamUtils.getTeamUpgrades(plr).healPoolLevel = 1;
                sendUpgradeMessage(plr, "Heal Pool");
            }
            Base(inventory, player);
        }, upgrades.healPoolLevel == 1 ? "PURCHASED" : "4 DIAMOND", LoreMode.PURCHASE);
        setItem(inventory, 15, ShopUtils.setItemName(new ItemStack(Material.DRAGON_EGG), "Dragon Buff"), (plr, inv) -> {
            if (upgrades.DragonBuffs != 1 && isUpgradeBuyable(plr, CurrencyType.DIAMOND, 5)) {
                TeamUtils.getTeamUpgrades(plr).DragonBuffs++;
                sendUpgradeMessage(plr, "Dragon Buff");
            }
            Base(inventory, player);
        }, "5 DIAMOND", LoreMode.PURCHASE);
        setItem(inventory, 16, ShopUtils.setItemName(new ItemStack(Material.LEATHER), "Traps"), (plr, inv) -> {
            Traps(inventory, player);
        }, "", LoreMode.PURCHASE);
        ShopUtils.fillRow(inventory, new ItemStack(Material.GRAY_STAINED_GLASS_PANE), 18, 27);

        setItem(inventory, 30, upgrades.getTrapItem(0), (plr, inv) -> {
            Traps(inventory, player);
        });
        setItem(inventory, 31, upgrades.getTrapItem(1), (plr, inv) -> {
            Traps(inventory, player);
        });
        setItem(inventory, 32, upgrades.getTrapItem(2), (plr, inv) -> {
            Traps(inventory, player);
        });
    }

    public void Traps(Inventory inventory, Player player) {
        inventory.clear();
        clearActions();
        TeamUpgrades upgrades = BedwarsRunner.isRunning ? TeamUtils.getTeamUpgrades(player) : new TeamUpgrades();
        if (!upgrades.canAddTrap()) {
            Base(inventory, player);
            player.sendMessage(Utils.text("All trap slots are full.", TextColor.color(255, 0, 0)));
            return;
        }
        
        setItem(inventory, 10, ShopUtils.setItemName(new ItemStack(Material.FEATHER), "Alert Trap"), (plr, inv) -> {
            if (isUpgradeBuyable(plr, CurrencyType.DIAMOND, 1)) {
                TeamUtils.getTeamUpgrades(plr).addTrap(TeamTraps.Alert);
                sendUpgradeMessage(plr, "Alert Trap");
            }
            Base(inventory, player);
        }, "1 DIAMOND", LoreMode.PURCHASE);
        setItem(inventory, 11, ShopUtils.setItemName(new ItemStack(Material.GOLDEN_PICKAXE), "Mining Fatigue Trap"), (plr, inv) -> {
            if (isUpgradeBuyable(plr, CurrencyType.DIAMOND, 1)) {
                TeamUtils.getTeamUpgrades(plr).addTrap(TeamTraps.MiningFatigue);
                sendUpgradeMessage(plr, "Mining Fatigue Trap");
            }
            Base(inventory, player);
        }, "1 DIAMOND", LoreMode.PURCHASE);
        setItem(inventory, 12, ShopUtils.setItemName(new ItemStack(Material.ENDER_EYE), "Blindness Trap"), (plr, inv) -> {
            if (isUpgradeBuyable(plr, CurrencyType.DIAMOND, 1)) {
                TeamUtils.getTeamUpgrades(plr).addTrap(TeamTraps.Blindness);
                sendUpgradeMessage(plr, "Blindness Trap");
            }
            Base(inventory, player);
        }, "1 DIAMOND", LoreMode.PURCHASE);
    }

    public String getPrice(int price) {
        if (price == 0) return "PURCHASED";
        return price + " DIAMOND";
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
