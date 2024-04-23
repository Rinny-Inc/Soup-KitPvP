package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;

public class Chemist extends Abilities {

	public Chemist() {
		super("Chemist", new ItemStack(Material.POTION, 1, (short)44), Rarity.LEGENDARY, 0L, new String[] {ChatColor.AQUA + "Toss lethal potion straight", ChatColor.AQUA + "at your enemies."});
	}
	
	@Override
	public ItemStack sword() {
		final ItemStack item = new ItemStack(Material.IRON_SWORD);
		final ItemMeta meta = item.getItemMeta();
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		return item;
	}
	
	@Override
	public ItemStack[] armors() {
		final ItemStack h = new ItemStack(Material.CHAINMAIL_HELMET);
		h.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		final ItemStack c = new ItemStack(Material.IRON_CHESTPLATE);
		c.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		final ItemStack l = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		l.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		final ItemStack b = new ItemStack(Material.CHAINMAIL_BOOTS);
		b.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		return new ItemStack[] {b, l, c, h};
	}
	
	@Override
	public ItemStack specialItem() {
		final ItemStack item = new ItemStack(Material.POTION, 3, (short) 16428);
		return item;
	}
	
	@Override
	public void onKill(Player killer) {
		killer.getInventory().setItem(1, this.specialItem());
		final ItemStack item = new ItemStack(Material.POTION, 1, (short) 16420);
		killer.getInventory().setItem(2, item);
	}
}
