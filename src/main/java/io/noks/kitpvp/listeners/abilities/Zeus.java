package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Zeus extends Abilities implements Listener {
	private Main plugin;

	public Zeus(Main main) {
		super("Zeus", new ItemStack(Material.WOOD_AXE), Rarity.RARE, 15L, new String[] { ChatColor.AQUA + "Invoke the thunder" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return this.plugin.getItemUnbreakable(this.getIcon().getType());
	}
	
	@Override
	public String specialItemName() {
		return "Zeus Axe";
	}

	@EventHandler
	public void onZeus(PlayerInteractEvent e) {
		if (!e.hasItem()) {
			return;
		}
		final Player p = e.getPlayer();
		final Action action = e.getAction();
		final PlayerManager pm = PlayerManager.get(p.getUniqueId());
		if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.WOOD_AXE && pm.hasAbility(this)) {
			e.setCancelled(true);
			if (!pm.hasActiveAbilityCooldown()) {
				pm.applyAbilityCooldown();
				p.getWorld().strikeLightning(p.getLocation().add(3.0D, 0.0D, 0.0D));
				p.getWorld().strikeLightning(p.getLocation().add(-3.0D, 0.0D, 0.0D));
				p.getWorld().strikeLightning(p.getLocation().add(0.0D, 0.0D, 3.0D));
				p.getWorld().strikeLightning(p.getLocation().add(0.0D, 0.0D, -3.0D));
				return;
			}
			final double cooldown = pm.getActiveAbilityCooldown().longValue() / 1000.0D;
			p.sendMessage(ChatColor.RED + "You can use your ability in " + df.format(cooldown) + " seconds.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onZeusDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = (Player) e.getEntity();
			if (PlayerManager.get(p.getUniqueId()).hasAbility(this) && e.getCause() == DamageCause.LIGHTNING) {
				e.setCancelled(true);
			}
		}
	}
}
