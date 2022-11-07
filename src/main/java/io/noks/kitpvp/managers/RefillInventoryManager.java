package io.noks.kitpvp.managers;

import java.util.List;
import java.util.Map;

import org.bukkit.block.Biome;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RefillInventoryManager {
	private Inventory inventory;
	private boolean filled;
	private Biome biome;
	public static final List<RefillInventoryManager> inventories = Lists.newArrayList();
	private Map<Biome, Long> cooldown;

	public RefillInventoryManager(Inventory inv) {
		this.cooldown = Maps.newConcurrentMap();

		this.inventory = inv;
		this.filled = false;
		this.biome = null;
	}

	public RefillInventoryManager(Inventory inv, Biome biome) {
		this.cooldown = Maps.newConcurrentMap();
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
		RefillInventoryManager im = new RefillInventoryManager(inv, biome);
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
