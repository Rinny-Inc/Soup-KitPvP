package io.noks.kitpvp.listeners.abilities;

import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Magma implements Listener {
	private Main plugin;

	public Magma(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof org.bukkit.entity.Player
				&& event.getDamager() instanceof org.bukkit.entity.Player
				&& PlayerManager.get(event.getDamager().getUniqueId()).getAbility().hasAbility(AbilitiesEnum.MAGMA)) {
			double rand = Math.random() * 100.0D;
			if (rand < 40.0D) {
				Biome biome = event.getDamager().getLocation().getBlock().getBiome();
				if (PlayerManager.get(event.getEntity().getUniqueId()).getAbility().hasAbility(AbilitiesEnum.CONTRE)) {
					event.getDamager().setFireTicks(((biome == Biome.HELL) ? 10 : 20) * 4);
					return;
				}
				event.getEntity().setFireTicks(((biome == Biome.HELL) ? 20 : 10) * 4);
			}
		}
	}
}
