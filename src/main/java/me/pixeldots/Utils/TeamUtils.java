package me.pixeldots.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.API.APIEventCaller;
import me.pixeldots.Game.BedData;
import me.pixeldots.Game.BedwarsGame;
import me.pixeldots.Game.data.PlayerStatistics;
import me.pixeldots.Game.data.TeamUpgrades;
import me.pixeldots.Game.data.TeamUpgrades.TeamTraps;
import net.kyori.adventure.text.format.TextColor;

public class TeamUtils {

    public static int[] ProtectionPrices = new int[] {2, 4, 8, 16, 0};
    public static int[] IronForgePrices = new int[] {2, 4, 6, 8, 0};
    public static int[] HastePrices = new int[] {2, 4, 0};

    public static void upgradeProtection(Player player) {
        upgradeProtection(BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team, player);
    }
    public static void upgradeProtection(int teamID, Player player) {
        getTeamUpgrades(teamID).ProtectionLevel++;
        String Team = BedwarsRunner.Variables.Teams.get(teamID);
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (PlayerUtils.getTeamColor(players.get(i)) == Team) {
                PlayerInventory inv = players.get(i).getInventory();
                ItemStack boots = inv.getBoots(); boots.setItemMeta(addUpgradeMeta(boots.getItemMeta(), boots, players.get(i))); inv.setBoots(boots);
                ItemStack leggings = inv.getLeggings(); leggings.setItemMeta(addUpgradeMeta(leggings.getItemMeta(), leggings, players.get(i))); inv.setLeggings(leggings);
                ItemStack chestplate = inv.getChestplate(); chestplate.setItemMeta(addUpgradeMeta(chestplate.getItemMeta(), chestplate, players.get(i))); inv.setChestplate(chestplate);
                ItemStack helmet = inv.getHelmet(); helmet.setItemMeta(addUpgradeMeta(helmet.getItemMeta(), helmet, players.get(i))); inv.setHelmet(helmet);
            }
        }
    }

    public static void upgradeSharpness(Player player) {
        upgradeSharpness(BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team, player);
    }
    public static void upgradeSharpness(int teamID, Player player) {
        getTeamUpgrades(teamID).SharpnessLevel++;
        String Team = BedwarsRunner.Variables.Teams.get(teamID);
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (PlayerUtils.getTeamColor(players.get(i)) == Team) {
                PlayerInventory inv = players.get(i).getInventory();
                ItemStack[] items = inv.getContents();
                for (int j = 0; j < items.length; j++) {
                    if (items[j] != null && items[j].getType().name().endsWith("_SWORD")) {
                        ItemStack stack = items[j]; stack.setItemMeta(addUpgradeMeta(stack.getItemMeta(), stack, players.get(i))); inv.setItem(j, stack);
                    }
                }
            }
        }
    }

    public static void upgradedMessage(Player to, Player upgrader, String purchased) {
        to.sendMessage(Utils.text(upgrader.getName() + " purchased ", TextColor.color(0, 255, 0)).append(Utils.text(purchased, TextColor.color(255, 255, 0))));
        to.playSound(to.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1, 1);
    }

    public static ItemMeta addUpgradeMeta(ItemMeta meta, ItemStack stack, Player player) {
        if (stack.getType().name().endsWith("_SWORD")) {
            TeamUpgrades upgrades = getTeamUpgrades(player);
            if (upgrades != null && upgrades.SharpnessLevel > 0) meta.addEnchant(Enchantment.DAMAGE_ALL, upgrades.SharpnessLevel, true);
        } else if (stack.getType().name().endsWith("_LEGGINGS") || stack.getType().name().endsWith("_HELMET") || stack.getType().name().endsWith("_CHESTPLATE") || stack.getType().name().endsWith("_BOOTS")) {
            TeamUpgrades upgrades = getTeamUpgrades(player);
            if (upgrades != null && upgrades.ProtectionLevel > 0) meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, upgrades.ProtectionLevel, true);
        }
        return meta;
    }

    public static void triggerTeamTrap(int TeamID, Player triggerer) {
        TeamUpgrades upgrades = getTeamUpgrades(TeamID);
        long time = Utils.getDateTime();
        if (upgrades.lastTrapTriggered+Utils.toMillisecondTime("1m") > time) return;
        upgrades.lastTrapTriggered = time;

        String trapName = upgrades.Traps[0].name();
        switch (upgrades.Traps[0]) {
            case None:
                return;
            case Alert:
                break;
            case MiningFatigue:
                trapName = "Miner Fatigue";
                triggerer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 30*20, 1));
                break;
            case Blindness:
                triggerer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30*20, 1));
                break;
            default:
                return;
        }
        List<UUID> players = BedwarsRunner.Variables.PlayersInGame;
        for (int i = 0; i < players.size(); i++) {
            if (BedwarsRunner.Variables.PlayerStats.get(players.get(i)).team != TeamID) continue;
            PlayerUtils.sendTitle(Bukkit.getPlayer(players.get(i)), "TRAP TRIGGERED!", "Your " + trapName + " Trap has been set off!", TextColor.color(255, 0, 0), TextColor.color(255, 255, 255));
        }

        upgrades.Traps[0] = upgrades.Traps[1];
        upgrades.Traps[1] = upgrades.Traps[2];
        upgrades.Traps[2] = TeamTraps.None;
    }

    public static void BedDestruction(Player player, String color) {
        String plrName = player.getName();
        ChatColor plrColor = Utils.getChatColor(PlayerUtils.getTeamColor(player));

        Utils.broadcastChat(ChatColor.BOLD + "BED DESTRUCTION > " + ChatColor.RESET + Utils.getChatColor(color) + TextUtils.upperCaseFirst(color) + " Bed" + ChatColor.WHITE + " was destroyed by " + plrColor + plrName + "!", TextColor.color(255, 255, 255));
    }
    public static void breakTeamBed(int i, int j) {
        BedData bed = BedwarsRunner.Variables.getTeamBeds(i).get(j);
        Location locationA = new Location(BedwarsRunner.world, bed.pos.getX(), bed.pos.getY(), bed.pos.getZ());
        Location locationB = locationA.clone().add(BlockFace.valueOf(bed.facing).getDirection());

        locationA.getBlock().setType(Material.AIR);
        locationB.getBlock().setType(Material.AIR);
        
        BedwarsGame.TeamBedDestroyed(i, j);
    }
    public static void breakAllBeds() {
        for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
            for (int j = 0; j < BedwarsRunner.Variables.getTeamBeds(i).size(); j++) {
                breakTeamBed(i, j);
            }
        }
    }

    public static void TeamEliminated(String team, int teamID) {
        String eliminated = ChatColor.BOLD + "TEAM ELIMINATED > " + ChatColor.RESET;
        String teamEliminated = Utils.getChatColor(team) + TextUtils.upperCaseFirst(team) + " Team" + ChatColor.RED;
        String hasbeen = " has been eliminated!";
        Utils.broadcastChat(eliminated + teamEliminated + hasbeen, TextColor.color(255, 255, 255));

        APIEventCaller.teamEliminated(teamID, getPlayersOnTeam(teamID));
        if (BedwarsRunner.isTesting) return;

        int teamCount = 0;
        int teamLeft = -1;
        for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
            if (isTeamAlive(i)) { 
                teamCount++;
                teamLeft = (teamCount == 1 ? i : -1);
            }
        }

        if (teamLeft == -1) return;

        List<UUID> PlayerWon = new ArrayList<>();
        List<UUID> PlayerLost = new ArrayList<>();

        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team == teamLeft) {
                PlayerUtils.sendTitle(player, "Victory!", "", TextColor.color(255, 200, 0), null);
                PlayerWon.add(player.getUniqueId());
            }
            else {
                PlayerUtils.sendTitle(player, "Defeat", "", TextColor.color(255, 0, 0), null);
                PlayerLost.add(player.getUniqueId());
            }
            Vector lobbyPos = BedwarsRunner.Variables.LobbyPosition;

            player.teleport(new Location(BedwarsRunner.world, lobbyPos.getX(), lobbyPos.getY(), lobbyPos.getZ()));
            player.setGameMode(GameMode.SURVIVAL);

            String teamName = BedwarsRunner.Variables.Teams.get(teamLeft);
            player.sendMessage(Utils.text(TextUtils.upperCaseFirst(teamName), Utils.getTextColor(teamName)).append(Utils.text(" has won the game", TextColor.color(255, 255, 255))));
        }
        APIEventCaller.gameEnd(teamLeft, PlayerWon, PlayerLost);
        BedwarsRunner.endGame();
    }

    public static TeamUpgrades getTeamUpgrades(Player player) {
        return BedwarsRunner.Variables.TeamUpgrades.get(BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team);
    }
    public static TeamUpgrades getTeamUpgrades(int TeamID) {
        return BedwarsRunner.Variables.TeamUpgrades.get(TeamID);
    }

    public static boolean canUpgradeTeam(String upgrade, Player player) {
        TeamUpgrades upgrades = getTeamUpgrades(player);
        switch (upgrade.toLowerCase()) {
            case "protection":
                return !(upgrades.ProtectionLevel >= BedwarsConf.maxTeamProtection);
            case "sharpness":
                return !(upgrades.SharpnessLevel >= BedwarsConf.maxTeamSharpness);
            case "forge":
                return !(upgrades.ForgeLevel >= BedwarsConf.maxTeamForgeLevel);
            case "haste":
                return !(upgrades.HasteLevel >= BedwarsConf.maxTeamHasteLevel);
            case "dragonbuff":
                return !(upgrades.DragonBuffs >= BedwarsConf.maxTeamDragonBuffs);
            case "healpool":
                return !(upgrades.healPoolLevel >= BedwarsConf.maxTeamHealPoolLevel);
            case "trap.alert":
                return upgrades.canAddTrap();
            case "trap.miningfatigue":
                return upgrades.canAddTrap();
            case "trap.blindness":
                return upgrades.canAddTrap();
        }
        return false;
    }
    public static int modifyTeamUpgradeLevel(String upgrade, Player player) {
        TeamUpgrades upgrades = getTeamUpgrades(player);
        switch (upgrade.toLowerCase()) {
            case "protection":
                return upgrades.ProtectionLevel++;
            case "sharpness":
                return upgrades.SharpnessLevel++;
            case "forge":
                return upgrades.ForgeLevel++;
            case "haste":
                return upgrades.HasteLevel++;
            case "dragonbuff":
                return upgrades.DragonBuffs++;
            case "healpool":
                return upgrades.healPoolLevel++;
            case "trap.alert":
                upgrades.addTrap(TeamTraps.Alert);
                return 0;
            case "trap.miningfatigue":
                upgrades.addTrap(TeamTraps.MiningFatigue);
                return 0;
            case "trap.blindness":
                upgrades.addTrap(TeamTraps.Blindness);
                return 0;
        }
        return -1;
    }
    public static int getTeamUpgradeLevel(String upgrade, Player player) {
        TeamUpgrades upgrades = getTeamUpgrades(player);
        switch (upgrade.toLowerCase()) {
            case "protection":
                return upgrades.ProtectionLevel;
            case "sharpness":
                return upgrades.SharpnessLevel;
            case "forge":
                return upgrades.ForgeLevel;
            case "haste":
                return upgrades.HasteLevel;
            case "dragonbuff":
                return upgrades.DragonBuffs;
            case "healpool":
                return upgrades.healPoolLevel;
        }
        return 0;
    }

    public static boolean isTeamAlive(int team) {
        if (BedwarsRunner.Variables.canTeamRespawn(team)) return true;

        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId());
            if (stats.team == team) {
                if (stats.isDead == null || (stats.isDead != null && stats.isDead.hasBed == true)) return true;
            }
        }
        return false;
    }

    public static boolean teamHasPlayers(int team) {
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (BedwarsRunner.Variables.PlayerStats.get(players.get(i).getUniqueId()).team == team) return true;
        }
        return false;
    }
    public static List<UUID> getPlayersOnTeam(int team) {
        List<UUID> playersOnTeam = new ArrayList<>();
        for (int i = 0; i < BedwarsRunner.Variables.PlayersInGame.size(); i++) {
            if (BedwarsRunner.Variables.PlayerStats.get(BedwarsRunner.Variables.PlayersInGame.get(i)).team == team)
                playersOnTeam.add(BedwarsRunner.Variables.PlayersInGame.get(i));
        }
        return playersOnTeam;
    }

}
