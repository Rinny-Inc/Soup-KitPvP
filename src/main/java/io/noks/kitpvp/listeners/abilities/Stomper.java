package io.noks.kitpvp.listeners.abilities;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.google.common.collect.Lists;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Stomper implements Listener {
	private Main plugin;

	public Stomper(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onStomper(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player stomper = (Player) event.getEntity();
			if (event.getCause() == EntityDamageEvent.DamageCause.FALL
					&& PlayerManager.get(stomper.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.STOMPER)) {
				double damage = event.getDamage();
				List<Player> stomped = Lists.newArrayList();
				for (Entity nearbyPlayers : stomper.getNearbyEntities(5.0D, 3.5D, 5.0D)) {
					if (!(nearbyPlayers instanceof Player))
						continue;
					Player nearby = (Player) nearbyPlayers;
					if (!stomper.canSee(nearby) || !nearby.canSee(stomper))
						continue;
					stomped.add(nearby);
					if (PlayerManager.get(nearby.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.ANTISTOMPER)) {
						stomper.damage(damage, nearby);
						continue;
					}
					if (nearby.isSneaking()) {
						nearby.damage(1.0D, stomper);
						continue;
					}
					nearby.damage(damage, stomper);
				}
				if (stomped.isEmpty()) {
					return;
				}
				stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 0.8F, 1.1F);
				event.setDamage(Math.min(damage, 4.0D));
				stomped.clear();
			}
		}
	}
}
