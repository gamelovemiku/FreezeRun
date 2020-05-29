package com.gamelovemiku.freezerun.listeners;

import com.gamelovemiku.freezerun.events.PlayerJoinArenaEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestListener implements Listener {

    public TestListener() {

    }

    @EventHandler
    public void onJoinArena(PlayerJoinArenaEvent event) {
        //event.getPlayer().sendMessage("YOU JOIN THE ARENA ::::::::: MSG FROM EVENT CALLED!");
    }

}
