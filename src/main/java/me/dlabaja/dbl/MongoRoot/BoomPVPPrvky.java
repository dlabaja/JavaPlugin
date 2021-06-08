package me.dlabaja.dbl.MongoRoot;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class BoomPVPPrvky {
    public static HashMap<String,String> classa =  new HashMap<>(); //sem se ukládají třídy co si vyberete

    //vrátí nezničitelný a enchantovaný item
    public static ItemStack MakeItem(Material item, int count, Enchantment ench, int lvl) {
        ItemStack stack = new ItemStack(item, count);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.addEnchant(ench, lvl, true);
        stackMeta.setUnbreakable(true);
        stack.setItemMeta(stackMeta);
        return stack;
    }

    //vrátí nezničitelný a enchantovaný armor
    public static ItemStack MakeArmor(Material item, int count, Enchantment ench, int lvl) {
        ItemStack stack = new ItemStack(item, count);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.addEnchant(ench, lvl, true);
        stackMeta.setUnbreakable(true);
        stackMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        stack.setItemMeta(stackMeta);
        return stack;
    }

    //vrátí nezničitelný item
    public static ItemStack MakeItemUnbreakable(Material item, int count) {
        ItemStack stack = new ItemStack(item, count);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.setUnbreakable(true);
        stack.setItemMeta(stackMeta);
        return stack;
    }

    //vrátí nezničitelný armor
    public static ItemStack MakeArmorUnbreakable(Material item, int count) {
        ItemStack stack = new ItemStack(item, count);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.setUnbreakable(true);
        stackMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        stack.setItemMeta(stackMeta);
        return stack;
    }

    //vrátí SwappEgg
    public static ItemStack SwapEgg(int count) {
        ItemStack is = new ItemStack(Material.EGG, count);
        ItemMeta bm = is.getItemMeta();
        bm.setDisplayName(ChatColor.GOLD + "SwapEgg");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Tohle bych po politicích neházel...");
        bm.setLore(lore);
        is.setItemMeta(bm);
        return is;
    }
}
