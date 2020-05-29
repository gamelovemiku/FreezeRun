package com.gamelovemiku.freezerun;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class FreezeRunHelper {

    public String formatInGameColor(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void log(String msg){
        Bukkit.getLogger().info(msg);
    }

    public void runOnConsole(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
    }

    public int secondToTick(int second) {
        return 20 * second;
    }

    public void logMessage(String msg) {
        Bukkit.getServer().getLogger().info(msg);
    }

    public void sendSubtitle(Player player, String message, String description) {
        player.sendTitle(null, message, 15, 40, 15);
    }

    public int randomNumber(int max) {
        Random rnd = new Random();
        int num = rnd.nextInt(max);
        if (num == 0) {
            return 1;
        }
        return num;
    }

    public void getServerTime() {
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        String time = date.format(Calendar.getInstance().getTime());
        String times[] = time.split(":");
        Bukkit.broadcastMessage("########## Server Time: " + times[0] + " นาฬิกา " + times[1] + " นาที " + times[2] + " วินาที");
    }

    public void displayErrorContact(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_CAT_BEG_FOR_FOOD, 1,1);
        player.sendMessage("");
        player.sendMessage(formatInGameColor("&cหากคุณเห็นข้อความนี้.. หมายความว่าเซิร์ฟเวอร์ทำงานไม่ปกติอยู่ในขณะนี้"));
        player.sendMessage(formatInGameColor("&cโปรดติดต่อ Admin หรือทีมงานให้ด่วนที่สุด เพื่อการแก้ไขปัญหาที่รวดเร็ว"));
        player.sendMessage("");
        player.sendMessage(formatInGameColor("&c&l[&6รายงานปัญหานี้&c&l]&r  &c&l[&6แสดงรายชื่อทีมงานที่กำลังออนไลน์&c&l]"));
    }

    public void displayErrorContact(Player player, String error) {
        player.playSound(player.getLocation(), Sound.ENTITY_CAT_BEG_FOR_FOOD, 1,1);
        player.sendMessage("");
        player.sendMessage(formatInGameColor("&cหากคุณเห็นข้อความนี้.. หมายความว่าเซิร์ฟเวอร์ทำงานไม่ปกติอยู่ในขณะนี้"));
        player.sendMessage(formatInGameColor("&cโปรดติดต่อ Admin หรือทีมงานให้ด่วนที่สุด เพื่อการแก้ไขปัญหาที่รวดเร็ว"));
        player.sendMessage(formatInGameColor("&4&lPLOBLEM LOG: &7" + error.toUpperCase()));
        player.sendMessage("");
        player.sendMessage(formatInGameColor("&c&l[&6รายงานปัญหานี้&c&l]&r  &c&l[&6แสดงรายชื่อทีมงานที่กำลังออนไลน์&c&l]"));
    }

    public void sendJsonMessage(Player player, String msg) {
        runOnConsole("tellraw " + player.getName() + " " + msg);
    }

    public void sendActionBar(Player player, String msg) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formatInGameColor(msg)));
    }

}
