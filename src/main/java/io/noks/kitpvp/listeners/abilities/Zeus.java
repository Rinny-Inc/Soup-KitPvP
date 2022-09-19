package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Zeus implements Listener {
	private Main plugin;

	public Zeus(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onZeus(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action action = e.getAction();
		PlayerManager pm = PlayerManager.get(p.getUniqueId());
		if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				&& p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.WOOD_AXE
				&& pm.getAbility().hasAbility(AbilitiesEnum.ZEUS)) {
			e.setCancelled(true);
			if (!pm.getAbility().hasActiveCooldown()) {
				pm.getAbility().applyCooldown();
				p.getWorld().strikeLightning(p.getLocation().add(3.0D, 0.0D, 0.0D));
				p.getWorld().strikeLightning(p.getLocation().add(-3.0D, 0.0D, 0.0D));
				p.getWorld().strikeLightning(p.getLocation().add(0.0D, 0.0D, 3.0D));
				p.getWorld().strikeLightning(p.getLocation().add(0.0D, 0.0D, -3.0D));
			} else {
				double cooldown = pm.getAbility().getActiveCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in "
						+ (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
			}
		}
	}

	@EventHandler
	public void onZeusDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (PlayerManager.get(p.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.ZEUS)
					&& e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING)
				e.setCancelled(true);
		}
	}
}
