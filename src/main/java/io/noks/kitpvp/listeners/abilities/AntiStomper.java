package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;

public class AntiStomper extends Abilities {

	public AntiStomper() {
		super("AntiStomper", new ItemStack(Material.BREWING_STAND_ITEM), Rarity.RARE, 0L, new String[] { ChatColor.AQUA + "Cancel Stomper ability" });
	}
}
