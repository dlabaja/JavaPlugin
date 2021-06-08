package me.dlabaja.dbl;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.util.Random;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        //registruje listenery a commandy
        Commands cmd = new Commands();
        System.out.println("uwu zapnuto");
        System.out.println("jj");
        getServer().getPluginManager().registerEvents(new BoomPVP(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        this.getCommand("lobby").setExecutor(cmd);
        this.getCommand("boomkit").setExecutor(cmd);
    }

    @Override
    public void onDisable() {
        System.out.println("uwu vypnuto");
    }
}


