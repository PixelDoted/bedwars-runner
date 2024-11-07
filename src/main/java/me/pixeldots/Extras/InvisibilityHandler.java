package me.pixeldots.Extras;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import me.pixeldots.BedwarsRunner;
import me.pixeldots.Game.data.PlayerStatistics;

public class InvisibilityHandler {

    public ProtocolManager manager;

    public void Register() {
        manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(BedwarsRunner.instance, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketSending(PacketEvent e) {
                PacketContainer packet = e.getPacket();
                Player player = null;

                int EntityID = packet.getIntegers().getValues().get(0);
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (p.getEntityId() == EntityID) {
                        player = p;
                        break;
                    }
                }
                if (player == null) return;
                boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);

                // Team Check
                PlayerStatistics statsOwner = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId());
                PlayerStatistics playerReciver = BedwarsRunner.Variables.PlayerStats.get(e.getPlayer().getUniqueId());
                if (statsOwner != null && playerReciver != null && statsOwner.team == playerReciver.team) isInvisible = false;

                if (isInvisible) {
                    PlayerInventory inv = player.getInventory();
                    packet.getSlotStackPairLists().write(0, getEquipmentList(inv, false));
                }
            }
        });
    }

    public void sendInvisiblePacket(Player player) {
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            // Team Check
            PlayerStatistics statsOwner = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId());
            PlayerStatistics playerReciver = BedwarsRunner.Variables.PlayerStats.get(player.getUniqueId());
            if (statsOwner != null && playerReciver != null && statsOwner.team == playerReciver.team) continue;

            PlayerInventory inv = player.getInventory();

            packet.getIntegers().write(0, player.getEntityId());
            packet.getSlotStackPairLists().write(0, getEquipmentList(inv, false));

            try {
                manager.sendServerPacket(players.get(i), packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendVisiblePacket(Player player) {
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            PlayerInventory inv = player.getInventory();

            packet.getIntegers().write(0, player.getEntityId());
            packet.getSlotStackPairLists().write(0, getEquipmentList(inv, true));

            try {
                manager.sendServerPacket(players.get(i), packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Pair<EnumWrappers.ItemSlot, ItemStack>> getEquipmentList(PlayerInventory inv, boolean isVisible) {
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, (isVisible ? inv.getHelmet() : null)));
        list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, (isVisible ? inv.getChestplate() : null)));
        list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, (isVisible ? inv.getLeggings() : null)));
        list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, (isVisible ? inv.getBoots() : null)));
        list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, inv.getItemInMainHand()));
        list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, inv.getItemInOffHand()));
        return list;
    }
    
    public void updatePlayerNameTag(Player player, String name) {
        return; // TODO: Implement NameTag changing
        /*PacketContainer packet = manager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        packet.getIntegers().write(0, 0);
        packet.getStrings().write(0, uuid);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(player.toString()));
        packet.getSpecificModifier(Collection.class).write(0, Collections.singletonList(name));
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                manager.sendServerPacket(p, packet);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot send packet " + packet, e);
            }
        }*/
    }

}
