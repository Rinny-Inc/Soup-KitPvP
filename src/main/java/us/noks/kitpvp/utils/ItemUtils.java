package us.noks.kitpvp.utils;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

public class ItemUtils {
	private static ItemUtils instance = new ItemUtils();

	public static ItemUtils getInstance() {
		return instance;
	}

	public ItemStack getItemStack(ItemStack item, @Nullable String name, @Nullable String[] lore) {
		ItemStack i = item;
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		if (lore != null)
			im.setLore(Lists.newArrayList(lore));
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
		return getItemMaterial(Material.BOOK, ChatColor.GRAY + "Your abilities");
	}
}
