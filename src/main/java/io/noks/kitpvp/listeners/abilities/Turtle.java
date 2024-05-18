package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Turtle extends Abilities implements Listener {
	private Main plugin;

	public Turtle(Main main) {
		super("Turtle", new ItemStack(Material.OBSIDIAN), Rarity.UNCOMMON, 0L, new String[] { ChatColor.AQUA + "When you're blocking with your sword,", ChatColor.AQUA + "you only take 0,5 heart" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onEntityDamagedByPlayer(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			final Player damager = (Player) event.getDamager();

			if (PlayerManager.get(damager.getUniqueId()).hasAbility(this) && damager.getPlayer().isSneaking()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();

			if (PlayerManager.get(player.getUniqueId()).hasAbility(this)) {
				if (player.getPlayer().isSneaking() && !player.getPlayer().isBlocking()) {
					event.setDamage(2.0D);
				}
				if (player.getPlayer().isSneaking() && player.getPlayer().isBlocking())
					event.setDamage(1.0D);
			}
		}
	}
}
