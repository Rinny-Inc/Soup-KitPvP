package io.noks.kitpvp.listeners.abilities;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Stomper extends Abilities implements Listener {
	private Main plugin;

	public Stomper(Main main) {
		super("Stomper", new ItemStack(Material.ANVIL), Rarity.UNIQUE, 0L, new String[] { ChatColor.AQUA + "Transfer fall damages to your opponents", ChatColor.AQUA + "while jumping on them" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onStomper(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player stomper = (Player) event.getEntity();
			if (event.getCause() == DamageCause.FALL && !stomper.hasMetadata("Sponged") && PlayerManager.get(stomper.getUniqueId()).getAbility().hasAbility(this)) {
				double damage = event.getDamage();
				stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 0.8F, 1.1F);
				event.setDamage(Math.min(damage, 4.0D));
				final List<Entity> stomped = stomper.getNearbyEntities(5.0D, 3.5D, 5.0D);
				if (stomped.isEmpty()) {
					return;
				}
				for (Entity nearbyPlayers : stomped) {
					if (!(nearbyPlayers instanceof Player)) continue;
					Player nearby = (Player) nearbyPlayers;
					if (!stomper.canSee(nearby) || !nearby.canSee(stomper))continue;
					if (PlayerManager.get(nearby.getUniqueId()).getAbility().get() instanceof AntiStomper) {
						stomper.damage(damage, nearby);
						continue;
					}
					if (nearby.isSneaking()) {
						damage = 1.0D;
					}
					nearby.damage(damage, stomper);
				}
			}
		}
	}
}
