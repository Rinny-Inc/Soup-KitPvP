package io.noks.kitpvp.managers;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.avaje.ebean.validation.NotNull;

import io.noks.kitpvp.Main;

public class RefillInventoryManager {
	public static final Set<RefillInventoryManager> inventories = new HashSet<RefillInventoryManager>();
	public static @Nullable BukkitTask cooldownTask;
	private @Nullable Inventory inventory;
	private long cooldown;
	private @NotNull final Location location;
	private @NotNull Block wool;

	public RefillInventoryManager(Location location) {
		this.cooldown = 0l;
		this.location = location;
		this.wool = location.getBlock().getRelative(BlockFace.UP);
		setFilled(true);
		inventories.add(this);
	}

	public static RefillInventoryManager get(Location location) {
		for (RefillInventoryManager im : inventories) {
			if (im.getLocation().equals(location)) {
				return im;
			}
		}
		return new RefillInventoryManager(location);
	}

	public void remove() {
		inventories.remove(this);
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public boolean isFilled() {
		return this.inventory != null;
	}

	public void setFilled(boolean filled) {
		if (filled) {
			if (this.inventory == null) {
				this.inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Refill Chest");
			}
			final ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
			while (this.inventory.firstEmpty() != -1) {
				this.inventory.addItem(soup);
			}
		}
	}
	
	public Location getLocation() {
		return this.location;
	}

	public Long getCooldown() {
		return Long.valueOf(Math.max(0L, this.cooldown - System.currentTimeMillis()));
	}

	public void setCooldown(Long cooldown) {
		if (cooldown != 0L && cooldownTask == null) {
			this.startTask();
		}
		this.cooldown = System.currentTimeMillis() + (cooldown * 1000L);
		this.inventory = null;
		this.wool.setTypeIdAndData(35, (byte)14, false);
	}

	public boolean hasCooldown() {
		return this.cooldown > System.currentTimeMillis();
	}
	
	private void startTask() {
		cooldownTask = new BukkitRunnable() {
			private final int inventoriesSize = inventories.size();
			
			@Override
			public void run() {
				int inventoryNotCooldown = 0;
				for (RefillInventoryManager invs : inventories) {
					if (!invs.hasCooldown()) {
						inventoryNotCooldown++;
						if (wool.getData() != (byte) 5) {
							wool.setTypeIdAndData(35, (byte)5, false);
							invs.setFilled(true);
						}
						continue;
					}
					if (wool.getData() != (byte) 14) {
						wool.setTypeIdAndData(35, (byte)14, false);
					}
				}
				if (inventoryNotCooldown == this.inventoriesSize) {
					this.cancel();
					cooldownTask = null;
				}
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
	}
}
