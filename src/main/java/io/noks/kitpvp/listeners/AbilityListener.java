package io.noks.kitpvp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.managers.PlayerManager;

public class AbilityListener implements Listener {
	
	public AbilityListener(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler
	public void onToggleSneak(PlayerToggleSneakEvent event) {
		PlayerManager manager = PlayerManager.get(event.getPlayer().getUniqueId());
		if (manager.hasAbility()) {
			manager.ability().onToggleSneak(event);
		}
	}
}
