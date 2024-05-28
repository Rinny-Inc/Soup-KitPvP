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
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Hulk extends Abilities implements Listener {
	private Main plugin;

	public Hulk(Main main) {
		super("Hulk", new ItemStack(Material.SLIME_BALL), Rarity.UNCOMMON, 3L, new String[] { ChatColor.AQUA + "Launch the player in your hand" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onCarry(PlayerInteractEntityEvent e) {
		final Player p = e.getPlayer();
		if (e.getRightClicked() instanceof Player) {
			final Player r = (Player) e.getRightClicked();
			final PlayerManager pm = PlayerManager.get(p.getUniqueId());
			if (p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.AIR && pm.hasAbility(this) && !p.isInsideVehicle() && p.getPassenger() == null && r.getPassenger() == null) {
				if (!pm.hasActiveAbilityCooldown()) {
					p.setPassenger(r);
					r.sendMessage(ChatColor.GREEN + p.getName() + " just picked you up! Press SHIFT to dismount!");
					pm.applyAbilityCooldown();
					return;
				}
				final double cooldown = pm.getActiveAbilityCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
			}
		}
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			final Player damager = (Player) e.getDamager();
			if (PlayerManager.get(damager.getUniqueId()).hasAbility(this) && damager.getPassenger() != null) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onThrow(PlayerInteractEvent e) {
		if (!e.hasItem()) {
			return;
		}
		final Player p = e.getPlayer();
		final Action action = e.getAction();
		if (action == Action.LEFT_CLICK_AIR && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.AIR && PlayerManager.get(p.getUniqueId()).hasAbility(this) && p.getPassenger() != null) {
			final Player passenger = (Player) p.getPassenger();
			passenger.leaveVehicle();
			passenger.setVelocity(p.getEyeLocation().getDirection().multiply(1.2D).setY(0.7D));
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		final Player p = e.getEntity();
		if (e.getEntity() instanceof Player && p.getPassenger() != null && p.getPassenger() instanceof Player) {
			final Player passenger = (Player) p.getPassenger();
			p.eject();
			passenger.leaveVehicle();
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player && PlayerManager.get(e.getEntity().getUniqueId()).hasAbility(this)) {
			final Player p = (Player) e.getDamager();
			final Player v = (Player) e.getEntity();
			if (p.getPassenger() == v) {
				p.eject();
				v.leaveVehicle();
			}
		}
	}
}
