package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class Endermage implements Listener {
	private Main plugin;

	public Endermage(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Ability ability = PlayerManager.get(player.getUniqueId()).getAbility();

		if (!ability.hasAbility(AbilitiesEnum.ENDERMAGE)) {
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType() != Material.ENDER_PORTAL_FRAME) {
				return;
			}
			if (ability.hasActiveCooldown()) {
				double cooldown = ability.getActiveCooldown().longValue() / 1000.0D;
				player.sendMessage(ChatColor.RED + "You can use your ability in "
						+ (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
				return;
			}
			Location portal = block.getLocation().clone().add(0.5D, 1.0D, 0.5D);
			for (Entity e : player.getPlayer().getNearbyEntities(5.5D, 256.0D, 5.5D)) {
				if (e instanceof Player) {
					Player nearby = (Player) e;
					PlayerManager nm = PlayerManager.get(nearby.getUniqueId());
					if (nearby == player || !isEnderable(portal, nearby.getPlayer().getLocation())
							|| nm.getAbility().hasAbility(AbilitiesEnum.ENDERMAGE))
						continue;
					warpPlayer(nearby, portal);
					warpPlayer(player, portal);
					player.getPlayer().sendMessage(ChatColor.RED + "You teleported " + nearby.getName()
							+ ", you're invincible for 5 seconds!");
					nearby.getPlayer().sendMessage(ChatColor.RED
							+ "You have been teleported by an Endermage! You're invincible for 5 seconds!");
					ability.applyCooldown();
				}
			}
		}
	}

	private boolean isEnderable(Location portal, Location player) {
		return (Math.abs(portal.getX() - player.getX()) < 2.0D && Math.abs(portal.getZ() - player.getZ()) < 2.0D && Math.abs(portal.getY() - player.getY()) >= 3.5D);
	}

	private void warpPlayer(Player victim, Location loc) {
		victim.playEffect(victim.getPlayer().getLocation(), Effect.ENDER_SIGNAL, 9);
		victim.playEffect(loc, Effect.ENDER_SIGNAL, 9);
		victim.playSound(victim.getPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.2F);
		victim.playSound(loc, Sound.ENDERMAN_TELEPORT, 1.0F, 1.2F);
		victim.setNoDamageTicks(100);
		victim.teleport(loc);
	}
}
