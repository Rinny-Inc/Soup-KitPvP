package io.noks.kitpvp.listeners.abilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;

public class PvP extends Abilities {
	public PvP() {
		super("PvP", new ItemStack(Material.DIAMOND_SWORD), Rarity.COMMON, 0L, new String[] { "Default pvp kit" });
	}
}
