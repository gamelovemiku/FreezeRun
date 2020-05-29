package com.gamelovemiku.freezerun.player;

import com.gamelovemiku.freezerun.arena.Arena;
import org.bukkit.event.Listener;

import java.util.UUID;

public class PlayerState implements Listener {

    private UUID uuid;
    private boolean inGame;
    private boolean isHost;
    private boolean isFreezing;

    private Arena arena;

    public PlayerState(UUID uuid, Arena arena, boolean ingame, boolean isHost, boolean isFreezing){
        this.setUuid(uuid);
        this.setArena(arena);
        this.setInGame(ingame);
        this.setHost(isHost);
        this.setFreezing(isFreezing);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public boolean isHost() {
        return this.isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public boolean isFreezing() {
        return isFreezing;
    }

    public void setFreezing(boolean isfreezing) {
        this.isFreezing = isfreezing;
    }
}
