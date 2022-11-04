package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;

public class Grandpa extends Abilities {

	public Grandpa() {
		super("Grandpa", new ItemStack(Material.STICK), Rarity.COMMON, 0L, new String[] { ChatColor.AQUA + "Protect the tower" });
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getEnchantedItemStack(this.getIcon().getType(), Enchantment.KNOCKBACK, 1);
	}
	
	@Override
	public String specialItemName() {
		return "Grandpa Stick";
	}
	
	private ItemStack getEnchantedItemStack(Material material, Enchantment enchantment, int enchantmentLevel) {
		final ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchantment, enchantmentLevel);
		return item;
	}
}
