package io.noks.kitpvp.task.event;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.EventsType;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Economy.MoneyType;

public class FallenGolemTask implements Listener {
	private static FallenGolemTask instance = new FallenGolemTask();
	public static FallenGolemTask getInstance() {
		return instance;
	}

	private World world = Bukkit.getWorld("world");
	private int countdown = EventsType.GOLEM.getCountdown();
	private String prefix = EventsType.GOLEM.getPrefix(ChatColor.RED);

	private boolean spawned;
	private Location[] possibleLocations = { new Location(this.world, -274.0D, 86.0D, 600.0D) };

	private void setupFallenGolem() {
		Bukkit.broadcastMessage(
				this.prefix + ChatColor.GREEN.toString() + ChatColor.BOLD + "The Fallen Golem just appear!");
		IronGolem golem = (IronGolem) this.world
				.spawn(this.possibleLocations[(new Random()).nextInt(this.possibleLocations.length)], IronGolem.class);
		golem.setCustomName(ChatColor.RED + EventsType.GOLEM.getName());
		golem.setCustomNameVisible(true);
		golem.setMaxHealth(350.0D);
		golem.setHealth(golem.getMaxHealth());
	}

	public void doFallenGolem() {
		this.spawned = false;
		(new BukkitRunnable() {
			public void run() {
				FallenGolemTask.this.countdown = FallenGolemTask.this.countdown - 20;
				int minute = FallenGolemTask.this.countdown / 60 / 20;
				String countdownminute = FallenGolemTask.this.prefix + ChatColor.GRAY
						+ "The Fallen Golem will appear in " + ChatColor.YELLOW + ChatColor.ITALIC + minute
						+ ChatColor.GRAY + " minute";
				String countdownsec = FallenGolemTask.this.prefix + ChatColor.GRAY + "The Fallen Golem will appear in "
						+ ChatColor.YELLOW + ChatColor.ITALIC + (FallenGolemTask.this.countdown / 20) + ChatColor.GRAY
						+ " second";
				if (!Bukkit.getOnlinePlayers().isEmpty()) {
					if (FallenGolemTask.this.countdown == 2400) {
						Bukkit.broadcastMessage(countdownminute + "s");
					}
					if (FallenGolemTask.this.countdown == 1200) {
						Bukkit.broadcastMessage(countdownminute);
					}
					if (FallenGolemTask.this.countdown == 900 || FallenGolemTask.this.countdown == 600
							|| FallenGolemTask.this.countdown == 300 || FallenGolemTask.this.countdown == 200
							|| FallenGolemTask.this.countdown == 100) {
						Bukkit.broadcastMessage(countdownsec + "s");
					}
				}
				if (FallenGolemTask.this.countdown == 0) {
					if (!Bukkit.getOnlinePlayers().isEmpty()) {
						FallenGolemTask.this.spawned = true;
						FallenGolemTask.this.setupFallenGolem();
						cancel();
					}
					if (!FallenGolemTask.this.spawned)
						FallenGolemTask.this.countdown = 8400;
				}
			}
		}).runTaskTimer(Main.getInstance(), 0L, 20L);
	}

	public boolean hasSpawned() {
		return this.spawned;
	}

	@EventHandler
	public void onFallenGolemDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof IronGolem) {
			event.getDrops().clear();
			IronGolem golem = (IronGolem) event.getEntity();

			if (golem.getCustomName().toLowerCase().contains("fallen") && golem.getKiller() != null) {
				this.countdown = 8400;
				final Player killer = golem.getKiller();
				Bukkit.broadcastMessage(this.prefix + ChatColor.RED + "The fallen golem has been killed by " + killer.getName());
				PlayerManager.get(killer.getUniqueId()).getEconomy().add(85, MoneyType.BRONZE);
				// TODO: DROP STUFF
				doFallenGolem();
			}
		}
	}

	@EventHandler
	public void onPlayerAttackGolem(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof IronGolem) {
			Player player = (Player) event.getDamager();
			IronGolem golem = (IronGolem) event.getEntity();

			if (golem.getCustomName().toLowerCase().contains("fallen")) {
				if (!PlayerManager.get(player.getUniqueId()).getAbility().hasAbility()) {
					event.setCancelled(true);
					return;
				}
				if (new Random().nextInt(11) == 10 && golem.getTarget() != player) golem.setTarget(player);
			}
		}
	}
}
