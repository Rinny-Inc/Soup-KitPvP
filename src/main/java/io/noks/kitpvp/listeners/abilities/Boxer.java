package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;

public class Boxer extends Abilities {

	public Boxer() {
		super("Boxer", new ItemStack(Material.WOOD_SWORD), Rarity.COMMON, 0L, new String[] { ChatColor.AQUA + "Your hands are doing more damage" });
	}
	
	public Material sword() {
		return Material.WOOD_SWORD;
	}
}
