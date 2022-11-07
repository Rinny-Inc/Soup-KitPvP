package io.noks.kitpvp.abstracts;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.listeners.abilities.Archer;
import io.noks.kitpvp.listeners.abilities.CookieMonster;
import io.noks.kitpvp.listeners.abilities.Switcher;

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
	
	public void onKill(Player killer) {
		if (this instanceof Archer || this instanceof CookieMonster || this instanceof Switcher) {
			if (killer.getInventory().firstEmpty() == -1 && (this instanceof Archer ? !killer.getInventory().contains(Material.ARROW) : !killer.getInventory().contains(this.specialItem()))) {
				killer.getWorld().dropItem(killer.getLocation(), Main.getInstance().getItemUtils().getItemStack(new ItemStack((this instanceof Archer ? Material.ARROW : this.specialItem().getType()), 2), (this instanceof Archer ? null : ChatColor.RED + this.specialItemName()), null));
				return;
			}
			killer.getInventory().addItem(new ItemStack[] { Main.getInstance().getItemUtils().getItemStack(new ItemStack((this instanceof Archer ? Material.ARROW : this.specialItem().getType()), 2), (this instanceof Archer ? null : ChatColor.RED + this.specialItemName()), null) });
		}
	}
}
