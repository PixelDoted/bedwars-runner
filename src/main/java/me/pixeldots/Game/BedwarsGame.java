package me.pixeldots.Game;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.pixeldots.BedwarsConf;
import me.pixeldots.BedwarsRunner;
import me.pixeldots.API.APIEventCaller;
import me.pixeldots.Extras.BedBugEntity;
import me.pixeldots.Extras.BridgeEggEntity;
import me.pixeldots.Game.data.DirectionVector;
import me.pixeldots.Game.data.PlayerStatistics.PlayerDead;
import me.pixeldots.Game.data.PlayerStatistics;
import me.pixeldots.Game.data.TeamUpgrades;
import me.pixeldots.Game.data.WorldBlockData;
import me.pixeldots.Game.data.WorldInformation;
import me.pixeldots.Scoreboard.ScoreboardUtils;
import me.pixeldots.Shops.InventoryHandler;
import me.pixeldots.Utils.BlockUtils;
import me.pixeldots.Utils.NPCUtils;
import me.pixeldots.Utils.PlayerUtils;
import me.pixeldots.Utils.TeamUtils;
import me.pixeldots.Utils.ToolUtils;
import me.pixeldots.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;

public class BedwarsGame {

    public static long LastIronSpawn = 0;
    public static long LastDiamondSpawn = 0;
    public static long LastEmeraldSpawn = 0;

    public static void start() {
        BedwarsRunner.Variables.PlayerStats.clear();
        
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).getInventory().clear(); // Clear the player's inventory
            players.get(i).getEnderChest().clear(); // Clear the player's ender chest

            ItemStack teamSelector = new ItemStack(Material.CHEST); // create Chest ItemStack
            ItemMeta itemMeta = teamSelector.getItemMeta(); itemMeta.displayName(Utils.text("Team Selector"));
            teamSelector.setItemMeta(itemMeta);
            players.get(i).getInventory().addItem(teamSelector); // Give the teamselector to the player

