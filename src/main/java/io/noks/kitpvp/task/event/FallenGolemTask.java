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

import io.noks.kitpvp.abstracts.AbstractBossTask;
import io.noks.kitpvp.enums.EventsType;
import io.noks.kitpvp.managers.PlayerManager;

public class FallenGolemTask extends AbstractBossTask implements Listener {
	private World world = Bukkit.getWorld("world");
	private int countdown = EventsType.GOLEM.getCountdown();
	private String prefix = EventsType.GOLEM.getPrefix(ChatColor.RED);

	private Location[] possibleLocations = { new Location(this.world, -274.0D, 86.0D, 600.0D) };
	private Random random = new Random();
	
	@Override
	public void doTask() {
		this.countdown = this.countdown - 20;
		int minute = this.countdown / 60 / 20;
		String countdownminute = this.prefix + ChatColor.GRAY + "The Fallen Golem will appear in " + ChatColor.YELLOW + ChatColor.ITALIC + minute + ChatColor.GRAY + " minute";
		String countdownsec = this.prefix + ChatColor.GRAY + "The Fallen Golem will appear in " + ChatColor.YELLOW + ChatColor.ITALIC + (this.countdown / 20) + ChatColor.GRAY + " second";
		if (!Bukkit.getOnlinePlayers().isEmpty()) {
			switch (this.countdown) {
				case 2400: {
					Bukkit.broadcastMessage(countdownminute + "s");
					break;
				}
				case 1200: {
					Bukkit.broadcastMessage(countdownminute);
					break;
				}
				case 900, 
					 600, 
					 300, 
					 200, 
					 100: {
					Bukkit.broadcastMessage(countdownsec + "s");
					break;
				}
			}
		}
		if (this.countdown == 0) {
			if (!Bukkit.getOnlinePlayers().isEmpty()) {
				this.spawned = true;
				this.spawn();
			}
		}
	}
	
	@Override
	public void spawn() {
		Bukkit.broadcastMessage(this.prefix + ChatColor.GREEN.toString() + ChatColor.BOLD + "The Fallen Golem just appear!");
		final IronGolem golem = (IronGolem) this.world.spawn(this.possibleLocations[random.nextInt(this.possibleLocations.length)], IronGolem.class);
		golem.setCustomName(ChatColor.RED + EventsType.GOLEM.getName());
		golem.setCustomNameVisible(true);
		golem.setMaxHealth(350.0D);
		golem.setHealth(golem.getMaxHealth());
	}

	@EventHandler
	public void onFallenGolemDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof IronGolem) {
			event.getDrops().clear();
			final IronGolem golem = (IronGolem) event.getEntity();

			if (golem.getCustomName().toLowerCase().contains("fallen") && golem.getKiller() != null) {
				final Player killer = golem.getKiller();
				Bukkit.broadcastMessage(this.prefix + ChatColor.RED + "The fallen golem has been killed by " + killer.getName());
				PlayerManager.get(killer.getUniqueId()).getEconomy().add(85);
				this.stop();
				// TODO: DROP STUFF
			}
		}
	}
	
	@EventHandler
	public void onPlayerAttackGolem(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof IronGolem && event.getDamager() instanceof Player) {
			final Player player = (Player) event.getDamager();
			final IronGolem golem = (IronGolem) event.getEntity();

			if (golem.getCustomName().toLowerCase().contains("fallen")) {
				if (!PlayerManager.get(player.getUniqueId()).hasAbility()) {
					event.setCancelled(true);
					return;
				}
				if (random.nextInt(11) == 10 && golem.getTarget() != player) golem.setTarget(player);
			}
		}
	}
}
