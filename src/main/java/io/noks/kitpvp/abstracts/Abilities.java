package io.noks.kitpvp.abstracts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.noks.kitpvp.enums.Rarity;

public abstract class Abilities {
	private String name;
	private ItemStack icon;
	private Rarity rarity;
	private Long cooldown;
	private String[] lore;
	
	public Abilities(String name, ItemStack icon, Rarity rarity, Long cooldown, String[] lore) {
		this.name = name;
		this.icon = icon;
		this.rarity = rarity;
		this.cooldown = cooldown;
		this.lore = lore;
	}
	
	public ItemStack specialItem() {
		return new ItemStack(Material.MUSHROOM_SOUP);
	}
	
	public String specialItemName() {
		return null;
	}

	public Material sword() {
		return Material.STONE_SWORD;
	}
	
	public ItemStack[] armors() {
		return new ItemStack[] {null, null, null, null};
	}
	
	public boolean hasCooldown() {
		return (this.cooldown.longValue() != 0L);
	}
	
	public String getName() {
		return this.name;
	}
	
	public ItemStack getIcon() {
		return this.icon;
	}
	
	public Rarity getRarity() {
		return this.rarity;
	}
	
	public Long getCooldown() {
		return this.cooldown;
	}
	
	public String[] getLore() {
		return this.lore;
	}
	
	public ItemStack getUnbreakableItemStack(Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		im.spigot().setUnbreakable(true);
		item.setItemMeta(im);
		return item;
	}
}
