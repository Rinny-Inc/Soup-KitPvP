package io.noks.kitpvp.listeners.abilities;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class Camel implements Listener {
	private Main plugin;

	public Camel(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onCamelMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Ability ability = PlayerManager.get(player.getUniqueId()).getAbility();
		if (ability.hasAbility(AbilitiesEnum.CAMEL)) {
			if (player.getLocation().getBlock().getBiome() == Biome.DESERT) {
				if (player.getWalkSpeed() != 0.275F) player.setWalkSpeed(0.275F);
				return;
			}
			if (player.getWalkSpeed() != 0.2F)
				player.setWalkSpeed(0.2F);
		}
	}
}
