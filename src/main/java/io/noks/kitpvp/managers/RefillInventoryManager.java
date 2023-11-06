package io.noks.kitpvp.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Biome;
import org.bukkit.inventory.Inventory;

public class RefillInventoryManager {
	public static final List<RefillInventoryManager> inventories = new ArrayList<RefillInventoryManager>();
	private Inventory inventory;
	private boolean filled;
	private Biome biome;
	private Map<Biome, Long> cooldown;

	public RefillInventoryManager(Inventory inv) {
		this.cooldown = new HashMap<Biome, Long>();

		this.inventory = inv;
		this.filled = false;
		this.biome = null;
	}

	public RefillInventoryManager(Inventory inv, Biome biome) {
		this.cooldown = new HashMap<Biome, Long>();
		this.inventory = inv;
		this.filled = false;
		this.biome = biome;
	}

	public static RefillInventoryManager get(Inventory inv, Biome biome) {
		for (RefillInventoryManager im : inventories) {
			if (im.getBiome().equals(biome)) {
				return im;
			}
		}
		final RefillInventoryManager im = new RefillInventoryManager(inv, biome);
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
