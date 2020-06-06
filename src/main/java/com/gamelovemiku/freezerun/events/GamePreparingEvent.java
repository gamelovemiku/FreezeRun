package com.gamelovemiku.freezerun.events;

import com.gamelovemiku.freezerun.arena.Arena;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GamePreparingEvent extends Event implements Cancellable {

    private Arena arena;
    private boolean isCancelled;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public GamePreparingEvent(Arena arena) {
        this.arena = arena;
    }

    public Arena getArena() {
        return arena;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
