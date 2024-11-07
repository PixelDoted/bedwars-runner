package me.pixeldots.API.Events;

import java.util.List;
import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameEndEvent extends Event implements Cancellable {
    
    private int teamWon;
    private List<UUID> playersWon;
    private List<UUID> playersLost;

    private boolean cancelled = false;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public GameEndEvent(int _teamWon, List<UUID> _playersWon, List<UUID> _playersLost) {
        this.teamWon = _teamWon;
        this.playersWon = _playersWon;
        this.playersLost = _playersLost;
    }

    public int getTeamWon() {
        return teamWon;
    }
    public List<UUID> getPlayersWon() {
        return playersWon;
    }
    public List<UUID> getPlayersLost() {
        return playersLost;
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
