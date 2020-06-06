package com.gamelovemiku.freezerun.command;

import com.gamelovemiku.freezerun.FreezeRun;
import com.gamelovemiku.freezerun.arena.ArenaLoader;
import com.gamelovemiku.freezerun.arena.ArenaManager;
import com.gamelovemiku.freezerun.arena.ArenaState;
import com.gamelovemiku.freezerun.player.PlayerManager;
import com.gamelovemiku.freezerun.player.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCMD implements CommandExecutor {

    ArenaManager arenaManager = new ArenaManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && cmd.getName().equalsIgnoreCase("fr")) {

            Player p = ((Player) sender).getPlayer();

            if(args.length == 0) {
                sender.sendMessage(ChatColor.GOLD + "FreezeRun by gamelovemiku");
                sender.sendMessage(ChatColor.GRAY + "Use /fr help for a list of commands.");
                return true;
            }
            if(args[0].equalsIgnoreCase("cfg")) {
                sender.sendMessage(ChatColor.GOLD + FreezeRun.getInstance().getConfig().getString("msg").toString());

                new ArenaLoader().loadArenas();

                return true;
            }
            if(args[0].equalsIgnoreCase("reload")) {
                FreezeRun.getInstance().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded");
                return true;
            }
            if(args[0].equalsIgnoreCase("aio")) {
                arenaManager.listDummyPlayer(args[1]);
                arenaManager.join(p, args[1]);
                arenaManager.startOver(args[1]);
                p.sendMessage("----------------------- AFTER -------------------");
                arenaManager.listDummyPlayer(args[1]);
                return true;
            }
            if(args[0].equalsIgnoreCase("load")) {
                ArenaManager.getArenaList().clear();
                new ArenaLoader().loadArenas();
                return true;
            }
            if(args[0].equalsIgnoreCase("cps")) {
                p.sendMessage("PlayerManager Value: " + PlayerManager.getPlayerState().size());
                return true;
            }
            if(args[0].equalsIgnoreCase("info")) {
                try {
                    PlayerState info = PlayerManager.getPlayerState().get(p.getUniqueId());
                    p.sendMessage("Your status:");
                    p.sendMessage("Is in Arena: " + info.getArena().getName());
                    p.sendMessage("Is Host (It): " + info.isHost());
                    p.sendMessage("Is Freeze: " + info.isFreezing());
                } catch (NullPointerException error) {
                    p.sendMessage(ChatColor.RED + "You aren't join any area yet!");
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("speed")) {
                p.setWalkSpeed(Float.parseFloat(args[1]));
                p.sendMessage(ChatColor.GREEN + "Set speed to " + args[1]);
                return true;
            }
            if(args[0].equalsIgnoreCase("save")) {
                new ArenaLoader().saveArenas();
                p.sendMessage(ChatColor.GREEN + "Saved");
                return true;
            }
            if(args[0].equalsIgnoreCase("start")) {
                arenaManager.startOver(args[1]);
                return true;
            }
            if(args[0].equalsIgnoreCase("add")) {
                sender.sendMessage(ChatColor.GOLD + "Arena created! with id: " + arenaManager.create(args[1]));
                return true;
            }
            if(args[0].equalsIgnoreCase("delete")) {
                arenaManager.delete(args[1]);
                sender.sendMessage(ChatColor.GOLD + "Arena removed.");
                return true;
            }
            if(args[0].equalsIgnoreCase("setid")) {
                arenaManager.setId(args[1], args[2]);
                sender.sendMessage(ChatColor.GOLD + "Id set.");
                return true;
            }
            if(args[0].equalsIgnoreCase("setname")) {
                arenaManager.setName(args[1], args[2]);
                sender.sendMessage(ChatColor.GOLD + "Name set.");
                return true;
            }
            if(args[0].equalsIgnoreCase("setspawn")) {
                arenaManager.setGameSpawn(p.getLocation(), args[1]);
                sender.sendMessage(ChatColor.GOLD + "Spawn set.");
                return true;
            }
            if(args[0].equalsIgnoreCase("spawn")) {
                arenaManager.spawnToGame(p);
                sender.sendMessage(ChatColor.GOLD + "You are in spawn.");
                return true;
            }
            if(args[0].equalsIgnoreCase("setlobby")) {
                arenaManager.setGameLobbyLocation(p.getLocation(), args[1]);
                sender.sendMessage(ChatColor.GOLD + "Lobby set.");
                return true;
            }
            if(args[0].equalsIgnoreCase("lobby")) {
                arenaManager.spawnToLobby(p);
                sender.sendMessage(ChatColor.GOLD + "You are back to spawn");
                return true;
            }
            if(args[0].equalsIgnoreCase("setmainlobby")) {
                sender.sendMessage(ChatColor.GOLD + "Main lobby set.");
                return true;
            }
            if(args[0].equalsIgnoreCase("join")) { //add warp to lobby
                arenaManager.join(p, args[1]);
                //sender.sendMessage(ChatColor.GOLD + "You joined: " + args[1]);
                return true;
            }
            if(args[0].equalsIgnoreCase("leave")) {
                arenaManager.leave(p);
                //sender.sendMessage(ChatColor.GOLD + "You leaved: " + args[1]);
                return true;
            }
            if(args[0].equalsIgnoreCase("setstate")) {
                arenaManager.setState(args[1], ArenaState.valueOf(args[2]));
                sender.sendMessage(ChatColor.GOLD + "The arena " + args[1] + " state update to " + args[2]);
                return true;
            }
            if(args[0].equalsIgnoreCase("list")) {
                arenaManager.listDummyPlayer(args[1]);
                return true;
            }
            if(args[0].equalsIgnoreCase("total")) {
                sender.sendMessage(ChatColor.GOLD + "Total of all arena: " + arenaManager.totalArena());
                return true;
            }
            if(args[0].equalsIgnoreCase("mygame")) {
                p.sendMessage("You playing in " + new PlayerManager().findArena(p).getName());
                return true;
            }
            if(args[0].equalsIgnoreCase("autojoin")) {
                new PlayerManager().autoJoin(p);
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
            return true;
        }
        return true;
    }

}
