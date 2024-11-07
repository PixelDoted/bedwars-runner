package me.pixeldots.API.Events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BedDestroyedEvent extends Event implements Cancellable {

    private int teamID;
    private int bedID;
    private Location location;

    private boolean cancelled = false;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public BedDestroyedEvent(int _teamID, int _bedID, Location _location) {
        this.teamID = _teamID;
        this.bedID = _bedID;
        this.location = _location;
    }

    public int getTeamID() {
        return teamID;
    }
    public int getBedID() {
        return bedID;
    }
    public Location getLocation() {
        return location;
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
