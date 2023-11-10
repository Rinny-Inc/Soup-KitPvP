package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Fireman extends Abilities implements Listener {
	private Main plugin;

	public Fireman(Main main) {
		super("Fireman", new ItemStack(Material.LAVA_BUCKET), Rarity.UNCOMMON, 0L, new String[] { ChatColor.AQUA + "Fire resistant" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onFireman(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(this)) {
				switch (event.getCause()) {
				case FIRE:
					event.setCancelled(true);
					break;
				case FIRE_TICK:
					event.setCancelled(true);
					break;
				case LIGHTNING:
					event.setCancelled(true);
					break;
				case LAVA:
					event.setCancelled(true);
					break;
				default:
					break;
				}
			}
		}
	}
}
