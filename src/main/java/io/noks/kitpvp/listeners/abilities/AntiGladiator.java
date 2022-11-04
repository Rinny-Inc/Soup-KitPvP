package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;

public class AntiGladiator extends Abilities {

	public AntiGladiator() {
		super("AntiGladiator", new ItemStack(Material.LADDER), Rarity.UNIQUE, 0L, new String[] { ChatColor.AQUA + "Cancel Gladiator ability" });
	}
}
