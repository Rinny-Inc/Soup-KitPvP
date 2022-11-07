package io.noks.kitpvp.utils;

import java.util.Arrays;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	public ItemStack getItemStack(ItemStack item, @Nullable String name, @Nullable String[] lore) {
		ItemStack i = item;
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		if (lore != null) im.setLore(Arrays.asList(lore));
		i.setItemMeta(im);
		return i;
	}

	public ItemStack getItemMaterial(Material m, int data, String name) {
		ItemStack i = new ItemStack(m, 1, (short) data);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		i.setItemMeta(im);
		return i;
	}

	public ItemStack getItemMaterial(Material m, String name) {
		return getItemMaterial(m, name, 1);
	}

	public ItemStack getItemMaterial(Material m, String name, int amount) {
		ItemStack i = new ItemStack(m, amount);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		i.setItemMeta(im);
		return i;
	}

	public ItemStack getItemMaterial(Material m, int amount) {
		return new ItemStack(m, amount);
	}

	public ItemStack getItemUnbreakable(Material material) {
		return getItemUnbreakable(material, null);
	}

	public ItemStack getItemUnbreakable(Material material, @Nullable String name) {
		ItemStack i = new ItemStack(material);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		im.spigot().setUnbreakable(true);
		i.setItemMeta(im);
		return i;
	}

	public ItemStack getAbilitiesSelector() {
		return getItemStack(new ItemStack(Material.BOOK), ChatColor.GRAY + "Your abilities", new String[] {ChatColor.GRAY + "Choose your ability to fight other players"});
	}
}
