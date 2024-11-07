package me.pixeldots.API.Events;

import java.util.List;
import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamEliminatedEvent extends Event implements Cancellable {

    private int teamID;
    private List<UUID> players;

    private boolean cancelled = false;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public TeamEliminatedEvent(int _teamID, List<UUID> _players) {
        this.teamID = _teamID;
        this.players = _players;
    }

    public int getTeamID() {
        return teamID;
    }
    public List<UUID> getPlayers() {
        return players;
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
