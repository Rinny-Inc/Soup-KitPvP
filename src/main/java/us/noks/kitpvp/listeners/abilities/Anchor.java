package us.noks.kitpvp.listeners.abilities;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import us.noks.kitpvp.Main;
import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.managers.PlayerManager;
import us.noks.kitpvp.managers.caches.Ability;

public class Anchor implements Listener {
	private Main plugin;

	public Anchor(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onAnchor(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			final Player victim = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			Ability victimAbility = PlayerManager.get(victim.getUniqueId()).getAbility();
			Ability damagerAbility = PlayerManager.get(damager.getUniqueId()).getAbility();
			if (damagerAbility.hasAbility(AbilitiesEnum.ANTIANCHOR) || victimAbility.hasAbility(AbilitiesEnum.ANTIANCHOR)) {
				return;
			}
			if (damagerAbility.hasAbility(AbilitiesEnum.ANCHOR) || victimAbility.hasAbility(AbilitiesEnum.ANCHOR)) {
				victim.setVelocity(new Vector());
				(new BukkitRunnable() {
					public void run() {
						victim.setVelocity(new Vector());
					}
				}).runTaskLater(this.plugin, 1L);
				damager.playSound(victim.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
				victim.playSound(victim.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
			}
		}
	}
}
