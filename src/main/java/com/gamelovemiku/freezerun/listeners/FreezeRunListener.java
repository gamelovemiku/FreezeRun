package com.gamelovemiku.freezerun.listeners;

import com.gamelovemiku.freezerun.FreezeRun;
import com.gamelovemiku.freezerun.FreezeRunHelper;
import com.gamelovemiku.freezerun.arena.Arena;
import com.gamelovemiku.freezerun.arena.ArenaManager;
import com.gamelovemiku.freezerun.arena.ArenaState;
import com.gamelovemiku.freezerun.events.GameEndEvent;
import com.gamelovemiku.freezerun.events.GameStartEvent;
import com.gamelovemiku.freezerun.events.PlayerJoinArenaEvent;
import com.gamelovemiku.freezerun.events.PlayerLeaveArenaEvent;
import com.gamelovemiku.freezerun.player.PlayerManager;
import com.gamelovemiku.freezerun.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FreezeRunListener implements Listener {

    public FreezeRun plugin = FreezeRun.getInstance();
    private int countFreezed = 0;
    private int select_host_time = 15;

    public FreezeRunListener() {

    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if(ArenaManager.getArenaList().get("test1_arena").getState().equals(ArenaState.PLAYING)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(new FreezeRunHelper().formatInGameColor("&b&lFreezeRun> &cBlock breaking is disabled. If you playing in the game."));
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        //get arena player are in ** but now is fixes
        if(ArenaManager.getArenaList().get("test1_arena").getState().equals(ArenaState.PLAYING)) {
            event.setCancelled(true);

            try {
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
                        target.setWalkSpeed(0.2f);
                        damager.sendMessage(helper.formatInGameColor("&r"));
                        damager.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &a&lRELEASED: &2&l" + target.getName()));
                        damager.sendMessage(helper.formatInGameColor("&r"));
                        damager.sendTitle("", helper.formatInGameColor("&a&lReleasing: &a" + target.getName()), 15,35,15);
                        targetState.setFreezing(false);
                    }
                } else {
                    if(damagerState.isHost()) {
                        target.setWalkSpeed(0f);

                        damager.playSound(damager.getLocation(), Sound.BLOCK_SNOW_BREAK, 1,1);
                        target.sendMessage(helper.formatInGameColor("&r"));
                        target.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &b&lYOU ARE FREEZING BY " + damager.getName()));
                        target.sendMessage(helper.formatInGameColor("&r"));

                        damager.sendTitle("", helper.formatInGameColor("&b&lFreezing: &a" + target.getName()), 15,35,15);
                        damager.sendMessage(helper.formatInGameColor("&r"));
                        damager.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &b&lFREEZE: &3&l" + target.getName()));
                        damager.sendMessage(helper.formatInGameColor("&r"));

                        targetState.setFreezing(true);

                    } else {
                        damager.playSound(damager.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
                        damager.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &e&lYOU NOT A HOST. RUN AWAY!"));
                    }
                }

                Arena arena = ArenaManager.getArenaList().get("test1_arena");
                arena.getDummyplayers().forEach(name -> {
                    Player player = Bukkit.getPlayer(name);
                    PlayerState state = PlayerManager.getPlayerState().get(player.getUniqueId());
                    if(state.isFreezing()) {
                        countFreezed++;
                    }
                });

                arena.getDummyplayers().forEach(name -> {
                    Player player = Bukkit.getPlayer(name);
                    player.sendMessage(helper.formatInGameColor("&e&l" + damager.getName() + " IS FREEZING " + countFreezed + "/" + (ArenaManager.getArenaList().get("test1_arena").getDummyplayers().size()-1)));
                });

                if(arena.getDummyplayers().size()-1 == countFreezed) {
                    arena.setGametime(180);
                }

                countFreezed = 0;
            } catch (Exception e) {
                //
            }

        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        /**
        if(ArenaManager.getArenaList().get("test1_arena").getState().equals(ArenaState.PLAYING)) {
            Player target = event.getPlayer();
            PlayerState targetState = PlayerManager.getPlayerState().get(target.getUniqueId());
            if(targetState.isFreezing()) {
                event.setCancelled(true);
            }
        }
        */
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        Arena arena = event.getArena();
        Random rnd = new Random();
        FreezeRunHelper helper = new FreezeRunHelper();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(select_host_time > 0) {
                    arena.getDummyplayers().forEach(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        player.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &eFinding a host in " + select_host_time + " seconds."));
                    });
                } else {

                    Player host = Bukkit.getPlayer(arena.getDummyplayers().get(rnd.nextInt(arena.getDummyplayers().size())));
                    PlayerState pm = PlayerManager.getPlayerState().get(host.getUniqueId());
                    pm.setHost(true);

                    arena.getDummyplayers().forEach(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        PlayerState state = PlayerManager.getPlayerState().get(player.getUniqueId());

                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, helper.secondToTick(180), 4));
                        player.setFoodLevel(20);

                        if(state.isHost()) {
                            player.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &c&lYou are it! FREEZE OTHER PLAYER!"));
                            player.sendTitle(helper.formatInGameColor("&c&lTHE HOST"), helper.formatInGameColor("&eCatch other player and freeze them!"), 15, 35, 15);
                            player.setWalkSpeed(0.6f);
                        } else {
                            player.sendMessage(helper.formatInGameColor("&b&lFreezeRun> &aYou need to escaping from the host."));
                            player.sendTitle(helper.formatInGameColor("&a&lTHE ESCAPE"), helper.formatInGameColor("&fRun away from host and survive!"), 15, 35, 15);
                            player.setWalkSpeed(0.65f);
                        }

                        player.sendMessage(helper.formatInGameColor(""));
                    });

                    this.cancel();
                    select_host_time = 10;
                }
                select_host_time--;
            }
        }.runTaskTimer(FreezeRun.getInstance(), 0, 20);
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        Arena arena = event.getArena();

        arena.getDummyplayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.setWalkSpeed(0.2f);
        });

        arena.getDummyplayers().clear();
        PlayerManager.getPlayerState().clear();
    }

    @EventHandler
    public void onJoinArena(PlayerJoinArenaEvent event) {
        event.getPlayer().sendMessage("You joined the arena!");
        new ArenaManager().sendChatToAllPlayerInArena(event.getArena(),new FreezeRunHelper().formatInGameColor("&e# " + event.getPlayer().getName() + " joined the lobby (" + event.getArena().getDummyplayers().size() + "/8)"));
        if(!PlayerManager.getPlayerState().containsKey(event.getPlayer().getUniqueId())) {
            PlayerManager.getPlayerState().put(event.getPlayer().getUniqueId(), new PlayerState(event.getPlayer().getUniqueId(), event.getArena(), false, false));
        }
    }

    @EventHandler
    public void onLeaveArena(PlayerLeaveArenaEvent event) {
        event.getPlayer().sendMessage("You leaved the arena!");
        new ArenaManager().sendChatToAllPlayerInArena(event.getArena(),new FreezeRunHelper().formatInGameColor("&e# " + event.getPlayer().getName() + " leaved the lobby (" + event.getArena().getDummyplayers().size() + "/8)"));
        if(PlayerManager.getPlayerState().containsKey(event.getPlayer().getUniqueId())) {
            PlayerManager.getPlayerState().remove(event.getPlayer().getUniqueId());
        }
    }
}
