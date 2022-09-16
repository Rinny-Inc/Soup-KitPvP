package io.noks.kitpvp.listeners.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Fireman implements Listener {
	private Main plugin;

	public Fireman(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onFireman(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.FIREMAN)) {
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
