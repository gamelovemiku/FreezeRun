package com.gamelovemiku.freezerun.events;

import com.gamelovemiku.freezerun.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameFinishedEvent extends Event implements Cancellable {

    private Arena arena;
    private boolean isCancelled;
    private Player winner = null;
    private String msg = null;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public GameFinishedEvent(Arena arena) {
        this.arena = arena;
    }

    public GameFinishedEvent(Arena arena, Player winner) {
        this.arena = arena;
        this.winner = winner;
    }

    public GameFinishedEvent(Arena arena, Player winner, String msg) {
        this.arena = arena;
        this.winner = winner;
        this.msg = msg;
    }

    public Arena getArena() {
        return arena;
    }

    public Player getWinner() {
        return winner;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
