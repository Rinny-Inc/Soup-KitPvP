package io.noks.kitpvp.managers.caches;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;

import io.noks.kitpvp.interfaces.Fillable;

public class Feast implements Fillable {
	private World world = Bukkit.getWorld("world");
	private Map<Location, Material> blocks;
	
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

		if (this.blocks == null) {
			this.blocks = new HashMap<Location, Material>(13);
		}
		for (double[] offset : offsets) {
	        double offsetX = offset[0];
	        double offsetZ = offset[1];
	        Location chestLocation = enchantmentTableLocation.clone().add(offsetX, 0, offsetZ);
	        blocks.put(chestLocation, this.world.getBlockAt(chestLocation).getType());
	        this.world.getBlockAt(chestLocation).setType(Material.CHEST);
			Chest chest = (Chest) this.world.getBlockAt(chestLocation).getState();
			this.fill(chest);
	    }
		final DecimalFormat format = new DecimalFormat("#.##");
		Bukkit.broadcastMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "The feast just appear! " + ChatColor.GREEN + "(" + format.format(enchantmentTableLocation.getX()) + ", " + enchantmentTableLocation.getY() + ", " + format.format(enchantmentTableLocation.getZ()) + ")");
	}
	
	public void clearFeast() {
		for (Entry<Location, Material> entry : this.blocks.entrySet()) {
			Location loc = entry.getKey();
			Material oldMaterial = entry.getValue();
			this.world.getBlockAt(loc).setType(oldMaterial);
		}
		this.blocks.clear();
		this.blocks = null;
		Bukkit.broadcastMessage(ChatColor.RED + "The feast just disappeared!");
	}
}
