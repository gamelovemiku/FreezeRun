package com.gamelovemiku.freezerun.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Arena {

    private int id;
    private String name;

    private ArenaState state = ArenaState.WAITING;

    private Location spawn;
    private Location lobby;

    private List<UUID> players = new ArrayList<UUID>();
    private List<String> dummyplayers = new ArrayList<String>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players;
    }

    public List<String> getDummyplayers() {
        return dummyplayers;
    }

    public void setDummyplayers(List<String> dummyplayers) {
        this.dummyplayers = dummyplayers;
    }

    public ArenaState getState() {
        return state;
    }

    public void setState(ArenaState state) {
        this.state = state;
        Bukkit.broadcastMessage("Set state of " + getName() + " to " + getState().toString());
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }
}