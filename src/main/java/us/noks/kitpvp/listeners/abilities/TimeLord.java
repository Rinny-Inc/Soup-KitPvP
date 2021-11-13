package us.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import us.noks.kitpvp.Main;
import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.managers.PlayerManager;

public class TimeLord implements Listener {
	private Main plugin;
	private List<UUID> freeze;

	public TimeLord(Main main) {
		this.freeze = Lists.newArrayList();

		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onTimeStop(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Action action = e.getAction();
		PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (action == Action.RIGHT_CLICK_AIR && player.getItemInHand().getType() != null
				&& player.getItemInHand().getType() == Material.WATCH
				&& pm.getAbility().hasAbility(AbilitiesEnum.TIMELORD)) {
			if (!pm.getAbility().hasAbilityCooldown()) {
				pm.getAbility().setAbilityCooldown();
				player.sendMessage(ChatColor.GREEN + "You just stopped the time around you!");
				for (Entity nearbyEntity : player.getNearbyEntities(15.0D, 2.0D, 15.0D)) {
					if (nearbyEntity instanceof Player) {
						final Player nearby = (Player) nearbyEntity;
						this.freeze.add(nearby.getUniqueId());
						nearby.sendMessage(ChatColor.RED + "The time was stopped by a TimeLord!");
						nearby.playSound(nearby.getLocation(), Sound.PORTAL_TRAVEL, 0.8F, 1.0F);
						(new BukkitRunnable() {
							public void run() {
								if (TimeLord.this.freeze.contains(nearby.getUniqueId())) {
									TimeLord.this.freeze.remove(nearby.getUniqueId());
									nearby.sendMessage(ChatColor.GREEN + "The time is back to the normal");
								}
							}
						}).runTaskLaterAsynchronously(this.plugin, 200L);
					}
				}
			} else {
				double cooldown = pm.getAbility().getAbilityCooldown().longValue() / 1000.0D;
				player.sendMessage(ChatColor.RED + "You can use your ability in "
						+ (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
			}
		}
	}

	@EventHandler
	public void onTimeLordDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (this.freeze.contains(player.getUniqueId())) {
				this.freeze.remove(player.getUniqueId());
				player.sendMessage(ChatColor.GREEN + "The time is back to the normal");
			}
		}
	}

	@EventHandler
	public void onTimelordMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		if (this.freeze.contains(p.getUniqueId()))
			p.teleport(loc);
	}
}
