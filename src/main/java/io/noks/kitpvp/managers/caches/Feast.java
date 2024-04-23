package io.noks.kitpvp.managers.caches;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;

public class Feast {
	private World world = Bukkit.getWorld("world");
	private Map<Location, Material> blocks = new HashMap<Location, Material>(13);
	
	public Feast(Location location) {
		this.setupFeast(location);
	}
	
	private void setupFeast(Location enchantmentTableLocation) {
		blocks.put(enchantmentTableLocation, this.world.getBlockAt(enchantmentTableLocation).getType());
		this.world.getBlockAt(enchantmentTableLocation).setType(Material.ENCHANTMENT_TABLE);
		
		final double[][] offsets = {
	            {-2.0, 0.0},
	            {-2.0, -2.0},
	            {-1.0, -1.0},
	            {0.0, -2.0},
	            {2.0, -2.0},
	            {1.0, -1.0},
	            {2.0, 0.0},
	            {1.0, 1.0},
	            {2.0, 2.0},
	            {0.0, 2.0},
	            {-1.0, 1.0},
	            {-2.0, 2.0}
	        };

		for (double[] offset : offsets) {
	        double offsetX = offset[0];
	        double offsetZ = offset[1];
	        Location chestLocation = enchantmentTableLocation.clone().add(offsetX, 0, offsetZ);
	        blocks.put(chestLocation, this.world.getBlockAt(chestLocation).getType());
	        this.world.getBlockAt(chestLocation).setType(Material.CHEST);
			Chest chest = (Chest) this.world.getBlockAt(chestLocation).getState();
			fillFeast(chest);
	    }
		final DecimalFormat format = new DecimalFormat("#.##");
		Bukkit.broadcastMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "The feast just appear! " + ChatColor.GREEN + "(" + format.format(enchantmentTableLocation.getX()) + ", " + enchantmentTableLocation.getY() + ", " + format.format(enchantmentTableLocation.getZ()) + ")");
	}

	private void fillFeast(Chest chest) {
		Random random = new Random();

		ItemStack lighter = new ItemStack(Material.FLINT_AND_STEEL, 1);
		lighter.setDurability((short) Main.getInstance().getMathUtils().getRandom(59, 63));
		ItemStack[] items = { new ItemStack(Material.MUSHROOM_SOUP, Main.getInstance().getMathUtils().getRandom(1, 6)),
				new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.BOW),
				new ItemStack(Material.ARROW, Main.getInstance().getMathUtils().getRandom(2, 6)),
				new ItemStack(Material.BROWN_MUSHROOM, Main.getInstance().getMathUtils().getRandom(2, 9)),
				new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE),
				new ItemStack(Material.RED_MUSHROOM, Main.getInstance().getMathUtils().getRandom(2, 9)),
				new ItemStack(Material.LEATHER_HELMET),
				new ItemStack(Material.BOWL, Main.getInstance().getMathUtils().getRandom(3, 9)),
				new ItemStack(Material.GOLDEN_APPLE, Main.getInstance().getMathUtils().getRandom(1, 2)),
				new ItemStack(Material.POTION, 1, (short) 16386), new ItemStack(Material.GOLD_HELMET),
				lighter, new ItemStack(Material.EXP_BOTTLE, Main.getInstance().getMathUtils().getRandom(1, 3)) };

		for (int i = 0; i < (new Random()).nextInt(5) + 1 + 3; i++) {
			chest.getInventory().setItem((new Random()).nextInt(chest.getInventory().getSize()), new ItemStack(items[random.nextInt(items.length)]));
		}
	}
	
	public void clearFeast() {
		for (Entry<Location, Material> entry : this.blocks.entrySet()) {
			Location loc = entry.getKey();
			Material oldMaterial = entry.getValue();
			this.world.getBlockAt(loc).setType(oldMaterial);
		}
		Bukkit.broadcastMessage(ChatColor.RED + "The feast just disappeared!");
	}
}
