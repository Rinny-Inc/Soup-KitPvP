package io.noks.kitpvp.abstracts;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.noks.kitpvp.enums.Rarity;

public abstract class Abilities {
	private final String name;
	private final ItemStack icon;
	private final Rarity rarity;
	private final Long cooldown;
	private final String[] lore;
	
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

	public ItemStack sword() {
		final ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		final ItemMeta meta = item.getItemMeta();
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		return item;
	}
	
	public ItemStack[] armors() {
		return new ItemStack[] {new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_HELMET)};
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
	
	public void onKill(Player killer) {}
}
