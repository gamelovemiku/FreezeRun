package com.gamelovemiku.freezerun.player;

import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager implements Listener {

    public static HashMap<UUID, PlayerState> playerState = new HashMap<UUID, PlayerState>();

    public static HashMap<UUID, PlayerState> getPlayerState() {
        return playerState;
    }

    public void setPlayerState(HashMap<UUID, PlayerState> playerState) {
        this.playerState = playerState;
    }
}
