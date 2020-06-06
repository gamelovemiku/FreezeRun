package com.gamelovemiku.freezerun.arena;

import com.gamelovemiku.freezerun.FreezeRun;
import com.gamelovemiku.freezerun.FreezeRunHelper;
import com.gamelovemiku.freezerun.events.*;
import com.gamelovemiku.freezerun.player.PlayerManager;
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

    FreezeRunHelper helper = new FreezeRunHelper();

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

            FreezeRunHelper helper = new FreezeRunHelper();

            arena.setState(ArenaState.WAITING);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(arena.getLobbytime() >= 0) {

                        arena.getPlayers().forEach(player -> {
                            helper.sendActionBar(player, helper.formatInGameColor("&9กำลังเริ่มเกมในอีก &e" + arena.getLobbytime() + " วินาที"));
                        });

                        arena.getPlayers().forEach(player -> {
                            switch (arena.getLobbytime()) {
                                case 110:
                                case 90:
                                case 60:
                                case 30:
                                case 10:
                                case 5:
                                case 4:
                                case 3:
                                case 2:
                                case 1:
                                case 0:
                                    if (arena.getPlayers().size() < 2) {
                                        sendChatToAllPlayerInArena(arena, helper.formatInGameColor("&cกำลังรอผู้เล่นอื่นเข้ามาเพิ่มเติม หากถึงขั้นต่ำแล้วจะนับถอยหลังเข้าสู่เกกมโดยอัตโนมัติ"));
                                        arena.setLobbytime(-1);
                                        this.cancel();
                                    } else {
                                        player.sendMessage(helper.formatInGameColor("&7จะเริ่มเกมในอีก &e" + arena.getLobbytime() + " วินาที"));
                                        player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1,1);
                                        break;
                                    }
                            }
                        });
                    } else {
                        arena.getPlayers().forEach(player -> {;
                            player.sendMessage(helper.formatInGameColor("&aTeleporting you to game arena!"));
                            player.sendTitle(helper.formatInGameColor("&b&lFREEZE RUN"), helper.formatInGameColor("&6&lTHE HOST &7is coming.."), 20, 45, 20);
                            spawnToGame(player);
                        });
                        this.cancel();
                        startGame(arenaId);
                        return;
                    }
                    arena.setLobbytime(arena.getLobbytime()-1);
                }
            }.runTaskTimer(FreezeRun.getInstance(), 0, 20);
        }
        return;
    }

    public void startGame(String arenaId) {
        Arena arena = arenaList.get(arenaId);

        arena.setState(ArenaState.PREPARING);
        Bukkit.getPluginManager().callEvent(new GamePreparingEvent(arena));

        Bukkit.getScheduler().scheduleSyncDelayedTask(FreezeRun.getInstance(), () -> {
            arena.setState(ArenaState.PLAYING);
            Bukkit.getPluginManager().callEvent(new GameStartEvent(arena));
        }, helper.secondToTick(arena.getPreparetime()));
    }

    public void join(Player player, String arenaId) {
        Arena arena = arenaList.get(arenaId);
        if(!arena.getPlayers().contains(player)) {
            if(arena.getState().equals(ArenaState.WAITING)) {
                if(arena.getPlayers().size() <= arena.getMaxplayer()) {
                    addPlayer(player, arenaId);
                    spawnToLobby(player);
                    Bukkit.getPluginManager().callEvent(new PlayerJoinArenaEvent(player, arenaList.get(arenaId)));
                } else {
                    player.sendMessage(helper.formatInGameColor("&cThis arena is full!"));
                }
            } else {
                player.sendMessage(ChatColor.RED + "This lobby is already started.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You already joined this lobby.");
        }

        return;
    }

    public void leave(Player player) {
        Arena arena = new PlayerManager().findArena(player);
        if(arenaList.get(arena.getId()).getPlayers().contains(player)) {
            if(arenaList.get(arena.getId()).getPlayers().remove(player)) {
                removePlayer(player, arena.getId());
                Bukkit.getPluginManager().callEvent(new PlayerLeaveArenaEvent(player, arenaList.get(arena.getId())));
                return;
            }
        } else {
            //Bukkit.broadcastMessage("Not found " + player + " in " + arenaId + " arena!");
        }
        return;
    }

    public void forceStop(Arena arena, String reason) {
        if(arena.getState().equals(ArenaState.PLAYING)) {
            arena.getPlayers().forEach(player -> {
                player.sendMessage(new FreezeRunHelper().formatInGameColor("&c&lTHE GAME IS INSTANTLY STOP!"));
                player.sendMessage(new FreezeRunHelper().formatInGameColor("&6Reason: &e" + reason));
            });
            arena.setGametime(0);
        }
        return;
    }

    public void delete(String arenaId) {
        arenaList.remove(arenaId);
        Bukkit.broadcastMessage("Removed Arena by id: " + arenaId);
        return;
    }

    public void addPlayer(Player player, String arenaId) {
        if(!arenaList.get(arenaId).getPlayers().contains(player)) {
            arenaList.get(arenaId).getPlayers().add(player);
        } else {
            player.sendMessage(new FreezeRunHelper().formatInGameColor("&8[&bFreezeRun&8] &cYou already joined to this arena! &8--- &a" + arenaId + "!"));
        }

        return;
    }

    public void removePlayer(Player player, String arenaId) {
        if(!arenaList.get(arenaId).getPlayers().contains(player)) {
            arenaList.get(arenaId).getPlayers().remove(player);
        } else {
            player.sendMessage(new FreezeRunHelper().formatInGameColor("&8[&bFreezeRun&8] &cYou not in this arena! &8--- &a" + arenaId + "!"));
        }
        return;
    }

    public void listDummyPlayer(String arenaId) {
        arenaList.get(arenaId).getPlayers();
        Bukkit.broadcastMessage(ChatColor.AQUA + "================================================");
        Bukkit.broadcastMessage(ChatColor.AQUA + "Arena Info |||||||||||||" + arenaId + " ---> TOTAL=" + arenaList.get(arenaId).getPlayers().size());
        Bukkit.broadcastMessage(ChatColor.RED + "Arena Id: " + arenaList.get(arenaId).getId());
        Bukkit.broadcastMessage(ChatColor.AQUA + "Arena Name: " + arenaList.get(arenaId).getName());
        Bukkit.broadcastMessage(ChatColor.AQUA + "Arena status: " + arenaList.get(arenaId).getState().toString());
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Arena lobbytime: " + arenaList.get(arenaId).getLobbytime());
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Arena gametime: " + arenaList.get(arenaId).getGametime());
        Bukkit.broadcastMessage(ChatColor.GRAY + "Arena Lobby Location: " + arenaList.get(arenaId).getLobby().toString());
        Bukkit.broadcastMessage(ChatColor.GRAY + "Arena Spawn Location: " + arenaList.get(arenaId).getSpawn().toString());
        for (Player player : arenaList.get(arenaId).getPlayers()) {
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

    public void spawnToLobby(Player player) {
        arenaList.forEach((key, value) -> {
            if(value.getPlayers().contains(player)) {
                player.teleport(value.getLobby());
                return;
            }
            return;
        });
    }

    public void spawnToGame(Player player) {
        arenaList.forEach((key, value) -> {
            if(value.getPlayers().contains(player)) {
                player.teleport(value.getSpawn());
                return;
            }
            return;
        });
    }

    public Location getGameSpawnLocation(String arenaId) {
        return arenaList.get(arenaId).getSpawn();
    }

    public void sendChatToAllPlayerInArena(Arena arena, String msg) {
        arena.getPlayers().forEach(player -> {
            player.sendMessage(msg);
        });
        return;
    }

    public void sendTitleToAllPlayerInArena(Arena arena, String title, String subtitle) {
        arena.getPlayers().forEach(player -> {
            player.sendTitle(title, subtitle, 15, 30, 15);
        });
        return;
    }

    public void sendTimeOutTitle(Arena arena) {
        arena.getPlayers().forEach(player -> {
            if(arena.getGametime() < 10) {
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 4,1);

                if((arena.getGametime() % 2) == 0) {
                    player.sendTitle(helper.formatInGameColor("&c&l" + arena.getGametime()), helper.formatInGameColor("&fGame Time"), 0, 30, 15);
                } else {
                    player.sendTitle(helper.formatInGameColor("&e&l" + arena.getGametime()), helper.formatInGameColor("&fGame Time"), 0, 30, 15);
                }
            }
        });
        return;
    }

    public Integer totalArena() {
        return arenaList.size();
    }

}