            Vector pos = BedwarsRunner.Variables.LobbyPosition; // lobby position
            players.get(i).teleport(new Location(BedwarsRunner.world, pos.getX(), pos.getY(), pos.getZ())); // teleport player to the lobby
            players.get(i).setGameMode(GameMode.ADVENTURE); // set player's gamemode to adventure
            players.get(i).sendMessage("The game will start soon"); // send starting soon message
        }
        Utils.Logger().info("[DEBUG] Setting up the Lobby Scoreboard");
        
        ScoreboardUtils.RegisterLobbyBoard();
        BedwarsRunner.StartingTime = Utils.getDateTime()+(30*1000);
        BedwarsRunner.isStarting = true;
    }

    public static void run() {
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).getInventory().clear(); // Clear the player's inventory
            players.get(i).getEnderChest().clear(); // Clear the player's ender chest
        }
        InventoryHandler.playerInventorys.clear(); // clear all inventorys

        // Calculate Minimum and Maximum Area
        Vector areaStart = BedwarsRunner.Variables.AreaStart; 
        Vector areaEnd = BedwarsRunner.Variables.AreaEnd;
        BedwarsRunner.Variables.AreaMin = new Vector(Math.min(areaStart.getBlockX(), areaEnd.getBlockX()), Math.min(areaStart.getBlockY(), areaEnd.getBlockY()), Math.min(areaStart.getBlockZ(), areaEnd.getBlockZ()));
        BedwarsRunner.Variables.AreaMax = new Vector(Math.max(areaStart.getBlockX(), areaEnd.getBlockX()), Math.max(areaStart.getBlockY(), areaEnd.getBlockY()), Math.max(areaStart.getBlockZ(), areaEnd.getBlockZ()));
        // Calculate Minimum and Maximum Area

        try {
            Utils.Logger().info("[DEBUG] Setting up Diamond Generator Holograms");
            for (int i = 0; i < BedwarsRunner.Variables.DiamondSpawners.size(); i++) {
                Vector pos = BedwarsRunner.Variables.DiamondSpawners.get(i);
                NPCUtils.createDiamondHologram(pos, BedwarsRunner.world); // create Diamond Generator Hologram
            }
            Utils.Logger().info("[DEBUG] Setting up Emerald Generator Holograms");
            for (int i = 0; i < BedwarsRunner.Variables.EmeraldSpawners.size(); i++) {
                Vector pos = BedwarsRunner.Variables.EmeraldSpawners.get(i);
                NPCUtils.createEmeraldHologram(pos, BedwarsRunner.world); // create Emerald Generator Hologram
            }

            Utils.Logger().info("[DEBUG] Setting up Team Beds");
            BedwarsRunner.Variables.TeamHasBed.clear();
            for (int i = 0; i < BedwarsRunner.Variables.TeamBeds.size(); i++) {
                List<BedData> beds = BedwarsRunner.Variables.getTeamBeds(i);
                for (int j = 0; j < beds.size(); j++) {
                    BedData bed = beds.get(j);
                    Location footPos = new Location(BedwarsRunner.world, bed.pos.getX(), bed.pos.getY(), bed.pos.getZ());
                    if (!BlockUtils.isBedBlock(BedwarsRunner.world.getBlockAt(footPos).getType())) { // check if block doesn't contain a bed
                        Material mat = Material.getMaterial(bed.color.toUpperCase() + "_BED"); // get Colored Bed Material

                        BlockFace face = BlockFace.valueOf(bed.facing);
                        Block FootBlock = footPos.getBlock(); // Foot Block
                        FootBlock.setType(mat); // set Foot Material
                        Bed FootData = (Bed)FootBlock.getBlockData(); // Bed Block Data
                        FootData.setFacing(face); // set facing for Foot

                        Block HeadBlock = footPos.getBlock().getRelative(face); // Head Block
                        HeadBlock.setType(mat); // set Head Material
                        Bed HeadData = (Bed)HeadBlock.getBlockData(); // Bed Block Data
                        HeadData.setFacing(face); HeadData.setPart(Part.HEAD); // set facing and part for Head

                        BedwarsRunner.world.setBlockData(HeadBlock.getLocation(), HeadData); // update Head Data
                        BedwarsRunner.world.setBlockData(FootBlock.getLocation(), FootData); // update Foot Data
                    }
                    BedwarsRunner.Variables.addTeamHasBed(i, true, bed.id); // set Team Has Bed to true
                }

            }

            Utils.Logger().info("[DEBUG] Setting up Players");
            int nextAddTeam = 0;
            for (int i = 0; i < players.size(); i++) {
                int teamID = nextAddTeam;
                while (teamID >= BedwarsRunner.Variables.Teams.size()) {
                    teamID -= BedwarsRunner.Variables.Teams.size();
                }

                Player player = players.get(i);
                UUID uuid = player.getUniqueId();
                if (BedwarsRunner.Variables.Spectators.contains(uuid)) {
                    Vector spectator = BedwarsRunner.Variables.SpectatorSpawn;
                    player.teleport(new Location(player.getWorld(), spectator.getX(), spectator.getY(), spectator.getZ()));
                    continue;
                }
                if (!BedwarsRunner.Variables.PlayerStats.containsKey(uuid)) { // check if the player hasn't selected a team
                    BedwarsRunner.Variables.PlayerStats.put(uuid, new PlayerStatistics(teamID)); // create Player's Statistics for this Game
                    nextAddTeam++; // add 1 to nextAddTeam
                } else teamID = BedwarsRunner.Variables.PlayerStats.get(uuid).team; // set teamID to the player's selected team
                
                BedwarsRunner.Variables.PlayersInGame.add(uuid); // add the player to the game
                PlayerUtils.setPlayerNameColor(player, BedwarsRunner.Variables.Teams.get(teamID)); // update the player name

                DirectionVector vector = BedwarsRunner.Variables.getTeamSpawn(teamID); // get Team Spawn Position and Direction
                Location location = new Location(BedwarsRunner.world, vector.pos.getX(), vector.pos.getY(), vector.pos.getZ()); // create Location
                location.setYaw(vector.yaw); // set yaw
                location.setPitch(vector.pitch); // set pitch
                player.teleport(location); // teleport player to there base
                player.setGameMode(GameMode.SURVIVAL); // set player's gamemode to survival
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()); // set the player's health
                PlayerUtils.giveLeatherArmor(player, player.getInventory()); // give the player defualt items
            }
            BedwarsRunner.Variables.Spectators.clear();

            Utils.Logger().info("[DEBUG] Resetting Team Upgrades");
            BedwarsRunner.Variables.TeamUpgrades.clear();
            for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
                BedwarsRunner.Variables.TeamUpgrades.put(i, new TeamUpgrades()); // create Team Upgrades
            }

            Utils.Logger().info("[DEBUG] Setting up the Scoreboard");
            ScoreboardUtils.RegisterGameBoard(); // create the games scoreboard
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        BedwarsRunner.Variables.WorldInfo = new WorldInformation();
        BedwarsRunner.isRunning = true;

        if (BedwarsConf.eliminateEmptyTeams) {
            // Eliminate any teams with no players
            for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
                if (!TeamUtils.teamHasPlayers(i)) {
                    for (int j = 0; j < BedwarsRunner.Variables.getTeamBeds(i).size(); j++) {
                        TeamUtils.breakTeamBed(i, j);
                    }
                    TeamUtils.TeamEliminated(BedwarsRunner.Variables.Teams.get(i), i);
                }
            }
        }
    }

    public static void stop() {
        BedwarsRunner.isRunning = false;

        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) { // Reset player data
            Player player = players.get(i);
            player.playerListName(player.displayName()); // reset the players playerListName
            player.customName(player.displayName()); // reset the players customName
            //BedwarsRunner.invisibilityHandler.updatePlayerNameTag(player, Utils.text(player.displayName()));
            
            player.getInventory().clear(); // clear the players inventory
            player.getEnderChest().clear(); // clear the players enderchest

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType()); // remove all potion effects
            }
        }

        for (int i = 0; i < BedwarsRunner.Variables.BlocksPlaced.size(); i++) { // Remove placed blocks
            Vector pos = BedwarsRunner.Variables.BlocksPlaced.get(i);
            BedwarsRunner.world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).setType(Material.AIR);
        }

        if (BedwarsConf.canDestroyWorld) { // Replace destroyed world blocks
            for (int i = 0; i < BedwarsRunner.Variables.WorldBlocksDestroyed.size(); i++) {
                WorldBlockData data = BedwarsRunner.Variables.WorldBlocksDestroyed.get(i);
                Block block = data.blockPos.getBlock();
                block.setType(data.blockType);
                data.blockPos.getWorld().setBlockData(data.blockPos, data.blockData);
            }
            BedwarsRunner.Variables.WorldBlocksDestroyed.clear();
        }
        for (int i = 0; i < BedwarsRunner.Variables.HologramEntitys.size(); i++) { // Remove Holograms from world
            ArmorStand[] entitys = BedwarsRunner.Variables.HologramEntitys.get(i);
            for (int j = 0; j < entitys.length; j++) {
                entitys[j].remove();                
            }
        }

        List<Entity> entities = BedwarsRunner.world.getEntities();
        for (int i = 0; i < entities.size(); i++) { // Clear Entitys
            EntityType type = entities.get(i).getType();
            if (type == EntityType.DROPPED_ITEM || type == EntityType.ARROW || type == EntityType.IRON_GOLEM || 
                type == EntityType.EGG || type == EntityType.SNOWBALL || type == EntityType.SILVERFISH || 
                type == EntityType.PRIMED_TNT || type == EntityType.ENDER_DRAGON || type == EntityType.AREA_EFFECT_CLOUD) {
                entities.get(i).remove();
            }
        }
        // Clear all Game based Variables
        BedwarsRunner.Variables.PlayerStats.clear();
        BedwarsRunner.Variables.HologramEntitys.clear();
        BedwarsRunner.Variables.PlayersInGame.clear();
        BedwarsRunner.Variables.TeamUpgrades.clear();
        BedwarsRunner.Variables.TeamHasBed.clear();
        BedwarsRunner.Variables.BlocksPlaced.clear();
        
        BedwarsRunner.Variables.DreamDefenders.clear();
        BedwarsRunner.Variables.BedBugs.clear();
        BedwarsRunner.Variables.PopUpTowers.clear();
        BedwarsRunner.Variables.BridgeEggs.clear();
        Utils.Logger().info("Bedwars match Stopped");
    }
 
    public static void SpawnItems(long currentTime) throws Exception {
        if (LastIronSpawn < currentTime) {
            for (int i = 0; i < BedwarsRunner.Variables.Teams.size(); i++) {
                int forgeLevel = TeamUtils.getTeamUpgrades(i).ForgeLevel; // get team forge level
                List<Vector> gens = BedwarsRunner.Variables.getTeamGenerators(i); // get team generators
                for (int j = 0; j < gens.size(); j++) {
                    Vector vector = gens.get(j); // get generator vector
                    Location location = new Location(BedwarsRunner.world, vector.getX(), vector.getY(), vector.getZ()); // create location
                    boolean[] canSpawn = canSpawnTeamGenItem(location);

                    if (forgeLevel >= 3) { // check if the forge is level 3 or greater
                        if (forgeLevel == 3) forgeLevel--; // stop the forge from giving 150% at forgelevel 3
                        if (canSpawn[2] && Utils.randomRange(0, 100/((forgeLevel/2))) <= 5) { // randomize the chance of an emerald
                            Item item = BedwarsRunner.world.dropItem(location, ToolUtils.setUnbreakable(new ItemStack(Material.EMERALD))); // drop the emerald
                            if (!BedwarsConf.teamGeneratorItemsHaveVelocity) { // Remove velocity if true
                                item.setVelocity(new Vector(0,0,0)); // set velocity to 0,0,0
                                item.teleport(location); // teleport item to position
                            }
                        }
                    }
                    float forgeMultiplyer = 1+forgeLevel/2; // forge multiplyer leve;
                    if (canSpawn[0]) {
                        Item ironItem = BedwarsRunner.world.dropItem(location, ToolUtils.setUnbreakable(new ItemStack(Material.IRON_INGOT, Math.round((Utils.randomRange(1, 2) == 2 ? 2 : 1)*forgeMultiplyer)))); // drop iron
                        if (!BedwarsConf.teamGeneratorItemsHaveVelocity)  { // Remove velocity if true
                            ironItem.setVelocity(new Vector(0,0,0)); // set velocity to 0,0,0
                            ironItem.teleport(location); // teleport item to position
                        }
                    }
                    if (canSpawn[1] && Utils.randomRange(0, 100/forgeMultiplyer) <= 20) { // randomize the change of gold
                        Item item = BedwarsRunner.world.dropItem(location, ToolUtils.setUnbreakable(new ItemStack(Material.GOLD_INGOT))); // drop gold
                        if (!BedwarsConf.teamGeneratorItemsHaveVelocity) { // Remvove velocity if true
                            item.setVelocity(new Vector(0,0,0)); // set velocity to 0,0,0
                            item.teleport(location); // teleport item to position
                        }
                    }

                    if (BedwarsConf.debugTeamGenerators) {
                        BedwarsRunner.logger.info("[DEBUG] Atttempted to spawn items at " + location + " spawned, Iron: " + canSpawn[0] + ", Gold: " + canSpawn[1] + ", Emerald: " + canSpawn[2] + ", forge level: " + forgeLevel);
                    }
                }
            }
            LastIronSpawn = currentTime + Utils.toMillisecondTime(BedwarsConf.ironGenTime);//Math.round(1.5f*1000);
        }
        if (LastDiamondSpawn < currentTime) {
            for (int i = 0; i < BedwarsRunner.Variables.DiamondSpawners.size(); i++) {
                Vector vector = BedwarsRunner.Variables.DiamondSpawners.get(i); // get generator vector
                Location location = new Location(BedwarsRunner.world, vector.getX(), vector.getY(), vector.getZ()); // create location
                boolean can_spawn = canSpawnDiamondGenItem(location);
                if (can_spawn) {
                    Item item = BedwarsRunner.world.dropItem(location, new ItemStack(Material.DIAMOND)); // drop a diamond
                    item.setVelocity(new Vector(0,0,0)); // set velocity to 0,0,0
                    item.teleport(location); // teleport item to position
                }

                if (BedwarsConf.debugDiamondGenerators) {
                    BedwarsRunner.logger.info("[DEBUG] Attempted to spawn Diamond at " + location + " spawned: " + can_spawn);
                }
            }
            LastDiamondSpawn = currentTime + (long)Math.ceil(Utils.toMillisecondTime(BedwarsConf.diamondGenTime)/Utils.getDiamondDivider(BedwarsRunner.Variables.WorldInfo.diamondLevel));//(30*1000);
        }
        if (LastEmeraldSpawn < currentTime) {
            for (int i = 0; i < BedwarsRunner.Variables.EmeraldSpawners.size(); i++) {
                Vector vector = BedwarsRunner.Variables.EmeraldSpawners.get(i); // get generator vector
                Location location = new Location(BedwarsRunner.world, vector.getX(), vector.getY(), vector.getZ()); // create location 
                boolean can_spawn = canSpawnEmeraldGenItem(location);
                
                if (can_spawn) {
                    Item item = BedwarsRunner.world.dropItem(location, new ItemStack(Material.EMERALD)); // drop an emerald
                    item.setVelocity(new Vector(0,0,0)); // set velocity to 0,0,0
                    item.teleport(location); // teleport item to position
                }

                if (BedwarsConf.debugEmeraldGenerators) {
                    BedwarsRunner.logger.info("[DEBUG] Attempted to spawn Emerald at " + location + " spawned: " + can_spawn);
                }
            }
            LastEmeraldSpawn = currentTime + (long)Math.ceil(Utils.toMillisecondTime(BedwarsConf.emeraldGenTime)/Utils.getEmeraldDivider(BedwarsRunner.Variables.WorldInfo.emeraldLevel));//(60*1000);
        }
    }

    public static void TickPlayers(long currentTime) throws Exception {
        for (int i = 0; i < BedwarsRunner.Variables.PlayersInGame.size(); i++) {
            Player player = Bukkit.getPlayer(BedwarsRunner.Variables.PlayersInGame.get(i)); // get player from bukkit
            if (player == null || !BedwarsRunner.Variables.PlayerStats.containsKey(player.getUniqueId())) continue; // skip player if they don't exsist or arn't in the game
            PlayerStatistics stats = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()); // get the players stats
            int TeamID = stats.team; // get the players team ID

            
            if (stats.isDead != null) { // Player Died Handler
                PlayerDead status = stats.isDead; // get the players isdead info
                if (status.hasBed) PlayerUtils.titleDied(player, currentTime, status.timeDied); // send a title to the player if they have a bed
                if (status.hasBed && status.timeDied <= currentTime) {
                    stats.isDead = null; // set isdead to null
                    DirectionVector spawn = BedwarsRunner.Variables.getTeamSpawn(TeamID); // get the players team spawn
                    Location location = new Location(BedwarsRunner.world, spawn.pos.getX(), spawn.pos.getY(), spawn.pos.getZ(), spawn.yaw, spawn.pitch); // create location from team spawn
                    player.setGameMode(GameMode.SURVIVAL); // set the players gamemode to survival
                    player.teleport(location); // teleport the player to a team spawn
                }
            } else {
                TeamUpgrades upgrades = TeamUtils.getTeamUpgrades(TeamID); // get the players team upgrades
                if (upgrades.healPoolLevel > 0) {  // Heal Pool Handler
                    DirectionVector vector = BedwarsRunner.Variables.getTeamSpawn(TeamID); // get the team spawn vector
                    Location spawnLocation = new Location(BedwarsRunner.world, vector.pos.getX(), vector.pos.getY(), vector.pos.getZ()); // create Location from player spawn
                    if (player.getLocation().distance(spawnLocation) <= BedwarsConf.healPoolDistance) player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, upgrades.healPoolLevel-1)); // give the player regeneration if they're in range
                }
                if (upgrades.HasteLevel > 0) // Maniac Miner Handler
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20, upgrades.HasteLevel-1)); // give the player haste
                
                BedwarsPlayerTicker.updateDreamDefenderTarget(currentTime, player, stats);
                BedwarsPlayerTicker.updateTrapTrigger(currentTime, player, stats);
                BedwarsPlayerTicker.updatePlayerTracker(player, stats);
            }

            // Void Handler
            if (player.getLocation().getY() < BedwarsRunner.Variables.AreaMin.getBlockY()) player.damage(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()*2);
        }
    }

    public static void TickExtras(long currentTime) throws Exception {
        if (currentTime >= BedwarsRunner.Variables.WorldInfo.nextEventTime) { // Event Handler
            BedwarsRunner.Variables.WorldInfo.nextWorldEvent(); // update the world event if the time is up
        }
        
        for (int i = BedwarsRunner.Variables.BedBugs.size()-1; i >= 0; i--) { // Bed Bug Handler
            BedBugEntity bedBug = BedwarsRunner.Variables.BedBugs.get(i); // get a bedbug
            if (currentTime >= bedBug.spawnTime) { // check if the bedbug ran out of time
                bedBug.entity.remove(); // remove the bedbug
                BedwarsRunner.Variables.BedBugs.remove(bedBug); // remove the bedbug from the BedBugs list
            } else {
                int teamID = bedBug.teamID; // get the bedbugs teamID
                float timer = Math.round((bedBug.spawnTime-currentTime)/100f)/10f; // get the bedbugs timer
                bedBug.entity.customName( Utils.text("Bed Bug [ " + timer + "s ]", Utils.getTextColor(BedwarsRunner.Variables.Teams.get(teamID))) ); // update the bedbugs name
            }
        }

        for (int i = 0; i < BedwarsRunner.Variables.BridgeEggs.size(); i++) { // Bridge Egg Handler
            BridgeEggEntity egg = BedwarsRunner.Variables.BridgeEggs.get(i); // get a bridge egg
            Location location = egg.entity.getLocation().subtract(0, 2, 0); // get the bridge eggs location - 2y
            if (BlockUtils.isBlockOutOfBounds(location)) continue; // ignore the rest of this code if the location is out of bounds

            Block block = location.getBlock(); // get the block from location
            if (block.getType() == Material.AIR) { // check if the block is air
                block.setType(Material.valueOf((BedwarsRunner.Variables.Teams.get(egg.teamID) + "_wool").toUpperCase())); // set the block to wool
                BedwarsRunner.Variables.BlocksPlaced.add(new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ())); // add to blocks placed list
            }
            // repeat of the first, and rotates the location on Y by 90
            Location location2 = location.clone().add(location.getDirection().rotateAroundY(90));
            Block block2 = location2.getBlock();
            if (block2.getType() == Material.AIR) {
                block2.setType(Material.valueOf((BedwarsRunner.Variables.Teams.get(egg.teamID) + "_wool").toUpperCase()));
                BedwarsRunner.Variables.BlocksPlaced.add(new Vector(location2.getBlockX(), location2.getBlockY(), location2.getBlockZ()));
            }
        }
        for (int i = BedwarsRunner.Variables.PopUpTowers.size()-1; i >= 0; i--) { // Pop-Up Tower Handler
            BedwarsRunner.Variables.PopUpTowers.get(i).tick(); // tick the Pop-Up Tower
        }

        for (int i = 0; i < BedwarsRunner.Variables.HologramEntitys.size(); i++) { // Hologram Handler
            ArmorStand[] holograms = BedwarsRunner.Variables.HologramEntitys.get(i); // get armor stands from a hologram
            holograms[2].setRotation(BedwarsRunner.Variables.HologramRotation, 0); // update the 3rd armor stands rotation

            if (holograms[2].getItem(EquipmentSlot.HEAD).getType() == Material.DIAMOND_BLOCK) NPCUtils.updateDiamondHologram(holograms); // run this code if it is a Diamond Hologram
            else if (holograms[2].getItem(EquipmentSlot.HEAD).getType() == Material.EMERALD_BLOCK) NPCUtils.updateEmeraldHologram(holograms); // run this code if it is an Emerald Hologram
        }

        // Update Hologram Rotation
        BedwarsRunner.Variables.HologramRotation += 4; // add 4 to the hologram rotation
        if (BedwarsRunner.Variables.HologramRotation >= 360) BedwarsRunner.Variables.HologramRotation -= 360; // subtract by 360 if hologram rotation is greater then 360
    }

    public static void playerDied(Player player) {
        int playerTeam = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId()).team; // get the players team
        if (!TeamUtils.isTeamAlive(playerTeam)) { // check if there team is alive
            TeamUtils.TeamEliminated(BedwarsRunner.Variables.Teams.get(playerTeam), playerTeam); // eliminate the team
        } else if (!BedwarsRunner.Variables.canTeamRespawn(playerTeam)) { // check if the player can respawn
            AsyncBedwarsGameTicker.actions.add(() -> {
                ScoreboardUtils.UpdateTeamsboard(); // update Teams on the scoreboard
            });
        }
    }

    public static void TeamBedDestroyed(int TeamID, int BedID) {
        BedData bed = BedwarsRunner.Variables.getTeamBeds(TeamID).get(BedID); // get the bed data

        BedwarsRunner.Variables.teamBedDestroyed(TeamID, BedID); // run teamBedDestory
        List<Player> players = BedwarsRunner.world.getPlayers(); // get all players
        boolean canRespawn = BedwarsRunner.Variables.canTeamRespawn(TeamID); // check if the team can respawn

        for (int i = 0; i < players.size(); i++) {
            int team = BedwarsRunner.Variables.PlayerStats.get(players.get(i).getUniqueId()).team; // get the players team
            if (TeamID == team) { // check if the players team matches the beds team
                String subtitle = (canRespawn ? "bed " + BedID + " has been destroyed!" : "You will no longer respawn!"); // create subtitle
                PlayerUtils.sendTitle(players.get(i), "Bed Destroyed", subtitle, TextColor.color(255, 0, 0), TextColor.color(255, 255, 255)); // send Title to player
                players.get(i).playSound(players.get(i).getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 1, 1); // play sound to player
            }
        }

        AsyncBedwarsGameTicker.actions.add(() -> {
            ScoreboardUtils.UpdateTeamsboard(); // udate teams on the scoreboard
        });
        APIEventCaller.bedDestroyed(TeamID, BedID, new Location(BedwarsRunner.world, bed.pos.getBlockX(), bed.pos.getBlockY(), bed.pos.getBlockZ())); // API Call
    }

    // Generator Checks
    public static boolean[] canSpawnTeamGenItem(Location location) {
        int ironCount = 0;
        int goldCount = 0;
        int emeraldCount = 0;
        Object[] objs = location.getNearbyEntitiesByType(Item.class, 3).stream().toArray();
        for (int i = 0; i < objs.length; i++) {
            Item item = (Item)objs[i];
            if (item.getItemStack().getType() == Material.IRON_INGOT) {
                ironCount += item.getItemStack().getAmount();
            } else if (item.getItemStack().getType() == Material.GOLD_INGOT) {
                goldCount += item.getItemStack().getAmount();
            } else if (item.getItemStack().getType() == Material.EMERALD) {
                emeraldCount += item.getItemStack().getAmount();
            }

            if (ironCount >= BedwarsConf.TeamGeneratorMaxIronCount && goldCount >= BedwarsConf.TeamGeneratorMaxGoldCount && emeraldCount >= BedwarsConf.TeamGeneratorMaxEmeraldCount) 
                return new boolean[] {false, false, false};
        }
        return new boolean[] {
            ironCount < BedwarsConf.TeamGeneratorMaxIronCount,
            goldCount < BedwarsConf.TeamGeneratorMaxGoldCount,
            emeraldCount < BedwarsConf.TeamGeneratorMaxEmeraldCount
        };
    }

    public static boolean canSpawnEmeraldGenItem(Location location) {
        int emeraldCount = 0;
        Object[] objs = location.getNearbyEntitiesByType(Item.class, 3).stream().toArray();
        for (int i = 0; i < objs.length; i++) {
            Item item = (Item)objs[i];
            if (item.getItemStack().getType() == Material.EMERALD) {
                emeraldCount += item.getItemStack().getAmount();
            }

            if (emeraldCount >= BedwarsConf.GeneratorMaxEmeraldCount) 
                return false;
        }
        return true;
    }

    public static boolean canSpawnDiamondGenItem(Location location) {
        int diamondCount = 0;
        Object[] objs = location.getNearbyEntitiesByType(Item.class, 3).stream().toArray();
        for (int i = 0; i < objs.length; i++) {
            Item item = (Item)objs[i];
            if (item.getItemStack().getType() == Material.DIAMOND) {
                diamondCount += item.getItemStack().getAmount();
            }

            if (diamondCount >= BedwarsConf.GeneratorMaxDiamondCount) 
                return false;
        }
        return true;
    }

}
