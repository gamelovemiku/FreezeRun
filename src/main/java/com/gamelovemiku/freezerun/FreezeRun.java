package com.gamelovemiku.freezerun;

import com.gamelovemiku.freezerun.arena.ArenaLoader;
import com.gamelovemiku.freezerun.command.GameCMD;
import com.gamelovemiku.freezerun.listeners.FreezeRunListener;
import com.gamelovemiku.freezerun.listeners.TestListener;
import com.gamelovemiku.freezerun.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class FreezeRun extends JavaPlugin {

    public static FreezeRun freezeRun;
    public static FreezeRun getInstance() {
        return freezeRun;
    }

    @Override
    public void onEnable() {

        freezeRun = this;

        FreezeRunHelper helper = new FreezeRunHelper();

        Bukkit.getServer().getLogger().info(helper.formatInGameColor("&3[FreezeRun] &7Developed by &dgamelovemiku"));
        Bukkit.getServer().getLogger().info(helper.formatInGameColor("&3[FreezeRun] &aLaunched!"));

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new FreezeRunListener(), this);
        getServer().getPluginManager().registerEvents(new TestListener(), this);

        this.getCommand("fr").setExecutor(new GameCMD());

        new ArenaLoader().loadArenas();

    }

    @Override
    public void onDisable() {
        this.getConfig().options().copyDefaults(true);
        saveConfig();
        Bukkit.getServer().getLogger().info("&3[FreezeRun] &7Disabled!");
    }

}
