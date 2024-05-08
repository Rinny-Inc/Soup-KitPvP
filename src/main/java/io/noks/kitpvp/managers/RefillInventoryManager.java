package io.noks.kitpvp.managers;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.avaje.ebean.validation.NotNull;

import io.noks.Hologram;
import io.noks.kitpvp.Main;

public class RefillInventoryManager {
	public static final Set<RefillInventoryManager> inventories = new HashSet<RefillInventoryManager>();
	public static @Nullable BukkitTask cooldownTask;
	private Inventory inventory;
	private boolean filled;
	private long cooldown;
	private @NotNull final Location location;
	private @NotNull Hologram hologram;

	public RefillInventoryManager(Location location) {
		this.cooldown = 0l;
		this.location = location;
		setFilled(true);
		this.hologram = Bukkit.getServer().newHologram(location.clone().add(0.5, 1.5, 0.5), ChatColor.GREEN.toString() + ChatColor.BOLD + "Free Soup");
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
		return this.filled;
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
		this.filled = filled;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public String getHologramMessage() {
		return this.hologram.getMessage();
	}
	
	public void updateHologramMessage(String msg) {
		this.hologram.setMessage(msg);
	}

	public Long getCooldown() {
		return Long.valueOf(Math.max(0L, this.cooldown - System.currentTimeMillis()));
	}

	public void setCooldown(Long cooldown) {
		if (cooldown != 0L && cooldownTask == null) {
			//this.startTask();
		}
		this.cooldown = System.currentTimeMillis() + (cooldown * 1000L);
		this.filled = false;
	}

	public boolean hasCooldown() {
		return this.cooldown > System.currentTimeMillis();
	}
	
	private void startTask() {
		cooldownTask = new BukkitRunnable() {
			private final DecimalFormat format = new DecimalFormat("#.#");
			private final int inventoriesSize = inventories.size();
			
			@Override
			public void run() {
				int inventoryNotCooldown = 0;
				for (RefillInventoryManager invs : inventories) {
					if (!invs.hasCooldown()) {
						inventoryNotCooldown++;
						if (!invs.getHologramMessage().contains("free")) {
							invs.updateHologramMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Free Soup");
							invs.setFilled(true);
						}
						continue;
					}
					double cooldown = invs.getCooldown().longValue() / 1000.0D;
					invs.updateHologramMessage(ChatColor.RED + format.format(cooldown) + "s");
				}
				if (inventoryNotCooldown == this.inventoriesSize) {
					this.cancel();
					cooldownTask = null;
				}
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
}
