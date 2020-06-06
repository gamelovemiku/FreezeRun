package com.gamelovemiku.freezerun.player;

import com.gamelovemiku.freezerun.FreezeRunHelper;
import com.gamelovemiku.freezerun.arena.Arena;
import com.gamelovemiku.freezerun.arena.ArenaManager;
import com.gamelovemiku.freezerun.arena.ArenaState;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    public static HashMap<UUID, PlayerState> playerState = new HashMap<UUID, PlayerState>();

    public static HashMap<UUID, PlayerState> getPlayerState() {
        return playerState;
    }

    private Arena arena = null;
    private Arena choose = null;

    public void setPlayerState(HashMap<UUID, PlayerState> playerState) {
        this.playerState = playerState;
    }

    public Arena findArena(Player player) {
        ArenaManager.getArenaList().forEach((s, arena) -> {
            if(arena.getPlayers().contains(player)) {
                this.arena = arena;
                return;
            }
        });
        return arena;
    }

    public void autoJoin(Player player) {
        ArenaManager.getArenaList().forEach((s, arena) -> {
            if(choose == null) choose = arena;

            if(arena.getState().equals(ArenaState.WAITING)) {
                if(choose.getPlayers().size() <= arena.getPlayers().size()) {
                    choose = arena;
                }
            }
        });

        new ArenaManager().join(player, choose.getId());
        player.sendMessage(new FreezeRunHelper().formatInGameColor("&7Sending you to &a" + choose.getName()));
        return;
    }

}
