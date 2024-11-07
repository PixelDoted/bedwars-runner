package me.pixeldots.Game.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.pixeldots.Shops.ShopUtils;

public class TeamUpgrades {

    public int ProtectionLevel = 0;
    public int SharpnessLevel = 0;
    public int ForgeLevel = 0;
    public int HasteLevel = 0;
    public int DragonBuffs = 0;
    public int healPoolLevel = 0;

    public TeamTraps[] Traps = new TeamTraps[] {TeamTraps.None, TeamTraps.None, TeamTraps.None};
    public long lastTrapTriggered = 0;

    public enum TeamTraps {None, Alert, MiningFatigue, Blindness};

    public void addTrap(TeamTraps trap) {
        if (Traps[0] == TeamTraps.None) {
            Traps[0] = trap;
        } else if (Traps[1] == TeamTraps.None) {
            Traps[1] = trap;
        } else if (Traps[2] == TeamTraps.None) {
            Traps[2] = trap;
        }
    }

    public boolean canAddTrap() {
        return Traps[0] == TeamTraps.None || Traps[1] == TeamTraps.None || Traps[2] == TeamTraps.None;
    }

    public ItemStack getTrapItem(int id) {
        switch (Traps[id]) {
            case Alert:
                return ShopUtils.setItemName(new ItemStack(Material.FEATHER), "Alert Trap");
            case MiningFatigue:
                return ShopUtils.setItemName(new ItemStack(Material.GOLDEN_PICKAXE), "Mining Fatigue Trap");
            case Blindness:
                return ShopUtils.setItemName(new ItemStack(Material.ENDER_EYE), "Blindness Trap");
            default:
                return ShopUtils.setItemName(new ItemStack(Material.COAL_BLOCK), "No Trap");
        }
    }
    
}
