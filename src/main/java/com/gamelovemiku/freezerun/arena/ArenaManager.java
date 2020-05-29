package com.gamelovemiku.freezerun.arena;

import com.gamelovemiku.freezerun.FreezeRun;
import com.gamelovemiku.freezerun.FreezeRunHelper;
import com.gamelovemiku.freezerun.events.GameEndEvent;
import com.gamelovemiku.freezerun.events.GameStartEvent;
import com.gamelovemiku.freezerun.events.PlayerJoinArenaEvent;
import com.gamelovemiku.freezerun.events.PlayerLeaveArenaEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ArenaManager implements Listener {

    private static HashMap<String, Arena> arenaList = new HashMap<String, Arena>();

    public static HashMap<String, Arena> getArenaList() {
        return arenaList;
    }

    public String create(String id) {
        if(arenaList.containsKey(id) == false) {
            Arena arena = new Arena();
            arenaList.put(id, arena);
            arena.setId(id);
            return id;
        }
        return "ERROR";
    }

    public void startOver(String arenaId) {
        if(arenaList.get(arenaId).getState().equals(ArenaState.WAITING)) {

            Arena arena = arenaList.get(arenaId);
            arena.setGametime(180);
            arena.setLobbytime(45);

            FreezeRunHelper helper = new FreezeRunHelper();

            arena.setState(ArenaState.WAITING);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(arena.getLobbytime() > 0) {
                        arena.getDummyplayers().forEach(uuid -> {
                            Player player = Bukkit.getPlayer(uuid);
                            player.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &7The game will starting in &e" + arena.getLobbytime() + " seconds. &a(" + arena.getDummyplayers().size() + "/Unlimited)"));
                            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1,1);
                        });
                    } else {
                        arena.setState(ArenaState.FINISHING);

                        arena.getDummyplayers().forEach(uuid -> {
                            Player player = Bukkit.getPlayer(uuid);
                            player.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &aTeleporting you to game arena!"));
                            player.sendTitle(helper.formatInGameColor("&b&lFREEZE RUN"), helper.formatInGameColor("&eFreezer is coming.."), 20, 45, 20);
                            spawnToGame(player.getName());
                        });

                        this.cancel();
                        startGame(arenaId);
                        arena.setLobbytime(45);;
                    }
                    arena.setLobbytime(arena.getLobbytime()-1);
                }
            }.runTaskTimer(FreezeRun.getInstance(), 0, 20);
        }
        return;
    }

    public void startGame(String arenaId) {
        Arena arena = arenaList.get(arenaId);
        FreezeRunHelper helper = new FreezeRunHelper();

        Bukkit.getPluginManager().callEvent(new GameStartEvent(arena));

        arena.setState(ArenaState.PLAYING);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(arena.getGametime() > 0) {
                    arena.getDummyplayers().forEach(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        helper.sendActionBar(player, helper.formatInGameColor("&7The game will ending in &e" + arena.getGametime() + " seconds."));
                    });
                } else {
                    arena.setState(ArenaState.FINISHING);

                    arena.getDummyplayers().forEach(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        player.sendMessage(helper.formatInGameColor("&r"));
                        player.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &c&lGAME OVER!"));
                        player.sendMessage(helper.formatInGameColor("&r"));

                        player.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &aTeleporting you back to lobby!"));

                        Bukkit.getScheduler().scheduleSyncDelayedTask(FreezeRun.getInstance(), () -> {
                            player.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &fThank you for help us testing!"));
                        }, helper.secondToTick(3));
                        spawnToLobby(player.getName());
                    });

                    this.cancel();
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(arena));
                    arena.setState(ArenaState.WAITING);
                    arena.setGametime(120);
                }
                arena.setGametime(arena.getGametime()-1);
            }
        }.runTaskTimer(FreezeRun.getInstance(), 0, 20);
    }

    public void join(String player, String arenaId) {
        if(arenaList.get(arenaId).getState().equals(ArenaState.WAITING)) {
            addDummyPlayer(player, arenaId);
            spawnToLobby(player);
            Bukkit.getPluginManager().callEvent(new PlayerJoinArenaEvent(Bukkit.getPlayer(player), arenaList.get(arenaId)));
        } else {
            Bukkit.getPlayer(player).sendMessage(ChatColor.RED + "This lobby is already started.");
        }
        return;
    }

    public void leave(String player, String arenaId) {
        if(arenaList.get(arenaId).getDummyplayers().contains(player)) {
            if(arenaList.get(arenaId).getDummyplayers().remove(player)) {
                removeDummyPlayer(player, arenaId);
                Bukkit.getPluginManager().callEvent(new PlayerLeaveArenaEvent(Bukkit.getPlayer(player), arenaList.get(arenaId)));
                return;
            }
        } else {
            //Bukkit.broadcastMessage("Not found " + player + " in " + arenaId + " arena!");
        }
        return;
    }

    public void delete(String arenaId) {
        arenaList.remove(arenaId);
        Bukkit.broadcastMessage("Removed Arena by id: " + arenaId);
        return;
    }

    public void addDummyPlayer(String name, String arenaId) {
        if(!arenaList.get(arenaId).getDummyplayers().contains(name)) {
            arenaList.get(arenaId).getDummyplayers().add(name);
            Bukkit.broadcastMessage("Added player: " + name + " to " + arenaId + "!");
        } else {
            Bukkit.getPlayer(name).sendMessage(new FreezeRunHelper().formatInGameColor("&b&lFreezeRun> &cYou already joined to this arena! &8--- &a" + arenaId + "!"));
        }

        return;
    }

    public void removeDummyPlayer(String name, String arenaId) {
        if(!arenaList.get(arenaId).getDummyplayers().contains(name)) {
            arenaList.get(arenaId).getDummyplayers().remove(name);
            Bukkit.broadcastMessage("Removed player: " + name + " from " + arenaId + "!");
        } else {
            //Bukkit.broadcastMessage(ChatColor.RED + "You not in this arena #" + arenaId + "!");
            Bukkit.getPlayer(name).sendMessage(new FreezeRunHelper().formatInGameColor("&b&lFreezeRun> &cYou not in this arena! &8--- &a" + arenaId + "!"));
        }
        return;
    }

    public void listDummyPlayer(String arenaId) {
        arenaList.get(arenaId).getDummyplayers();
        Bukkit.broadcastMessage(ChatColor.AQUA + "================================================");
        Bukkit.broadcastMessage(ChatColor.AQUA + "Arena Info |||||||||||||" + arenaId + " ---> TOTAL=" + arenaList.get(arenaId).getDummyplayers().size());
        Bukkit.broadcastMessage(ChatColor.RED + "Arena Id: " + arenaList.get(arenaId).getId());
        Bukkit.broadcastMessage(ChatColor.AQUA + "Arena Name: " + arenaList.get(arenaId).getName());
        Bukkit.broadcastMessage(ChatColor.AQUA + "Arena status: " + arenaList.get(arenaId).getState().toString());
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Arena lobbytime: " + arenaList.get(arenaId).getLobbytime());
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Arena gametime: " + arenaList.get(arenaId).getGametime());
        Bukkit.broadcastMessage(ChatColor.GRAY + "Arena Lobby Location: " + arenaList.get(arenaId).getLobby().toString());
        Bukkit.broadcastMessage(ChatColor.GRAY + "Arena Spawn Location: " + arenaList.get(arenaId).getSpawn().toString());
        for (String player : arenaList.get(arenaId).getDummyplayers()) {
            Bukkit.broadcastMessage("------ "+ ChatColor.GREEN + player);
        }
        return;
    }

    public void setId(String arenaId, String id) {
        arenaList.get(arenaId).setId(id);
    }

    public void setName(String arenaId, String name) {
        arenaList.get(arenaId).setName(name);
    }

    public void setState(String arenaId, ArenaState state) {
        arenaList.get(arenaId).setState(state);
    }

    public void setGameLobbyLocation(Location location, String arenaId) {
        arenaList.get(arenaId).setLobby(location);
        return;
    }

    public void setGameSpawn(Location location, String arenaId) {
        arenaList.get(arenaId).setSpawn(location);
        return;
    }

    public void spawnToLobby(String name) {
        //Bukkit.broadcastMessage("Scanning arena for this player...");
        arenaList.forEach((key, value) -> {
            if(value.getDummyplayers().contains(name)) {
                Bukkit.getPlayer(name).teleport(value.getLobby());
                return;
            } else {
                //Bukkit.broadcastMessage("Not found this player in: " + key + "   |||||||  " + value.getDummyplayers().contains(name));
            }
            return;
        });
    }

    public void spawnToGame(String name) {
        //Bukkit.broadcastMessage("Scanning arena for this player...");
        arenaList.forEach((key, value) -> {
            //Bukkit.broadcastMessage("Key: " + key);
            if(value.getDummyplayers().contains(name)) {
                //Bukkit.broadcastMessage("You are in game spawn!");
                Bukkit.getPlayer(name).teleport(value.getSpawn());
                return;
            }
            return;
        });
    }

    public Location getGameSpawnLocation(String arenaId) {
        return arenaList.get(arenaId).getSpawn();
    }

    public void sendChatToAllPlayerInArena(Arena arena, String msg) {
        arena.getDummyplayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.sendMessage(msg);
        });
        return;
    }

    public void sendTitleToAllPlayerInArena(Arena arena, String title, String subtitle) {
        arena.getDummyplayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.sendTitle(title, subtitle, 15, 30, 15);
        });
        return;
    }

    public Integer totalArena() {
        return arenaList.size();
    }

}
