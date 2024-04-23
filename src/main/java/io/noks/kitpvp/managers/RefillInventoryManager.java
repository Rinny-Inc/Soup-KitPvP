package io.noks.kitpvp.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.inventory.Inventory;

import io.noks.custom.Hologram;

public class RefillInventoryManager {
	public static final List<RefillInventoryManager> inventories = new ArrayList<RefillInventoryManager>();
	private Inventory inventory;
	private boolean filled;
	private Biome biome;
	private Map<Biome, Long> cooldown;
	private Hologram hologram;

	public RefillInventoryManager(Inventory inv, Location location) {
		this.cooldown = new HashMap<Biome, Long>();
		this.inventory = inv;
		this.filled = false;
		this.biome = location.getBlock().getBiome();
		this.hologram = Bukkit.getServer().newHologram(location, null);
	}
	
	// countdown
	// double cooldown = im.getCooldown().longValue() / 1000.0D;
	// player.sendMessage(ChatColor.RED + "Refill ends in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");

	public static RefillInventoryManager get(Inventory inv, Location location) {
		final Biome biome = location.getBlock().getBiome();
		for (RefillInventoryManager im : inventories) {
			if (im.getBiome().equals(biome)) {
				return im;
			}
		}
		final RefillInventoryManager im = new RefillInventoryManager(inv, location);
		inventories.add(im);
		return im;
	}

	public void remove() {
		inventories.remove(this);
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public boolean isFilled() {
		return this.filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	public Biome getBiome() {
		return this.biome;
	}

	public void setBiome(Biome biome) {
		this.biome = biome;
	}

	public Long getCooldown() {
		if (this.cooldown.containsKey(this.biome))
			return Long.valueOf(Math.max(0L, ((Long) this.cooldown.get(this.biome)).longValue() - System.currentTimeMillis()));
		return Long.valueOf(0L);
	}

	public void setCooldown(Long cooldown) {
		this.cooldown.put(this.biome, Long.valueOf(System.currentTimeMillis() + cooldown.longValue() * 1000L));
	}

	public boolean hasCooldown() {
		if (!this.cooldown.containsKey(this.biome))
			return false;
		return (((Long) this.cooldown.get(this.biome)).longValue() > System.currentTimeMillis());
	}
}
