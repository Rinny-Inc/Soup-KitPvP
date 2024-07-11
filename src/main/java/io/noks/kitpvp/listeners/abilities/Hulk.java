package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Hulk extends Abilities implements Listener {
	private Main plugin;
	private Random random = new Random();

	public Hulk(Main main) {
		super("Hulk", new ItemStack(Material.SLIME_BALL), Rarity.UNCOMMON, 3L, new String[] { ChatColor.AQUA + "Launch the player in your hand" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	// TODO: STACKABLE players
	@EventHandler
	public void onCarry(PlayerInteractEntityEvent e) {
		final Player p = e.getPlayer();
		if (e.getRightClicked() instanceof Player) {
			final Player r = (Player) e.getRightClicked();
			if (!PlayerManager.get(r.getUniqueId()).hasAbility()) {
				e.setCancelled(true);
				return;
			}
			final PlayerManager pm = PlayerManager.get(p.getUniqueId());
			if (p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.AIR && pm.hasAbility(this) && !p.isInsideVehicle() && p.getPassenger() == null && r.getPassenger() == null) {
				if (!pm.hasActiveAbilityCooldown()) {
					p.setPassenger(r);
					final String text = ChatColor.GREEN + "You have been picked up by a Hulk! Press SHIFT to try to dismount!";
					if (r.getProtocolVersion() < 47) {
						r.sendMessage(text);
					}
					r.sendActionBar(text);
					pm.applyAbilityCooldown();
					return;
				}
				final double cooldown = pm.getActiveAbilityCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerAttack(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			final Player damager = (Player) e.getDamager();
			if (PlayerManager.get(damager.getUniqueId()).hasAbility(this) && damager.getPassenger() != null) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onThrow(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final Action action = e.getAction();
		if (action == Action.LEFT_CLICK_AIR && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.AIR && PlayerManager.get(p.getUniqueId()).hasAbility(this) && p.getPassenger() != null) {
			final Player passenger = (Player) p.getPassenger();
			passenger.leaveVehicle();
			passenger.setVelocity(p.getEyeLocation().getDirection().multiply(1.2D).setY(0.7D));
		}
	}
	
	@EventHandler
	public void onTryToGetOffHulk(EntityDismountEvent event) {
		if (!(event.getDismounted() instanceof Player)) {
			return;
		}
		final PlayerManager pm = PlayerManager.get(event.getDismounted().getUniqueId());
		if (pm.hasAbility(this)) {
			if (random.nextInt(5) != 3) {
				final String text = ChatColor.RED + "Failed to escape the Hulk! try again..";
				final Player player = pm.getPlayer();
				
				event.setCancelled(true);
				if (player.getProtocolVersion() < 47) {
					player.sendMessage(text);
				}
				player.sendActionBar(text);
			}
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
}
