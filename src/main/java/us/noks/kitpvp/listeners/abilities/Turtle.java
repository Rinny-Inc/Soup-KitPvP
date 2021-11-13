package us.noks.kitpvp.listeners.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import us.noks.kitpvp.Main;
import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.managers.PlayerManager;

public class Turtle implements Listener {
	private Main plugin;

	public Turtle(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onEntityDamagedByPlayer(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();

			if (PlayerManager.get(damager.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.TURTLE)
					&& damager.getPlayer().isSneaking()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.TURTLE)) {
				if (player.getPlayer().isSneaking() && !player.getPlayer().isBlocking()) {
					event.setDamage(2.0D);
				}
				if (player.getPlayer().isSneaking() && player.getPlayer().isBlocking())
					event.setDamage(1.0D);
			}
		}
	}
}
