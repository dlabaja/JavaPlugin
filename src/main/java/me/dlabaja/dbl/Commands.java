package me.dlabaja.dbl;

import me.dlabaja.dbl.MongoRoot.BoomPVPPrvky;
import me.dlabaja.dbl.MongoRoot.MongoBoomPVP;
import me.dlabaja.dbl.MongoRoot.MongoData;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class Commands implements CommandExecutor {

    //Vytvoří konstruktor pro příkazy
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //teleportuje na lobby
        if (command.getName().equalsIgnoreCase("lobby")) {
            Player player = (Player) sender;
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv tp " + player.getName() + " world");
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv tp " + player.getName() + " world");
        }

        //dá hráči boompvp kit
        if (command.getName().equalsIgnoreCase("boomkit")) { // boomkit JMENO VOLBA(1-4)
            Player player = Bukkit.getPlayer(args[0]);
            String volba = args[1];
            if(volba.equals("1")){ //pilot
                player.getInventory().clear();
                player.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.ENDER_PEARL, 16));
                player.getPlayer().getInventory().setItem(0, BoomPVPPrvky.MakeItem(Material.STICK, 1, Enchantment.KNOCKBACK, 5));
                player.getPlayer().getInventory().setItem(1, BoomPVPPrvky.MakeItemUnbreakable(Material.FISHING_ROD, 1));
                player.getPlayer().getInventory().setItem(2, new ItemStack(Material.FIREWORK_ROCKET, 2));
                player.getPlayer().getInventory().setChestplate(BoomPVPPrvky.MakeArmorUnbreakable(Material.ELYTRA, 1));
                player.getPlayer().getInventory().setLeggings(BoomPVPPrvky.MakeArmorUnbreakable(Material.DIAMOND_LEGGINGS, 1));
                player.getPlayer().getInventory().setHelmet(BoomPVPPrvky.MakeArmorUnbreakable(Material.CHAINMAIL_HELMET, 1));
                BoomPVPPrvky.classa.put(player.getName(), volba);
            }
            if(volba.equals("2")){ //fighter
                player.getInventory().clear();
                player.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.ENDER_PEARL, 16));
                player.getPlayer().getInventory().setItem(0, BoomPVPPrvky.MakeItem(Material.STICK, 1, Enchantment.KNOCKBACK, 5));
                player.getPlayer().getInventory().setItem(1, BoomPVPPrvky.MakeItem(Material.IRON_AXE, 1, Enchantment.DAMAGE_ALL, 13));
                player.getPlayer().getInventory().setItem(2, BoomPVPPrvky.MakeItemUnbreakable(Material.SHIELD, 1));
                player.getPlayer().getInventory().setChestplate(BoomPVPPrvky.MakeArmor(Material.IRON_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
                player.getPlayer().getInventory().setLeggings(BoomPVPPrvky.MakeArmorUnbreakable(Material.IRON_LEGGINGS, 1));
                player.getPlayer().getInventory().setBoots(BoomPVPPrvky.MakeArmorUnbreakable(Material.CHAINMAIL_BOOTS, 1));
                BoomPVPPrvky.classa.put(player.getName(), volba);
            }
            if(volba.equals("3")){ //archer
                player.getInventory().clear();
                player.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.ENDER_PEARL, 16));
                player.getPlayer().getInventory().setItem(0, BoomPVPPrvky.MakeItem(Material.STICK, 1, Enchantment.KNOCKBACK, 5));
                player.getPlayer().getInventory().setItem(1, BoomPVPPrvky.MakeItem(Material.BOW, 1, Enchantment.ARROW_DAMAGE, 255));
                player.getPlayer().getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
                player.getPlayer().getInventory().setChestplate(BoomPVPPrvky.MakeArmor(Material.CHAINMAIL_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
                player.getPlayer().getInventory().setLeggings(BoomPVPPrvky.MakeArmorUnbreakable(Material.LEATHER_LEGGINGS, 1));
                player.getPlayer().getInventory().setHelmet(BoomPVPPrvky.MakeArmorUnbreakable(Material.CHAINMAIL_HELMET, 1));
                BoomPVPPrvky.classa.put(player.getName(), volba);
            }
            if(volba.equals("4")){ //troller
                player.getInventory().clear();
                player.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.ENDER_PEARL, 16));
                player.getPlayer().getInventory().setItem(0, BoomPVPPrvky.MakeItem(Material.STICK, 1, Enchantment.KNOCKBACK, 5));
                player.getPlayer().getInventory().setItem(1, BoomPVPPrvky.SwapEgg(2));
                player.getPlayer().getInventory().setChestplate(BoomPVPPrvky.MakeArmor(Material.IRON_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
                player.getPlayer().getInventory().setLeggings(BoomPVPPrvky.MakeArmorUnbreakable(Material.IRON_LEGGINGS, 1));
                player.getPlayer().getInventory().setBoots(BoomPVPPrvky.MakeArmorUnbreakable(Material.LEATHER_BOOTS, 1));
                BoomPVPPrvky.classa.put(player.getName(), volba);
            }
        }

        return true;
    }
}


