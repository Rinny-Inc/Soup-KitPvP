package io.noks.kitpvp.listeners.abilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;

public class Jesus extends Abilities {
	
	public Jesus() {
		super("Jesus", new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), Rarity.BETA, 0L, new String[] {"Walk on Lava and Water"});
	}
}
