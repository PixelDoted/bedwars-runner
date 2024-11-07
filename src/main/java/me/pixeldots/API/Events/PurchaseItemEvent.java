package me.pixeldots.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.pixeldots.Shops.ShopUtils.CurrencyType;

public class PurchaseItemEvent extends Event implements Cancellable {
    
    private Player player;
    private ItemStack item;
    private int costAmount;
    private CurrencyType currency;
    private boolean fromQuickBuy;

    private boolean cancelled = false;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public PurchaseItemEvent(Player _player, ItemStack _item, int _costAmount, CurrencyType _currency, boolean _fromQuickBuy) {
        this.player = _player;
        this.item = _item;
        this.costAmount = _costAmount;
        this.currency = _currency;
        this.fromQuickBuy = _fromQuickBuy;
    }

    public Player getPlayer() {
        return this.player;
    }
    public ItemStack getItem() {
        return this.item;
    }
    public void setItem(ItemStack _item) {
        this.item = _item;
    }
    public int getCostAmount() {
        return this.costAmount;
    }
    public CurrencyType getCurrency() {
        return this.currency;
    }
    public boolean fromQuickBuy() {
        return this.fromQuickBuy;
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
