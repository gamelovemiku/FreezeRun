package com.gamelovemiku.freezerun.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Arena {

    private String id;
    private String name;

    private ArenaState state = ArenaState.WAITING;

    private Location spawn;
    private Location lobby;

    private int freezecount = 0;
    private int maxplayer = 4;

    private int lobbytime = 60;
    private int gametime = 180;
    private int preparetime = 15;

    private boolean isStartLobbyCountDown = false;

    private List<Player> players = new ArrayList<Player>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public ArenaState getState() {
        return state;
    }

    public void setState(ArenaState state) {
        this.state = state;
        //Bukkit.broadcastMessage("Set state of " + getName() + " to " + getState().toString());
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public int getMaxplayer() {
        return maxplayer;
    }

    public void setMaxplayer(int maxplayer) {
        this.maxplayer = maxplayer;
    }

    public int getPreparetime() {
        return preparetime;
    }

    public void setPreparetime(int preparetime) {
        this.preparetime = preparetime;
    }

    public int getGametime() {
        return gametime;
    }

    public void setGametime(int gametime) {
        this.gametime = gametime;
    }

    public int getLobbytime() {
        return lobbytime;
    }

    public void setLobbytime(int lobbytime) {
        this.lobbytime = lobbytime;
    }

    public int getFreezecount() {
        return freezecount;
    }

    public void setFreezecount(int freezecount) {
        this.freezecount = freezecount;
    }

    public void reset(int lobbytime, int gametime) {
        this.lobbytime = lobbytime;
        this.gametime = gametime;
    }
}