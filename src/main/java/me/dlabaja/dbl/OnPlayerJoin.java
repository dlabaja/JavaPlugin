package me.dlabaja.dbl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {

    //teleportuje hráče na lobby po připojení
    @EventHandler
    public void OnPlayerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.WHITE +""+ ChatColor.BOLD + "[" + ChatColor.GREEN +""+ ChatColor.BOLD + "+" +ChatColor.WHITE +""+ ChatColor.BOLD + "] " + event.getPlayer().getName());
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv tp " + event.getPlayer().getName() + " world");
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv tp " + event.getPlayer().getName() + " world");
        event.getPlayer().getActivePotionEffects().clear();
    }
}
