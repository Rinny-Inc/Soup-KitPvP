package us.noks.kitpvp.listeners.abilities;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import com.google.common.collect.Maps;

import us.noks.kitpvp.Main;
import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.managers.PlayerManager;

public class Kangaroo implements Listener {
	private Main plugin;
	private Map<UUID, Boolean> used;

	public Kangaroo(Main main) {
		this.used = Maps.newHashMap();

		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onKangarooInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.getItemInHand().getType() != null && player.getItemInHand().getType() == Material.FIREWORK
				&& PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.KANGAROO)) {
			event.setCancelled(true);
			if (!this.used.containsKey(player.getUniqueId())
					|| !((Boolean) this.used.get(player.getUniqueId())).booleanValue()) {
				boolean sneak = player.isSneaking();
				float multiplier = !sneak ? 0.6F : 1.5F;
				float vertical = !sneak ? 0.8F : 0.5F;
				Vector kangaVel = player.getEyeLocation().getDirection();
				player.setVelocity(kangaVel.multiply(multiplier).setY(vertical));
				player.setFallDistance(0.0F);
				this.used.put(player.getUniqueId(), Boolean.valueOf(true));
			}
		}
	}

	@EventHandler
	public void onKangarooGround(PlayerOnGroundEvent event) {
		if (!event.getOnGround()) {
			return;
		}
		Player player = event.getPlayer();
		if (this.used.containsKey(player.getUniqueId())
				&& ((Boolean) this.used.get(player.getUniqueId())).booleanValue()) {
			this.used.put(player.getUniqueId(), Boolean.valueOf(false));
		}
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		if (this.used.containsKey(event.getPlayer().getUniqueId())) {
			this.used.remove(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = event.getEntity();

			if (this.used.containsKey(player.getUniqueId()))
				this.used.remove(player.getUniqueId());
		}
	}
}
