package com.gamelovemiku.freezerun.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.gamelovemiku.freezerun.FreezeRun;
import com.gamelovemiku.freezerun.FreezeRunHelper;
import com.gamelovemiku.freezerun.arena.Arena;
import com.gamelovemiku.freezerun.arena.ArenaManager;
import com.gamelovemiku.freezerun.arena.ArenaState;
import com.gamelovemiku.freezerun.events.*;
import com.gamelovemiku.freezerun.player.PlayerManager;
import com.gamelovemiku.freezerun.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FreezeRunListener implements Listener {

    public FreezeRunListener() {

    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if(!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        if(!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if(!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSnowballHit(EntityDamageByEntityEvent e){
        FreezeRunHelper helper = new FreezeRunHelper();
        if ((e.getDamager() instanceof Snowball)) {
            if ((e.getEntity() instanceof Player)) {
                Player attacked = (Player) e.getEntity();
                double damage = 0.01;
                e.setDamage(damage);
                attacked.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, new FreezeRunHelper().secondToTick(4), 3));
                attacked.sendTitle(helper.formatInGameColor("&f&lSLOW A WHILE"), helper.formatInGameColor("&eYou touched snowball from &c&lTHE HOST"), 15, 25, 15);
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        try {
            Arena arena = ArenaManager.getArenaList().get(new PlayerManager().findArena((Player) event.getDamager()).getId());
            if(arena.getState().equals(ArenaState.PLAYING)) {
                event.setCancelled(true);
                Player target = (Player) event.getEntity();
                PlayerState targetState = PlayerManager.getPlayerState().get(target.getUniqueId());

                Player damager = (Player) event.getDamager();
                PlayerState damagerState = PlayerManager.getPlayerState().get(damager.getUniqueId());

                FreezeRunHelper helper = new FreezeRunHelper();

                if(targetState.isFreezing()) {
                    damager.playSound(damager.getLocation(), Sound.ENTITY_CAT_PURR, 1,1);
                    if(damagerState.isHost()) {
                        damager.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &a" + target.getName() + " is already freezing!"));
                        damager.sendTitle("", helper.formatInGameColor("&b&lAlready Freezing: &a" + target.getName()), 15,35,15);
                    } else {
                        target.setWalkSpeed(0.65f);
                        damager.sendMessage(helper.formatInGameColor("&r"));
                        damager.sendMessage(helper.formatInGameColor("&a&lReleasing: &2&l" + target.getName()));
                        damager.sendTitle("", helper.formatInGameColor("&a&lReleasing: &a" + target.getName()), 15,35,15);
                        targetState.setFreezing(false);
                    }
                } else {
                    if(damagerState.isHost()) {
                        target.setWalkSpeed(0f);

                        targetState.setFreezing(true);

                        damager.playSound(damager.getLocation(), Sound.BLOCK_SNOW_BREAK, 1,1);
                        target.sendMessage(helper.formatInGameColor("&r"));
                        target.sendMessage(helper.formatInGameColor("&b&lYou are freezing by " + damager.getName().toUpperCase()));

                        damager.sendTitle("", helper.formatInGameColor("&b&lFreezing: &a" + target.getName()), 15,35,15);
                        damager.sendMessage(helper.formatInGameColor("&r"));
                        damager.sendMessage(helper.formatInGameColor("&b&lFREEZE: &3" + target.getName().toUpperCase()));
                        damager.sendMessage(helper.formatInGameColor("&6+5 Snowball"));

                        damager.getInventory().addItem(new ItemStack(Material.SNOWBALL, 5));

                        arena.getPlayers().forEach(player -> {
                            PlayerState state = PlayerManager.getPlayerState().get(player.getUniqueId());
                            if(state.isFreezing()) {
                                arena.setFreezecount(arena.getFreezecount()+1);
                            }
                        });

                        arena.getPlayers().forEach(player -> {
                            player.sendMessage(helper.formatInGameColor("&a"));
                            player.sendMessage(helper.formatInGameColor("&a" + target.getName() + " &7is freezing by &c&lTHE HOST &d"));
                            player.sendMessage(helper.formatInGameColor("&a&lTHE ESCAPEE &7are currently surviving &e" + ((arena.getPlayers().size()-1)-arena.getFreezecount()) + " players!"));
                        });

                        if(arena.getPlayers().size()-1 == arena.getFreezecount()) {
                            arena.setGametime(0);
                            arena.getPlayers().forEach(player -> {
                                PlayerState state = PlayerManager.getPlayerState().get(player.getUniqueId());
                                if(state.isHost()) {
                                    Bukkit.getPluginManager().callEvent(new GameFinishedEvent(arena, player));
                                    Bukkit.getPluginManager().callEvent(new GameEndEvent(arena));
                                }
                            });
                        }

                    } else {
                        damager.playSound(damager.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
                        damager.sendMessage(helper.formatInGameColor("&8[&bFreezeRun&8] &c&lYou not a host. RUN AWAY!"));
                    }
                }
                arena.setFreezecount(0);
            }
        }catch (Exception e) {
            //
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {}

    @EventHandler
    public void onPrepare(GamePreparingEvent event) {
        Arena arena = event.getArena();
        Random rnd = new Random();
        FreezeRunHelper helper = new FreezeRunHelper();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(arena.getPreparetime() > 0) {
                    arena.getPlayers().forEach(player -> {
                        player.sendMessage(helper.formatInGameColor("&8[&bFreezeRun&8] &eFinding a host in " + arena.getPreparetime() + " seconds."));
                    });
                } else {

                    Player host = arena.getPlayers().get(rnd.nextInt(arena.getPlayers().size()));
                    PlayerState pm = PlayerManager.getPlayerState().get(host.getUniqueId());
                    pm.setHost(true);

                    arena.getPlayers().forEach(player -> {
                        PlayerState state = PlayerManager.getPlayerState().get(player.getUniqueId());

                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, helper.secondToTick(180), 4));
                        player.setFoodLevel(20);

                        if(state.isHost()) {
                            player.sendMessage(helper.formatInGameColor("&8[&bFreezeRun&8] &c&lYou are THE HOST! &7Freeze everybody If they can move."));
                            player.sendTitle(helper.formatInGameColor("&c&lTHE HOST"), helper.formatInGameColor("&eCatch other player and freeze them!"), 15, 35, 15);

                            PlayerInventory inv = player.getInventory();
                            for (int i = 0; i < 3; i++) {
                                inv.setItem(i, new ItemStack(Material.SNOWBALL, 16));
                            }

                            player.setWalkSpeed(0.6f);
                        } else {
                            player.sendMessage(helper.formatInGameColor("&8[&bFreezeRun&8] &aYou need to escaping from the host."));
                            player.sendTitle(helper.formatInGameColor("&a&lTHE ESCAPE"), helper.formatInGameColor("&fRun away from host and survive!"), 15, 35, 15);
                            player.setWalkSpeed(0.65f);
                        }

                        player.sendMessage(helper.formatInGameColor(""));
                    });

                    this.cancel();
                    arena.setPreparetime(10);
                }
                arena.setPreparetime(arena.getPreparetime()-1);
            }
        }.runTaskTimer(FreezeRun.getInstance(), 0, 20);
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        Arena arena = event.getArena();
        FreezeRunHelper helper = new FreezeRunHelper();

        new BukkitRunnable() {
            @Override
            public void run() {

                if(arena.getState().equals(ArenaState.WAITING)) {
                    this.cancel();
                    Bukkit.getPluginManager().callEvent(new GameFinishedEvent(arena));
                    arena.setState(ArenaState.WAITING);
                    arena.setGametime(120);
                }

                if(arena.getGametime() > 0) {
                    arena.getPlayers().forEach(player -> {
                        PlayerState state = PlayerManager.getPlayerState().get(player.getUniqueId());

                        if((arena.getGametime() % 35) == 0) {
                            if(state.isFreezing()) {
                                player.sendTitle("", helper.formatInGameColor("&b&lYou are freezing now"), 15, 35, 15);
                            }

                            if(state.isHost()) {
                                player.sendTitle("", helper.formatInGameColor("&c&lYou are host"), 15, 25, 10);
                            }
                        }

                        helper.sendActionBar(player, helper.formatInGameColor("&7Time left: &e" + arena.getGametime() + " seconds."));
                    });

                    new ArenaManager().sendTimeOutTitle(arena);

                } else {
                    arena.setState(ArenaState.FINISHED);

                    arena.getPlayers().forEach(player -> {
                        player.sendMessage(helper.formatInGameColor("&r"));
                        player.sendMessage(helper.formatInGameColor("&8[&bFreezeRun&8] &c&lGAME OVER!"));
                        player.sendMessage(helper.formatInGameColor("&r"));

                        player.sendMessage(helper.formatInGameColor("&8[&bFreezeRun&8] &aTeleporting you back to lobby!"));

                        Bukkit.getScheduler().scheduleSyncDelayedTask(FreezeRun.getInstance(), () -> {
                            player.sendMessage(helper.formatInGameColor("&8[&bFreezeRun&8] &fThank you for help us testing!"));
                        }, helper.secondToTick(3));
                    });

                    this.cancel();
                    Bukkit.getPluginManager().callEvent(new GameFinishedEvent(arena));
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(arena));
                    arena.setState(ArenaState.WAITING);
                    arena.setGametime(120);
                }
                arena.setGametime(arena.getGametime()-1);
            }
        }.runTaskTimer(FreezeRun.getInstance(), 0, 20);
    }

    @EventHandler
    public void onFinished(GameFinishedEvent event) {
        Arena arena = event.getArena();
        arena.setState(ArenaState.FINISHED);
        FreezeRunHelper helper = new FreezeRunHelper();

        if (event.getWinner() != null) {
            event.getArena().getPlayers().forEach(player -> {
                player.sendMessage(helper.formatInGameColor("&c&lTHE HOST &eis a winner!"));
                player.sendTitle(helper.formatInGameColor("&c&lTHE HOST &fwon!"), "Finishing the game..", 15, 30, 15);
            });
        }else {
            new ArenaManager().sendTitleToAllPlayerInArena(arena, helper.formatInGameColor("&a&lTHE ESCAPEE &fwon!"), "Finishing the game..");
            new ArenaManager().sendChatToAllPlayerInArena(arena, helper.formatInGameColor("&a&lTHE ESCAPEE &eis a winner!"));
        }
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        Arena arena = event.getArena();
        arena.setState(ArenaState.WAITING);
        arena.reset(-1, 180);

        arena.getPlayers().forEach(player -> {
            player.setWalkSpeed(0.2f);
            player.getInventory().clear();
            player.teleport(new Location(Bukkit.getWorld("world"), -11.5, 13, -4, 90.5f, 4.2f));
        });

        arena.getPlayers().clear();
        PlayerManager.getPlayerState().clear();
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        try {
            Arena arena = new PlayerManager().findArena(event.getPlayer());
            if(arena != null) {
                if(arena.getState().equals(ArenaState.PLAYING)) {
                    Player player = event.getPlayer();
                    PlayerState state = PlayerManager.getPlayerState().get(player.getUniqueId());

                    if(state.isFreezing()) {
                        event.setCancelled(true);
                        player.teleport(event.getFrom());
                    }
                }
            }
        } catch (Exception error) {
            //
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        try {
            Arena arena = new PlayerManager().findArena(event.getPlayer());
            if(arena != null) {
                if(arena.getState().equals(ArenaState.PLAYING)) {
                    Player player = event.getPlayer();
                    PlayerState state = PlayerManager.getPlayerState().get(player.getUniqueId());

                    if(state.isFreezing()) {
                        if (event.getTo().getX() == event.getFrom().getX() && event.getTo().getY() == event.getFrom().getY() && event.getTo().getZ() == event.getFrom().getZ()) {
                            event.getPlayer().teleport(event.getFrom());
                        }
                    }
                }
            }
        } catch (Exception error) {
            //
        }
    }

    @EventHandler
    public void onJoinArena(PlayerJoinArenaEvent event) {
        event.getPlayer().sendMessage("You joined the arena!");
        new ArenaManager().sendChatToAllPlayerInArena(event.getArena(),new FreezeRunHelper().formatInGameColor("&8# &b" + event.getPlayer().getName() + " &7joined the lobby &e(" + event.getArena().getPlayers().size() + "/" + event.getArena().getMaxplayer() +  ")"));

        if(event.getArena().getPlayers().size() > 1) {
            if(event.getArena().getLobbytime() == -1) {
                event.getArena().setLobbytime(30);
                new ArenaManager().startOver(event.getArena().getId());
            }
        }

        if(!PlayerManager.getPlayerState().containsKey(event.getPlayer().getUniqueId())) {
            PlayerManager.getPlayerState().put(event.getPlayer().getUniqueId(), new PlayerState(event.getPlayer().getUniqueId(), event.getArena(), false, false));
        }
    }

    @EventHandler
    public void onLeaveArena(PlayerLeaveArenaEvent event) {
        event.getPlayer().sendMessage("You leaved the arena!");
        new ArenaManager().sendChatToAllPlayerInArena(event.getArena(),new FreezeRunHelper().formatInGameColor("&8# &b" + event.getPlayer().getName() + " &7leaved the lobby &e(" + event.getArena().getPlayers().size() + "/" + event.getArena().getMaxplayer() +  ")"));
        if(PlayerManager.getPlayerState().containsKey(event.getPlayer().getUniqueId())) {
            PlayerManager.getPlayerState().remove(event.getPlayer().getUniqueId());
        }

        if(event.getArena().getState().equals(ArenaState.WAITING)) {
            if(event.getArena().getPlayers().size() == 1) {
                event.getArena().setLobbytime(-1);
            }
        }
    }

    @EventHandler
    public void onLeaveArena(PlayerQuitEvent event) {
        Arena arena = new PlayerManager().findArena(event.getPlayer());
        if(ArenaManager.getArenaList().get(arena.getId()).getState().equals(ArenaState.PLAYING)) {
            Player player = event.getPlayer();
            PlayerState state = PlayerManager.getPlayerState().get(player.getUniqueId());

            if(state.isHost()) {
                new ArenaManager().forceStop(arena, "&cThe host is leave from game!");
            }
        }
    }
}
