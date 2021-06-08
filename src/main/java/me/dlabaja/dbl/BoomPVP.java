package me.dlabaja.dbl;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import me.dlabaja.dbl.MongoRoot.BoomPVPPrvky;
import me.dlabaja.dbl.MongoRoot.MongoBoomPVP;
import me.dlabaja.dbl.MongoRoot.MongoData;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.setOnInsert;

//import com.mongodb.async.*;

public class BoomPVP implements Listener {

    //Volám když hráč umře
    public void OnPlayerDeath(Player event) {
        //smaže všechny entity co hráč vystřelil
        for (Entity ent : event.getNearbyEntities(200, 200, 200)) {
            if (ent.getType() == EntityType.ARROW) {
                Arrow arrow = (Arrow) ent;
                if (arrow.getShooter() == event)
                    ent.remove();
            }
            if (ent.getType() == EntityType.ENDER_PEARL) {
                EnderPearl pearl = (EnderPearl) ent;
                if (pearl.getShooter() == event)
                    ent.remove();
            }
        }

        //odstraňí perly a další věci z inv
        event.setHealth(20);
        if (event.getInventory().getItemInOffHand().getType().equals(Material.ENDER_PEARL) || event.getInventory().getItemInOffHand().getType().equals(Material.ARROW)) {
            event.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
        event.getPlayer().getInventory().remove(Material.ENDER_PEARL);
        event.getPlayer().getInventory().remove(Material.ARROW);
        event.getPlayer().getInventory().remove(Material.EGG);
        event.getPlayer().getInventory().remove(Material.FIREWORK_ROCKET);

        //přidá věci podle třídy kterou má hráč
        if (Objects.requireNonNull(event.getPlayer()).getKiller() != null) {
            if (Objects.requireNonNull(event.getPlayer().getLastDamageCause()).getCause() != EntityDamageEvent.DamageCause.PROJECTILE)
                Objects.requireNonNull(event.getPlayer().getKiller()).getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 2));
            if (BoomPVPPrvky.classa.get(event.getPlayer().getKiller().getName()).equals("1"))
                event.getPlayer().getKiller().getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 1));
            if (BoomPVPPrvky.classa.get(event.getPlayer().getKiller().getName()).equals("3"))
                event.getPlayer().getKiller().getInventory().addItem(new ItemStack(Material.ARROW, 1));
            if (BoomPVPPrvky.classa.get(event.getPlayer().getKiller().getName()).equals("4"))
                event.getPlayer().getKiller().getInventory().addItem(BoomPVPPrvky.SwapEgg(1));
        }

        if (event.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
            event.getInventory().setItemInOffHand(new ItemStack(Material.ENDER_PEARL, 16));
        } else
            event.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 16));
        if (BoomPVPPrvky.classa.get(event.getPlayer().getName()).equals("3"))
            event.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        if (BoomPVPPrvky.classa.get(event.getPlayer().getName()).equals("4")) {
            event.getInventory().addItem(BoomPVPPrvky.SwapEgg(2));
        }
        if (BoomPVPPrvky.classa.get(event.getPlayer().getName()).equals("1"))
            event.getPlayer().getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 2));
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv tp " + event.getName() + " boompvp");
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);

        //replacne hashmapu
        smrti.replace(event.getName(), smrti.get(event.getName()) + 1);
    }

    //Hráč se připojil do hry
    @EventHandler
    public void OnPlayerJoinWorldEvent(PlayerChangedWorldEvent event) {
        if (Bukkit.getServer().getWorld("boompvp") == event.getPlayer().getWorld()) {

            //nový dokument v kolekci db, pokud neexistuje, přidá se
            Document doc = new Document("name", event.getPlayer().getName())
                    .append("kills", 0)
                    .append("deaths", 0)
                    .append("killstreak", 0);
            MongoData.coll.findOneAndUpdate(eq("name", "" + event.getPlayer().getName()), setOnInsert(doc), new FindOneAndUpdateOptions().upsert(true));

            //nahrání dat z db do hashmap
            if (killy.get(event.getPlayer().getName()) == null && smrti.get(event.getPlayer().getName()) == null) {
                MongoBoomPVP findDoc = MongoData.coll.find(eq("name", event.getPlayer().getName())).first();
                killy.put(event.getPlayer().getName(), findDoc.getKills());
                smrti.put(event.getPlayer().getName(), findDoc.getDeaths());
                killstreak.put(event.getPlayer().getName(), 0);
                BoomPVPPrvky.classa.put(event.getPlayer().getName(), "1");
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "boomkit " + event.getPlayer().getName() + " 1");
            }

            //nový scoreboard
            NewScoreboard(event.getPlayer());

            //port na spawn a přidání věcí
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv tp " + event.getPlayer().getName() + " boompvp");
            event.getPlayer().setBedSpawnLocation(new Location(Bukkit.getWorld("boompvp"), 11, 52, 0));
            event.getPlayer().getInventory().clear();
            event.getPlayer().setHealth(20);
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "boomkit " + event.getPlayer().getName() + " " + BoomPVPPrvky.classa.get(event.getPlayer().getName()));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2999999, 1, true));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2999999, 0, true));
        } else {
            event.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }

    //hráč spadne do voidu
    @EventHandler
    public void OnPlayerMoveVoid(PlayerMoveEvent player) {
        //pokud spadne pod y = -10, zabije se
        if (Bukkit.getServer().getWorld("boompvp") == player.getPlayer().getWorld()) {
            if (player.getPlayer().getLocation().getY() <= -10 || player.getPlayer().getLocation().getY() >= 57)
                player.getPlayer().setHealth(0);

            if (Bukkit.getServer().getWorld("world") == player.getPlayer().getWorld()) {
                if (player.getPlayer().getLocation().getY() <= -10)
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv tp " + player.getPlayer().getName() + " world");
            }
        }
    }

    //pokud vystřelí na spawnu, šíp se vrátí
    @EventHandler
    public void OnPlayerLaunchProjectile(ProjectileLaunchEvent event) {
        if (Bukkit.getServer().getWorld("boompvp") == event.getEntity().getWorld()) {
            if (event.getEntity().getLocation().getY() >= 51)
                event.setCancelled(true);
        }
    }

    //Aktivuje se při smrti hráče a dá útočníkovi věci + vygeneruje podle posledního damage death message
    @EventHandler
    public void OnPlayerKill(PlayerDeathEvent event) {
        if (Bukkit.getServer().getWorld("boompvp") == Objects.requireNonNull(event.getEntity().getPlayer()).getWorld()) {
            event.setDeathMessage("");
            killstreak.replace(event.getEntity().getPlayer().getName(), 0);
            String deathmsg;
            if (event.getEntity().getKiller() == event.getEntity().getPlayer() || event.getEntity().getKiller() == null) {
                deathmsg = "☠ " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName() + ChatColor.WHITE + " umřel";
                NewScoreboard(event.getEntity().getPlayer());
            } else {
                if (Objects.requireNonNull(event.getEntity().getLastDamageCause()).getDamage() <= 1)
                    deathmsg = "☠ " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName() + ChatColor.WHITE + " \uD83D\uDDE1 " + ChatColor.GOLD + "" + event.getEntity().getKiller().getName();
                else
                    deathmsg = "☠ " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName() + ChatColor.WHITE + " \uD83E\uDE93 " + ChatColor.GOLD + "" + event.getEntity().getKiller().getName();
                if (Objects.requireNonNull(event.getEntity().getLastDamageCause()).getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE))
                    deathmsg = "☠ " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName() + ChatColor.WHITE + " \uD83C\uDFF9 " + ChatColor.GOLD + "" + event.getEntity().getKiller().getName();
                Objects.requireNonNull(event.getEntity().getKiller()).playSound(event.getEntity().getKiller().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f);
                killy.replace(event.getEntity().getKiller().getName(), killy.get(event.getEntity().getKiller().getName()) + 1);
                killstreak.replace(event.getEntity().getKiller().getName(), killstreak.get(event.getEntity().getKiller().getName()) + 1);
                if (killstreak.get(event.getEntity().getKiller().getName()) % 5 == 0)
                    deathmsg = deathmsg + ChatColor.WHITE + "\n" + "\uD83D\uDD25 " + ChatColor.RED + "" + event.getEntity().getKiller().getName() + ChatColor.WHITE + " má killstreak " + ChatColor.RED + killstreak.get(event.getEntity().getKiller().getName()) + ChatColor.WHITE + " zabití!";
                NewScoreboard(event.getEntity().getKiller());
            }
            event.setDeathMessage(deathmsg);
            OnPlayerDeath(event.getEntity().getPlayer());
            NewScoreboard(event.getEntity().getPlayer());
        }
    }

    //pokud hráč leavne server, všechny údaje se zapíší z hashmapy do db a jeho hashmapa se smaže
    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event) {
        Document doc = new Document("name", event.getPlayer().getName())
                .append("kills", killy.get(event.getPlayer().getName()))
                .append("deaths", smrti.get(event.getPlayer().getName()))
                .append("killstreak", killstreak.get(event.getPlayer().getName()));
        MongoData.collDoc.replaceOne(eq("name", event.getPlayer().getName()), doc);
        killy.remove(event.getPlayer().getName());
        killstreak.remove(event.getPlayer().getName());
        smrti.remove(event.getPlayer().getName());
        BoomPVPPrvky.classa.remove(event.getPlayer().getName());
    }

    //pokud v inventáři klikne na perlu, perla se vrátí zpět. Takhle při smrti nemůže hráč duplikovat itemy
    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        if (Bukkit.getServer().getWorld("boompvp") == event.getWhoClicked().getWorld()) {
            if (Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.ENDER_PEARL) || event.getCurrentItem().getType().equals(Material.ARROW) || Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.FIREWORK_ROCKET) || Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.EGG)) {
                event.setCancelled(true);
            }
        }
    }

    //cooldown na SwapEgg
    HashMap<String, Long> cooldown = new HashMap<>();

    //Logika SwapEggu
    @EventHandler
    public void OnEggUse(ProjectileHitEvent event) {
        if (Bukkit.getServer().getWorld("boompvp") == event.getEntity().getWorld()) {
            if (event.getEntityType().equals(EntityType.EGG) && event.getHitEntity() != null) {
                Location trefeny = event.getHitEntity().getLocation();
                Player attacker = (Player) event.getEntity().getShooter();

                if (!(event.getHitEntity() instanceof Player))
                    return;
                if (cooldown.containsKey(attacker.getName())) {
                    if (cooldown.get(attacker.getName()) < (System.currentTimeMillis() + (1 * 1000))) {
                        long secleft = (cooldown.get(attacker.getName()) / 1000) - (System.currentTimeMillis() / 1000);
                        if (secleft >= 0)
                            attacker.sendMessage("Musíš ještě počkat " + secleft + " sekund");
                        attacker.getInventory().addItem(BoomPVPPrvky.SwapEgg(1));
                        if (secleft < 0) {
                            cooldown.remove(attacker.getName());
                            OnEggUse(event);
                            System.out.println("event");
                        }
                    }
                } else {
                    cooldown.put(attacker.getName(), (System.currentTimeMillis()) + (1 * 1000));
                    event.getHitEntity().teleport(attacker.getLocation());
                    attacker.teleport(trefeny);
                    Player pl = (Player) event.getHitEntity();
                    pl.damage(1, attacker);
                    attacker.playSound(attacker.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    attacker.spawnParticle(Particle.DRAGON_BREATH, attacker.getLocation(), 100);
                }
            }
        }
    }

    //Nespawne kuře při hodu
    @EventHandler
    public void OnChickenSpawn(CreatureSpawnEvent event) {
        if (Bukkit.getServer().getWorld("boompvp") == event.getEntity().getWorld()) {
            if (event.getEntityType().equals(EntityType.CHICKEN)) {
                event.getEntity().remove();
            }
        }
    }

    //hashmapy na staty
    HashMap<String, Integer> killy = new HashMap<>();
    HashMap<String, Integer> smrti = new HashMap<>();
    HashMap<String, Integer> killstreak = new HashMap<>();

    //nový scoreboard
    public void NewScoreboard(Player event) {
        Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        Objective sc;
        try {
            sc = board.registerNewObjective(event.getName(), "dummy", "---" + ChatColor.GOLD + "" + ChatColor.BOLD + "Statistiky" + ChatColor.WHITE + "" + ChatColor.BOLD + "---");
        } catch (IllegalArgumentException e) {
            sc = board.getObjective(event.getName());
            assert sc != null;

            sc.unregister();
            sc = board.registerNewObjective(event.getName(), "dummy", "---" + ChatColor.GOLD + "" + ChatColor.BOLD + "Statistiky" + ChatColor.WHITE + "" + ChatColor.BOLD + "---");
        }
        sc.setDisplaySlot(DisplaySlot.SIDEBAR);

        event.getPlayer().setScoreboard(board);

        float kd;
        if (smrti.get(event.getName()) == 0)
            kd = 0;
        else {
            kd = Math.round((((float) killy.get(event.getName())) / ((float) smrti.get(event.getName()))) * 100) / 100f;
        }

        sc.getScore(String.format("%13s%-6s", ChatColor.GOLD + "Kills:            " + ChatColor.BOLD + "  " + ChatColor.RESET + ChatColor.WHITE, killy.get(event.getName()))).setScore(5);
        sc.getScore(String.format("%13s%-6s", ChatColor.GOLD + "Deaths:           " + ChatColor.WHITE, smrti.get(event.getName()))).setScore(4);
        sc.getScore(String.format("%13s%-6s", ChatColor.GOLD + "K/D:               " + ChatColor.WHITE, kd)).setScore(3);
        sc.getScore("").setScore(2);
        sc.getScore(ChatColor.GOLD + "Killstreak:        " + ChatColor.WHITE + killstreak.get(event.getName())).setScore(1);
        event.setScoreboard(board);
    }
}


