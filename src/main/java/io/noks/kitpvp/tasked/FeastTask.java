package io.noks.kitpvp.tasked;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.EventsType;
import io.noks.kitpvp.utils.MathUtils;

public class FeastTask {
	private static FeastTask instance = new FeastTask();

	public static FeastTask getInstance() {
		return instance;
	}

	private World world = Bukkit.getWorld("world");
	private int countdown = EventsType.FEAST.getCountdown();
	private String prefix = EventsType.FEAST.getPrefix(ChatColor.GOLD);

	private boolean spawned;
	private Location enchantmentTableLocation = EventsType.FEAST.getPosition();
	private Location[] chestsLocation = { this.enchantmentTableLocation.clone().add(-2.0D, 0.0D, 0.0D),
			this.enchantmentTableLocation.clone().add(-2.0D, 0.0D, -2.0D),
			this.enchantmentTableLocation.clone().add(-1.0D, 0.0D, -1.0D),
			this.enchantmentTableLocation.clone().add(0.0D, 0.0D, -2.0D),
			this.enchantmentTableLocation.clone().add(2.0D, 0.0D, -2.0D),
			this.enchantmentTableLocation.clone().add(1.0D, 0.0D, -1.0D),
			this.enchantmentTableLocation.clone().add(2.0D, 0.0D, 0.0D),
			this.enchantmentTableLocation.clone().add(1.0D, 0.0D, 1.0D),
			this.enchantmentTableLocation.clone().add(2.0D, 0.0D, 2.0D),
			this.enchantmentTableLocation.clone().add(0.0D, 0.0D, 2.0D),
			this.enchantmentTableLocation.clone().add(-1.0D, 0.0D, 1.0D),
			this.enchantmentTableLocation.clone().add(-2.0D, 0.0D, 2.0D) };

	private void setupFeast() {
		Bukkit.broadcastMessage(this.prefix + ChatColor.GREEN.toString() + ChatColor.BOLD + "The feast just appear! "
				+ ChatColor.GREEN + "(" + this.enchantmentTableLocation.getX() + ", "
				+ this.enchantmentTableLocation.getY() + ", " + this.enchantmentTableLocation.getZ() + ")");
		this.world.getBlockAt(this.enchantmentTableLocation).setType(Material.ENCHANTMENT_TABLE);
		for (Location location : this.chestsLocation) {
			this.world.getBlockAt(location).setType(Material.CHEST);
			Chest chest = (Chest) this.world.getBlockAt(location).getState();
			fillFeast(chest);
		}

		(new BukkitRunnable() {
			public void run() {
				FeastTask.this.countdown = 8400;
				Bukkit.broadcastMessage(FeastTask.this.prefix + ChatColor.RED + "The feast just disappear!");
				FeastTask.this.doFeast();
			}
		}).runTaskLater(Main.getInstance(), 2400L);
	}

	private void fillFeast(Chest chest) {
		Random random = new Random();

		ItemStack lighter = new ItemStack(Material.FLINT_AND_STEEL, 1);
		lighter.setDurability((short) MathUtils.getRandom(59, 63));
		ItemStack[] items = { new ItemStack(Material.MUSHROOM_SOUP, MathUtils.getRandom(1, 6)),
				new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.BOW),
				new ItemStack(Material.ARROW, MathUtils.getRandom(2, 6)),
				new ItemStack(Material.BROWN_MUSHROOM, MathUtils.getRandom(2, 9)),
				new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE),
				new ItemStack(Material.RED_MUSHROOM, MathUtils.getRandom(2, 9)),
				new ItemStack(Material.LEATHER_HELMET),
				new ItemStack(Material.BOWL, MathUtils.getRandom(3, 9)),
				new ItemStack(Material.GOLDEN_APPLE, MathUtils.getRandom(1, 2)),
				new ItemStack(Material.POTION, 1, (short) 16386), new ItemStack(Material.GOLD_HELMET),
				lighter, new ItemStack(Material.EXP_BOTTLE, MathUtils.getRandom(1, 3)) };

		for (int i = 0; i < (new Random()).nextInt(5) + 1 + 3; i++) {
			chest.getInventory().setItem((new Random()).nextInt(chest.getInventory().getSize()),
					new ItemStack(items[random.nextInt(items.length)]));
		}
	}

	public void doFeast() {
		this.spawned = false;
		if (this.world.getBlockAt(this.enchantmentTableLocation).getType() != Material.AIR) {
			this.world.getBlockAt(this.enchantmentTableLocation).setType(Material.AIR);
		}
		for (Location chestsLocations : this.chestsLocation) {
			if (this.world.getBlockAt(chestsLocations).getType() != Material.AIR) {
				this.world.getBlockAt(chestsLocations).setType(Material.AIR);
				this.world.getEntities().clear();
			}
		}
		(new BukkitRunnable() {
			public void run() {
				FeastTask.this.countdown = FeastTask.this.countdown - 20;
				int minute = FeastTask.this.countdown / 60 / 20;
				String countdownminute = FeastTask.this.prefix + ChatColor.GRAY + "The feast will appear in "
						+ ChatColor.YELLOW + ChatColor.ITALIC + minute + ChatColor.GRAY + " minute";
				String countdownsec = FeastTask.this.prefix + ChatColor.GRAY + "The feast will appear in "
						+ ChatColor.YELLOW + ChatColor.ITALIC + (FeastTask.this.countdown / 20) + ChatColor.GRAY
						+ " second";
				if (!Bukkit.getOnlinePlayers().isEmpty()) {
					if (FeastTask.this.countdown == 2400) {
						Bukkit.broadcastMessage(countdownminute + "s");
					}
					if (FeastTask.this.countdown == 1200) {
						Bukkit.broadcastMessage(countdownminute);
					}
					if (FeastTask.this.countdown == 900 || FeastTask.this.countdown == 600
							|| FeastTask.this.countdown == 300 || FeastTask.this.countdown == 200
							|| FeastTask.this.countdown == 100) {
						Bukkit.broadcastMessage(countdownsec + "s");
					}
				}
				if (FeastTask.this.countdown == 0) {
					if (!Bukkit.getOnlinePlayers().isEmpty()) {
						FeastTask.this.spawned = true;
						FeastTask.this.setupFeast();
						cancel();
					}
					if (!FeastTask.this.spawned)
						FeastTask.this.countdown = 8400;
				}
			}
		}).runTaskTimer(Main.getInstance(), 0L, 20L);
	}

	public boolean hasSpawned() {
		return this.spawned;
	}
}
