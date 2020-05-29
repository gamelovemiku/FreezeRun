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

    public void loadArenas() {
        FileConfiguration file = FreezeRun.getInstance().getConfig();
        ConfigurationSection sec = file.getConfigurationSection("arenas");
        int count = 0;

        for(String key : sec.getKeys(false)){
            String name = file.getString("arenas." + key + ".name");

            Arena arena = new Arena();
            arena.setName(file.getString("arenas." + key +".name"));
            arena.setLobby(new Location(Bukkit.getWorld("world"), file.getInt("arenas." + key +".locations.lobby.x"), file.getInt("arenas." + key +".locations.lobby.y"), file.getInt("arenas." + key + ".locations.lobby.z")));
            arena.setSpawn(new Location(Bukkit.getWorld("world"), file.getInt("arenas." + key +".locations.spawn.x"), file.getInt("arenas." + key +".locations.spawn.y"), file.getInt("arenas." + key + ".locations.spawn.z")));

            Bukkit.broadcastMessage("###########-- READ " + file.getString("arenas." + key +".name"));
            ArenaManager.getArenaList().put(file.getString("arenas." + key +".name"), arena);

            count++;
        }
        Bukkit.getServer().getLogger().info("[FreezeRun] Successfully loaded " + count + " arenas!");
    }

    public void saveArenas() {
        instance.saveConfig();
    }
}
