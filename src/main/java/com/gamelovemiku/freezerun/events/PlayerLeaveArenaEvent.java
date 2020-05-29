package com.gamelovemiku.freezerun.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveArenaEvent extends Event implements Cancellable {

    private Player player;
    private boolean isCancelled;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public PlayerLeaveArenaEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
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
