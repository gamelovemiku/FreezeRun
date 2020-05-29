package com.gamelovemiku.freezerun.player;

import com.gamelovemiku.freezerun.arena.Arena;
import com.gamelovemiku.freezerun.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    public static HashMap<UUID, PlayerState> playerState = new HashMap<UUID, PlayerState>();

    public static HashMap<UUID, PlayerState> getPlayerState() {
        return playerState;
    }

    private Arena arena = null;

    public void setPlayerState(HashMap<UUID, PlayerState> playerState) {
        this.playerState = playerState;
    }

    public Arena findArena(String player) {
        ArenaManager.getArenaList().forEach((s, arena) -> {
            if(arena.getDummyplayers().contains(player)) {
                this.arena = arena;
                return;
            }
        });
        return arena;
    }

}
