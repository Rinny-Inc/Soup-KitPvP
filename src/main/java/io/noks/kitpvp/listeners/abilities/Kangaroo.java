package io.noks.kitpvp.listeners.abilities;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Kangaroo implements Listener {
	private Main plugin;

	public Kangaroo(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onKangarooInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (player.getItemInHand().getType() != null && player.getItemInHand().getType() == Material.FIREWORK && PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.KANGAROO)) {
			event.setCancelled(true);
			if (!player.hasMetadata("kangaroo")) {
				boolean sneak = player.isSneaking();
				float multiplier = !sneak ? 0.6F : 1.5F;
				float vertical = !sneak ? 0.8F : 0.5F;
				Vector kangaVel = player.getEyeLocation().getDirection();
				player.setVelocity(kangaVel.multiply(multiplier).setY(vertical));
				player.setFallDistance(0.0F);
				player.setMetadata("kangaroo", new FixedMetadataValue(this.plugin, Boolean.valueOf(true)));
			}
		}
	}

	@EventHandler
	public void onKangarooGround(PlayerOnGroundEvent event) {
		if (!event.getOnGround()) {
			return;
		}
		Player player = event.getPlayer();
		if (player.hasMetadata("kangaroo")) {
			player.removeMetadata("kangaroo", this.plugin);
		}
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if (player.hasMetadata("kangaroo")) {
			player.removeMetadata("kangaroo", this.plugin);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = event.getEntity();

			if (player.hasMetadata("kangaroo")) {
				player.removeMetadata("kangaroo", this.plugin);
			}
		}
	}
}
