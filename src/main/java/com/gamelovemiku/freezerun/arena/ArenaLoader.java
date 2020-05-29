package com.gamelovemiku.freezerun.arena;

import com.gamelovemiku.freezerun.FreezeRun;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.geom.Area;
import java.util.List;

public class ArenaLoader {

    FreezeRun instance = FreezeRun.getInstance();
    FileConfiguration file = FreezeRun.getInstance().getConfig();
    ConfigurationSection sec = file.getConfigurationSection("arenas");

    public void loadArenas() {
        int count = 0;
        for(String key : sec.getKeys(false)){
            Arena arena = new Arena();
            arena.setId(file.getString("arenas." + key + ".id"));
            arena.setName(file.getString("arenas." + key +".name"));
            arena.setLobby(new Location(Bukkit.getWorld(file.getString("arenas." + key +".locations.lobby.world")), file.getDouble("arenas." + key +".locations.lobby.x"), file.getDouble("arenas." + key +".locations.lobby.y"), file.getDouble("arenas." + key + ".locations.lobby.z")));
            arena.setSpawn(new Location(Bukkit.getWorld(file.getString("arenas." + key +".locations.spawn.world")), file.getDouble("arenas." + key +".locations.spawn.x"), file.getDouble("arenas." + key +".locations.spawn.y"), file.getDouble("arenas." + key + ".locations.spawn.z")));

            arena.setLobbytime(file.getInt("arenas." + key +".lobbytime"));
            arena.setGametime(file.getInt("arenas." + key +".gametime"));

            //Bukkit.broadcastMessage("###########-- READ " + file.getString("arenas." + key +".name"));
            ArenaManager.getArenaList().put(file.getString("arenas." + key +".id"), arena);

            count++;
        }
        Bukkit.getServer().getLogger().info("[FreezeRun] Successfully loaded " + count + " arenas!");
    }

    public void saveArenas() {
        Bukkit.broadcastMessage("Saving " + ArenaManager.getArenaList().size() + " arenas.");
        ArenaManager.getArenaList().forEach((s, arena) -> {
            if(arena instanceof Arena) {
                String arenaId = arena.getId();
                file.set("arenas." + arenaId +".id", arena.getId());
                file.set("arenas." + arenaId +".name", arena.getName());

                file.set("arenas." + arenaId + ".locations.lobby.world", arena.getLobby().getWorld().getName());
                file.set("arenas." + arenaId + ".locations.lobby.x", arena.getLobby().getX());
                file.set("arenas." + arenaId + ".locations.lobby.y", arena.getLobby().getY());
                file.set("arenas." + arenaId + ".locations.lobby.z", arena.getLobby().getZ());

                file.set("arenas." + arenaId + ".locations.spawn.world", arena.getSpawn().getWorld().getName());
                file.set("arenas." + arenaId + ".locations.spawn.x", arena.getLobby().getX());
                file.set("arenas." + arenaId + ".locations.spawn.y", arena.getLobby().getY());
                file.set("arenas." + arenaId + ".locations.spawn.z", arena.getLobby().getZ());

                file.set("arenas." + arenaId + ".lobbytime", arena.getLobbytime());
                file.set("arenas." + arenaId + ".gametime", arena.getGametime());
            }
        });
        instance.saveConfig();
    }
}
