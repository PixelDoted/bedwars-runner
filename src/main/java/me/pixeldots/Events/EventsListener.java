package me.pixeldots.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.Extras.BedBugEntity;
import me.pixeldots.Extras.BridgeEggEntity;
import me.pixeldots.Extras.DreamDefenderEntity;
import me.pixeldots.Extras.PopUpTowerData;
import me.pixeldots.Game.AsyncBedwarsGameTicker;
import me.pixeldots.Game.BedData;
import me.pixeldots.Game.BedwarsGame;
import me.pixeldots.Game.data.PlayerStatistics;
import me.pixeldots.Game.data.WorldBlockData;
import me.pixeldots.Scoreboard.GameScoreboardUtils;
import me.pixeldots.Shops.ShopUtils;
import me.pixeldots.Utils.BlockUtils;
import me.pixeldots.Utils.NPCUtils;
import me.pixeldots.Utils.PlayerUtils;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class EventsListener implements Listener {

    // Block
    @EventHandler
    public void onBlockExplode(EntityExplodeEvent e) {
        if (!(BedwarsRunner.isRunning || BedwarsRunner.isStarting)) return;
        for (Block b : new ArrayList<Block>(e.blockList())) {
            if (BlockUtils.isBedBlock(b.getType()) || BlockUtils.isGlassBlock(b.getType())) e.blockList().remove(b);
            else if (!BedwarsRunner.Variables.BlocksPlaced.contains(BlockUtils.getVector(b.getLocation()))) {
                if (BedwarsConf.canDestroyWorld) 
                    BedwarsRunner.Variables.WorldBlocksDestroyed.add(new WorldBlockData(b.getType(), b.getLocation(), b.getBlockData()));
                else e.blockList().remove(b);
            }
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (BedwarsRunner.isStarting) e.setCancelled(true);
        if (!BedwarsRunner.isRunning) return;

        Material type = e.getBlock().getType();
        Block block = e.getBlock();
        if (BlockUtils.isBedBlock(type)) {
            Player player = e.getPlayer();
            String blockColor = block.getType().name().replace("_BED", "").toLowerCase();

            if (blockColor.equals(PlayerUtils.getTeamColor(player))) {
                e.setCancelled(true);
                return;
            }
            BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).bedsBroken++;
            AsyncBedwarsGameTicker.actions.add(() -> { GameScoreboardUtils.UpdateStatistics(player); });
            for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
                BedData bed = BedwarsRunner.Variables.getTeamBed(i, block.getLocation());

                if (bed == null || !blockColor.equals(bed.color)) continue;
                TeamUtils.BedDestruction(player, bed.color);
                BedwarsGame.TeamBedDestroyed(i, bed.id);
            }
        } else if (!BedwarsRunner.Variables.BlocksPlaced.contains(BlockUtils.getVector(block.getLocation()))) {
            if (BedwarsConf.canDestroyWorld) BedwarsRunner.Variables.WorldBlocksDestroyed.add(new WorldBlockData(type, block.getLocation(), block.getBlockData()));
            else e.setCancelled(true);
        } else {
            Location location = block.getLocation();
            Vector pos = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            BedwarsRunner.Variables.BlocksPlaced.remove(pos);
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
        if (BedwarsRunner.isStarting) e.setCancelled(true);
        if (!BedwarsRunner.isRunning) return;
        
        if (BlockUtils.isBlockOutOfBounds(e.getBlock().getLocation())) {
            e.getPlayer().sendMessage(Utils.text("You have reached the build limit!", TextColor.color(255, 22, 22))); 
            e.setCancelled(true);
        } else if (e.getBlockPlaced().getType() == Material.TNT) {
            Location location = e.getBlockPlaced().getLocation();
            location.getWorld().getBlockAt(location).setType(Material.AIR);
            location.add(.5f, .5f, .5f);
            location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        } else if (e.getBlockPlaced().getType() == Material.CHEST && Utils.text(e.getItemInHand().getItemMeta().displayName()).equalsIgnoreCase("Compact Pop-Up Tower")) {
            e.setCancelled(true);
            e.getItemInHand().subtract();
            int TeamID = BedwarsRunner.Variables.PlayerStats.get(e.getPlayer().getUniqueId()).team;
            Location location = e.getBlockPlaced().getLocation();
            Vector pos = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            BlockFace facing = ((Chest)e.getBlockPlaced().getBlockData()).getFacing();
            PopUpTowerData PopUpTower = new PopUpTowerData(facing, TeamID, pos);
            BedwarsRunner.Variables.PopUpTowers.add(PopUpTower);
        } else {
            Location location = e.getBlockPlaced().getLocation();
            Vector pos = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            BedwarsRunner.Variables.BlocksPlaced.add(pos);
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent e) {
        if (!(BedwarsRunner.isRunning || BedwarsRunner.isStarting)) return;
        List<Item> items = e.getItems();
        for (int i = items.size()-1; i >= 0; i--) {
            if (BlockUtils.isBedBlock(items.get(i).getItemStack().getType())) items.remove(i);
        }
    }

    // Entity
    /*@EventHandler
    public void onDragonChangePhase(EnderDragonChangePhaseEvent e) {
        if (e.getNewPhase() == Phase.FLY_TO_PORTAL || e.getNewPhase() == Phase.LAND_ON_PORTAL) {
            int playerID = Utils.randomRange(0, BedwarsRunner.world.getPlayerCount()-1);
            Player player = BedwarsRunner.world.getPlayers().get(playerID);
            
            EnderDragon dragon = e.getEntity();
            dragon.setPhase(Phase.CHARGE_PLAYER);
            dragon.setTarget(player);
            e.setCancelled(true);
        }
    }*/

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (BlockUtils.isBlockOutOfBounds(e.getBlock().getLocation())) {
            e.setCancelled(true);
        } else if (e.getTo() == Material.AIR) {
            if (!BedwarsRunner.Variables.BlocksPlaced.contains(BlockUtils.getVector(e.getBlock().getLocation()))) {
                Block block = e.getBlock();
                WorldBlockData data = new WorldBlockData(block.getType(), block.getLocation(), e.getBlockData());
                BedwarsRunner.Variables.WorldBlocksDestroyed.add(data);
            }
        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getAction() == EntityPotionEffectEvent.Action.REMOVED) {
                if (e.getOldEffect().getType() == PotionEffectType.INVISIBILITY) 
                    BedwarsRunner.invisibilityHandler.sendVisiblePacket((Player)e.getEntity());
            } else if (e.getAction() == EntityPotionEffectEvent.Action.ADDED) {
                if (e.getNewEffect().getType() == PotionEffectType.INVISIBILITY) 
                    BedwarsRunner.invisibilityHandler.sendInvisiblePacket((Player)e.getEntity());
            }
        }
    }

    @EventHandler
    public void onEntityAttackEntity(EntityDamageByEntityEvent e) {
        if (BedwarsRunner.isStarting) e.setCancelled(true);
        if (!BedwarsRunner.isRunning) return;

        if (e.getDamager().getType() == EntityType.FIREBALL || e.getDamager().getType() == EntityType.PRIMED_TNT) {
            e.setDamage(4);
            Vector hitDifference = e.getEntity().getLocation().toVector().subtract(e.getDamager().getLocation().toVector());
            Vector hitDirection = hitDifference.normalize();
            double hitDistance = Math.sqrt(hitDifference.getX()*hitDifference.getX()+hitDifference.getY()*hitDifference.getY()+hitDifference.getZ()*hitDifference.getZ());
            e.getEntity().setVelocity(e.getEntity().getVelocity().add(hitDirection.multiply( hitDistance == 0 ? 2 : 1/(hitDistance+1) ).multiply(2)));
        }
        if (e.getEntity() instanceof IronGolem) {
            for (int i = 0; i < BedwarsRunner.Variables.DreamDefenders.size(); i++) {
                DreamDefenderEntity obj = BedwarsRunner.Variables.DreamDefenders.get(i);
                if (obj.entity != (IronGolem)e.getEntity()) continue;
                if (e.getDamager() instanceof Player && !BedwarsConf.friendlyFire && obj.teamID == BedwarsRunner.Variables.PlayerStats.get(((Player)e.getDamager()).getUniqueId()).team) {
                    e.setCancelled(true);
                    return;
                } else {
                    obj.spawnTime -= e.getFinalDamage()*Utils.toMillisecondTime(BedwarsConf.dreamDefenderAttackedMultiplyer);
                    e.setDamage(1);
                }
            }
        } else if (e.getEntity() instanceof Silverfish && e.getDamager() instanceof Player && !PlayerUtils.canPlayerAttackBedBug((Player)e.getDamager(), (Silverfish)e.getEntity())) {
            e.setCancelled(true);
            return;
        }
        
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getDamager() instanceof IronGolem) e.setDamage(BedwarsConf.dreamDefenderBaseDamage);
        Player player = (Player)e.getEntity();

        PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId());
        stats.isInPVP = Utils.getDateTime()+BedwarsConf.playerPVPTime;
        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow)e.getDamager();
            if (arrow.getShooter() instanceof Player) {
                String teamColor = BedwarsRunner.Variables.Teams.get(stats.team);
                String health = String.valueOf(Math.round( (((Player)e.getEntity()).getHealth()-e.getFinalDamage())*10)/10f);
                Component message = Utils.text(player.getName(), Utils.getTextColor(teamColor)).append(Utils.text(" is on ", TextColor.color(255, 255, 255))).append(Utils.text(health, TextColor.color(255, 22, 22))).append(Utils.text(" HP!", TextColor.color(255, 255, 255)));
                ((Player)arrow.getShooter()).sendMessage(message);
            }
        }

        if (!(e.getDamager() instanceof Player)) return;
        Player attacker = (Player)e.getDamager();

        if (!BedwarsConf.friendlyFire && BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team == BedwarsRunner.Variables.PlayerStats.get(attacker.getUniqueId()).team) {
            e.setCancelled(true);
            return;
        }
        if (player.getPotionEffect(PotionEffectType.INVISIBILITY) != null) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            BedwarsRunner.invisibilityHandler.sendVisiblePacket(player);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(BedwarsRunner.isRunning || BedwarsRunner.isStarting)) return;
        Projectile projectile = e.getEntity();
        if (projectile instanceof Egg && projectile.getShooter() instanceof Player) {
            BridgeEggEntity egg = new BridgeEggEntity();
            egg.entity = (Egg)projectile;
            egg.teamID = BedwarsRunner.Variables.PlayerStats.get(((Player)projectile.getShooter()).getUniqueId()).team;
            BedwarsRunner.Variables.BridgeEggs.add(egg);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(BedwarsRunner.isRunning || BedwarsRunner.isStarting)) return;
        Projectile entity = e.getEntity();
        if (entity instanceof Egg) {
            for (int i = 0; i < BedwarsRunner.Variables.BridgeEggs.size(); i++) {
                if (BedwarsRunner.Variables.BridgeEggs.get(i).entity == (Egg)entity) {
                    BedwarsRunner.Variables.BridgeEggs.remove(i);
                    break;
                }

            }
        } else if (entity instanceof Snowball && entity.getShooter() instanceof Player) {
            Player player = (Player)entity.getShooter();
            NPCUtils.spawnBedBug(entity.getLocation(), player);
        }
    }

    @EventHandler
    public void onEntityTargetEntity(EntityTargetLivingEntityEvent e) {
        if (!(BedwarsRunner.isRunning || BedwarsRunner.isStarting)) return;

        LivingEntity target = e.getTarget();
        if (!(target instanceof Player)) {
            if (target instanceof Silverfish && e.getEntity() instanceof IronGolem) e.setCancelled(true);
            return;
        }

        int targetedTeamID = BedwarsRunner.Variables.PlayerStats.get(((Player)e.getTarget()).getUniqueId()).team;
        if (e.getEntity() instanceof Silverfish) {
            for (int i = 0; i < BedwarsRunner.Variables.BedBugs.size(); i++) {
                BedBugEntity bedBug = BedwarsRunner.Variables.BedBugs.get(i);
                if (bedBug.entity == e.getEntity() && bedBug.teamID == targetedTeamID) {
                    e.setCancelled(true);
                    return;
                }
            }
        } else if (e.getEntity() instanceof IronGolem) {
            for (int i = 0; i < BedwarsRunner.Variables.DreamDefenders.size(); i++) {
                DreamDefenderEntity dreamDefender = BedwarsRunner.Variables.DreamDefenders.get(i);
                if (dreamDefender.entity == e.getEntity() && dreamDefender.teamID == targetedTeamID) {
                    e.setCancelled(true);
                    return;
                }
            }
        } /*else if (e.getEntity() instanceof EnderDragon) {
            for (int i = 0; i < BedwarsRunner.Variables.EnderDragons.size(); i++) {
                EnderDragonEntity enderDragon = BedwarsRunner.Variables.EnderDragons.get(i);
                if (enderDragon.entity != e.getEntity()) continue;
                if (enderDragon.teamID == -1) return;
                else if (enderDragon.teamID == targetedTeamID) {
                    e.setCancelled(true);
                    return;
                }
            }
        }*/ // TODO: READD THIS
    }
    
    // Inventory
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        Player player = (Player)e.getWhoClicked();
        InventoryView view = e.getView();
        String viewTitle = Utils.text(e.getView().title()).toLowerCase();

        if (ShopUtils.isShopInventory(viewTitle)) {
            if (e.getClickedInventory() == view.getTopInventory())
                ShopUtils.handleShop(view.getTopInventory(), player, e.getSlot(), viewTitle, e.isShiftClick());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player)e.getPlayer();
        PlayerInventory inv = player.getInventory();
        if (inv.getItemInOffHand() != null && inv.getItemInOffHand().getType() != Material.AIR) {
            player.getWorld().dropItem(player.getLocation(), inv.getItemInOffHand());
            inv.setItemInOffHand(null);
        }
    }

}
