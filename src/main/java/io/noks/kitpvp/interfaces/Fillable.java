package io.noks.kitpvp.interfaces;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public interface Fillable extends MathHelper {
	default void fill(Chest chest) {
		final Random random = new Random();
		final ItemStack lighter = new ItemStack(Material.FLINT_AND_STEEL, 1);
		lighter.setDurability((short) this.getRandom(59, 63));
		final ItemStack[] stack = { 
									new ItemStack(Material.MUSHROOM_SOUP, this.getRandom(1, 6)),
									new ItemStack(Material.LEATHER_BOOTS), 
									new ItemStack(Material.BOW),
									new ItemStack(Material.ARROW, this.getRandom(2, 6)),
									new ItemStack(Material.BROWN_MUSHROOM, this.getRandom(2, 9)),
									new ItemStack(Material.LEATHER_LEGGINGS), 
									new ItemStack(Material.LEATHER_CHESTPLATE),
									new ItemStack(Material.RED_MUSHROOM, this.getRandom(2, 9)),
									new ItemStack(Material.LEATHER_HELMET),
									new ItemStack(Material.BOWL, this.getRandom(3, 9)),
									new ItemStack(Material.GOLDEN_APPLE, this.getRandom(1, 2)),
									new ItemStack(Material.POTION, 1, (short) 16386), 
									new ItemStack(Material.GOLD_HELMET),
									lighter, 
									new ItemStack(Material.EXP_BOTTLE, this.getRandom(1, 3)) 
								  };
		
		for (int i = 0; i < (new Random()).nextInt(5) + 1 + 3; i++) {
			chest.getInventory().setItem((new Random()).nextInt(chest.getInventory().getSize()), new ItemStack(stack[random.nextInt(stack.length)]));
		}
	}
}
