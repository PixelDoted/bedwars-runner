package me.pixeldots.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.pixeldots.Shops.ShopUtils.CurrencyType;

public class PurchaseUpgradeEvent extends Event implements Cancellable {
    
    private Player player;
    private String upgrade;
    private int costAmount;
    private CurrencyType currency;

    private boolean cancelled = false;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public PurchaseUpgradeEvent(Player _player, String _upgrade, int _costAmount, CurrencyType _currency) {
        this.player = _player;
        this.upgrade = _upgrade;
        this.costAmount = _costAmount;
        this.currency = _currency;
    }

    public Player getPlayer() {
        return this.player;
    }
    public String getUpgrade() {
        return this.upgrade;
    }
    public void setUpgrade(String _upgrade) {
        this.upgrade = _upgrade;
    }
    public int getCostAmount() {
        return this.costAmount;
    }
    public CurrencyType getCurrency() {
        return this.currency;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean _cancelled) {
        this.cancelled = _cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}