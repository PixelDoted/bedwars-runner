package me.pixeldots.Events;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.Extras.BedwarsChatRenderer;
import me.pixeldots.Game.AsyncBedwarsGameTicker;
import me.pixeldots.Game.BedwarsGame;
import me.pixeldots.Game.data.PlayerStatistics.PlayerDead;
import me.pixeldots.Scoreboard.GameScoreboardUtils;
import me.pixeldots.Scoreboard.ScoreboardUtils;
import me.pixeldots.Game.data.PlayerStatistics;
import me.pixeldots.Shops.InventoryHandler;
import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Utils.BlockUtils;
import me.pixeldots.Utils.NPCUtils;
import me.pixeldots.Utils.PlayerUtils;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null) {
            if (e.getItem().getType() == Material.FIRE_CHARGE) {
                Player player = e.getPlayer();
                Location newLocation = player.getLocation().toVector().add(player.getEyeLocation().getDirection().multiply(2)).add(new Vector(0, 1, 0)).toLocation(BedwarsRunner.world).setDirection(player.getLocation().getDirection());

                Fireball entity = (Fireball)player.getWorld().spawnEntity(newLocation, EntityType.FIREBALL);
                entity.setCustomName("a Fireball Thrown by " + player.getCustomName());
                entity.setCustomNameVisible(false);
                entity.setYield(2);
                e.getItem().subtract();
            } else if (Utils.text(e.getItem()).equalsIgnoreCase("team selector")) {
                InventoryHandler.getTeamSelector(e.getPlayer());
            } else if (Utils.text(e.getItem()).equalsIgnoreCase("compass")) {
                InventoryHandler.getCompass(e.getPlayer());
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && Utils.text(e.getItem().getItemMeta().displayName()).equalsIgnoreCase("Dream Defender")) {
                NPCUtils.spawnDreamDefender(e.getClickedBlock().getLocation().add(0, 1, 0), e.getPlayer());
                e.getItem().subtract();
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!BedwarsRunner.isRunning) return;
        Player player = e.getPlayer();

        PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId());
        boolean hasBed = BedwarsRunner.Variables.canTeamRespawn(stats.team);
        stats.isDead = new PlayerDead(Utils.getDateTime()+(BedwarsConf.respawnTime*1000), hasBed);
        stats.trackingTeam = -1;
        
        player.setGameMode(GameMode.SPECTATOR);
        if (player.getKiller() != null) {
            int TeamID = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team;
            boolean isFinal = !BedwarsRunner.Variables.canTeamRespawn(TeamID);
            PlayerUtils.takePlayerItems(player.getKiller(), player);
            String color = PlayerUtils.getTeamColor(player);
            String killerColor = PlayerUtils.getTeamColor(player.getKiller());
            e.deathMessage(PlayerUtils.getDeathMessage(Utils.getChatColor(color) + player.getName(), Utils.getChatColor(killerColor) + player.getKiller().getName(), isFinal));
            BedwarsGame.playerDied(player);
            if (BedwarsRunner.Variables.PlayerStats.containsKey(player.getKiller().getUniqueId())) {
                if (isFinal) BedwarsRunner.Variables.PlayerStats.get(player.getKiller().getUniqueId()).finalKills++;
                else BedwarsRunner.Variables.PlayerStats.get(player.getKiller().getUniqueId()).kills++;
                AsyncBedwarsGameTicker.actions.add(() -> { GameScoreboardUtils.UpdateStatistics(player.getKiller()); });
            }
        } else {
            PlayerUtils.takePlayerItems(null, player);
            String color = PlayerUtils.getTeamColor(player);
            e.deathMessage(Utils.text(Utils.getChatColor(color) + player.getName() + ChatColor.WHITE + " has died"));
            BedwarsGame.playerDied(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (!BedwarsRunner.isRunning) return;
        Vector pos = BedwarsRunner.Variables.SpectatorSpawn;
        e.setRespawnLocation(new Location(BedwarsRunner.world, pos.getX(), pos.getY(), pos.getZ()));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType() == EntityType.VILLAGER) {
            Villager villager = (Villager)e.getRightClicked();

            String name = Utils.text(villager.customName());
            if (name.equalsIgnoreCase("item shop")) {
                InventoryHandler.getItemShop(e.getPlayer());
            } else if (name.equalsIgnoreCase("upgrade shop")) {
                InventoryHandler.getUpgradeShop(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (BedwarsRunner.isStarting) e.setCancelled(true);
        Player player = e.getPlayer();
        PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId());
        if (stats.isInPVP != -1) {
            long isInPVP = stats.isInPVP;
            if (Utils.getDateTime() >= isInPVP)
                stats.isInPVP = -1;
            else e.setCancelled(true);
        }

        if (e.getItemDrop().getItemStack().getType().name().toLowerCase().endsWith("_sword")) {
            if (!ShopUtils.playerHasItem(e.getPlayer(), "SWORD")) {
                if (e.getItemDrop().getItemStack().getType() == Material.WOODEN_SWORD) { e.setCancelled(true); return; }
                ItemStack stack = new ItemStack(Material.WOODEN_SWORD);
                ItemMeta meta = TeamUtils.addUpgradeMeta(stack.getItemMeta(), stack, e.getPlayer()); 
                meta.setUnbreakable(true); stack.setItemMeta(meta);
                e.getPlayer().getInventory().addItem(stack); 
            }
        }
    }
    
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.MILK_BUCKET) {
            PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(e.getPlayer().getUniqueId());
            stats.magicMilkTime = Utils.getDateTime()+Utils.toMillisecondTime(BedwarsConf.magicMilkDuration);
        }
        e.setReplacement(new ItemStack(Material.AIR));
    }

    @EventHandler
    public void onPlayerPickUpItem(PlayerAttemptPickupItemEvent e) {
        ItemStack stack = e.getItem().getItemStack();
        Material type = stack.getType();
        if (BlockUtils.isBedBlock(type)) { 
            e.getItem().remove(); 
            e.setCancelled(true);
        } else if (type.name().endsWith("_sword")) {
            PlayerInventory inv = e.getPlayer().getInventory();
            inv.remove(Material.WOODEN_SWORD);
        } else if ((type == Material.IRON_INGOT || type == Material.GOLD_INGOT || type == Material.EMERALD) && stack.getItemMeta().isUnbreakable()) {
            Player player = e.getPlayer();
            ItemMeta meta = stack.getItemMeta(); meta.setUnbreakable(false);
            stack.setItemMeta(meta);

            List<Player> players = PlayerUtils.getPlayersInRange(player);
            if (players.size() <= 0) return;
            float dividerCount = stack.getAmount()/(float)players.size();
            int collectorCut = (int)Math.ceil(dividerCount);
            int otherCut = (int)Math.floor(dividerCount);

            player.getInventory().addItem(new ItemStack(type, collectorCut));
            for (int i = 0; i < players.size(); i++) {
                PlayerInventory inv = players.get(i).getInventory();
                inv.addItem(new ItemStack(type, otherCut));
            }
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        if (!BedwarsRunner.isRunning) return;
        if ((!e.getBlockClicked().isSolid() && e.getBucket() == Material.WATER_BUCKET)) {
            e.setCancelled(true);
            return;
        }
        e.setItemStack(new ItemStack(Material.AIR));
        Location location = e.getBlock().getLocation();
        Vector pos = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        BedwarsRunner.Variables.BlocksPlaced.add(pos);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (BedwarsRunner.Variables.PlayersInGame.contains(e.getPlayer().getUniqueId())) {
            Utils.runDelayedTask(() -> {
                e.getPlayer().damage(e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()*2);

                int teamID = BedwarsRunner.Variables.PlayerStats.get(e.getPlayer().getUniqueId()).team;
                PlayerUtils.setPlayerNameColor(e.getPlayer(), BedwarsRunner.Variables.Teams.get(teamID));
                if (BedwarsRunner.isRunning) ScoreboardUtils.RegisterGameBoard();
                else if (BedwarsRunner.isStarting) ScoreboardUtils.RegisterLobbyBoard();
            }, 2);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (BedwarsRunner.Variables.PlayerStats.containsKey(e.getPlayer().getUniqueId()))
            BedwarsGame.playerDied(e.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)  {
        Player player = e.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) && player.getLocation().subtract(0, .1, 0).getBlock().isSolid()) {
            player.spawnParticle(Particle.WHITE_ASH, player.getLocation(), 3);
        }
    }

    @EventHandler
    public void onPlayerThrowEgg(PlayerEggThrowEvent e) {
        e.setHatching(false);
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e) {
        e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent e) {
        e.setSpawnLocation(false);
    }

    @EventHandler
    public void onPlayerSaturation(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        if (!BedwarsRunner.Variables.PlayersInGame.contains(e.getPlayer().getUniqueId())) return;
        Player player = e.getPlayer();
        String team = BedwarsRunner.Variables.getTeamColor(player.getUniqueId());
        
        Component teamPrefix = Utils.text("[" + team.toUpperCase() + "] " + ChatColor.RESET, Utils.getTextColor(team));
        BedwarsChatRenderer renderer = new BedwarsChatRenderer(e.renderer(), teamPrefix);
        e.renderer(renderer);
    }

    @EventHandler
    public void onPlayerEntityPortal(PlayerPortalEvent e) {
        e.setCancelled(true);
    }

}
