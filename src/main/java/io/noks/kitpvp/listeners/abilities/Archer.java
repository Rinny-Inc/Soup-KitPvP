package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.utils.ItemUtils;

public class Archer implements Listener {
	private Main plugin;

	public Archer(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onArrowDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			Player shooter = (Player) arrow.getShooter();

			if (PlayerManager.get(shooter.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.ARCHER)) {
				Player hitted = (Player) event.getEntity();
				if (hitted == shooter) return;
				double damage = shooter.getLocation().distance(hitted.getLocation()) * 0.35D;
				event.setDamage(damage);
				shooter.sendMessage(ChatColor.GRAY + "Damage given " + (new DecimalFormat("#.#")).format(damage));
			}
		}
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = event.getEntity().getKiller();
			Ability ability = PlayerManager.get(player.getUniqueId()).getAbility();

			if (ability.hasAbility(AbilitiesEnum.ARCHER)) {
				if (player.getInventory().firstEmpty() == -1 && !player.getInventory().contains(Material.ARROW)) {
					player.getWorld().dropItem(player.getLocation(), ItemUtils.getInstance().getItemStack(new ItemStack(Material.ARROW, 2), null, null));
				} else {
					player.getInventory().addItem(new ItemStack[] { ItemUtils.getInstance().getItemStack(new ItemStack(Material.ARROW, 2), null, null) });
				}
			}
		}
	}
}
