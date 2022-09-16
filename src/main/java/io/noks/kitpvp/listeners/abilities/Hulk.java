package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class Hulk implements Listener {
	private Main plugin;

	public Hulk(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onHulkCarry(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (e.getRightClicked() instanceof Player) {
			Player r = (Player) e.getRightClicked();
			Ability ability = PlayerManager.get(p.getUniqueId()).getAbility();
			if (p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.AIR
					&& ability.hasAbility(AbilitiesEnum.HULK) && !p.isInsideVehicle() && p.getPassenger() == null
					&& r.getPassenger() == null) {
				if (!ability.hasAbilityCooldown()) {
					p.setPassenger(r);
					r.sendMessage(ChatColor.GREEN + p.getName() + " just picked you up! Press SHIFT to dismount!");
					ability.setAbilityCooldown();
				} else {
					double cooldown = ability.getAbilityCooldown().longValue() / 1000.0D;
					p.sendMessage(ChatColor.RED + "You can use your ability in "
							+ (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
				}
			}
		}
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			if (PlayerManager.get(damager.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.HULK)
					&& damager.getPassenger() != null)
				e.setCancelled(true);

		}
	}

	@EventHandler
	public void onHulkThrow(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action action = e.getAction();
		if (action == Action.LEFT_CLICK_AIR && p.getItemInHand().getType() != null
				&& p.getItemInHand().getType() == Material.AIR
				&& PlayerManager.get(p.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.HULK)
				&& p.getPassenger() != null) {
			Player passenger = (Player) p.getPassenger();
			passenger.leaveVehicle();
			passenger.setVelocity(p.getEyeLocation().getDirection().multiply(1.2D).setY(0.7D));
		}
	}

	@EventHandler
	public void onHulkDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (e.getEntity() instanceof Player && p.getPassenger() != null && p.getPassenger() instanceof Player) {
			Player passenger = (Player) p.getPassenger();
			p.eject();
			passenger.leaveVehicle();
		}
	}

	@EventHandler
	public void onHulkDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player
				&& PlayerManager.get(e.getEntity().getUniqueId()).getAbility().hasAbility(AbilitiesEnum.HULK)) {
			Player p = (Player) e.getDamager();
			Player v = (Player) e.getEntity();
			if (p.getPassenger() == v) {
				p.eject();
				v.leaveVehicle();
			}
		}
	}
}
