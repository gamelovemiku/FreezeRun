package com.gamelovemiku.freezerun.listeners;

import com.gamelovemiku.freezerun.arena.ArenaLoader;
import com.gamelovemiku.freezerun.arena.ArenaManager;
import com.gamelovemiku.freezerun.events.PlayerJoinArenaEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GeneralListener implements Listener {

    public GeneralListener() {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(ArenaManager.getArenaList().size() == 0) {
            new ArenaLoader().loadArenas();
        }
    }

}
