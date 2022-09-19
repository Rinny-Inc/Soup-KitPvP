package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.utils.ItemUtils;

public class Switcher implements Listener {
	private Main plugin;

	public Switcher(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onSwitcherSwitch(ProjectileHitEvent event) {
		if (event.getEntity() instanceof org.bukkit.entity.Snowball && event.getEntity().getShooter() instanceof Player
				&& event.getHitEntity() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();

			if (PlayerManager.get(shooter.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.SWITCHER)) {
				Player hit = (Player) event.getHitEntity();

				if (hit == shooter)
					return;
				Location shooterLoc = shooter.getLocation();
				Location hitLoc = hit.getLocation();
				hit.teleport(shooterLoc);
				shooter.teleport(hitLoc);
			}
		}
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = event.getEntity().getKiller();
			PlayerManager pm = PlayerManager.get(player.getUniqueId());

			if (pm.getAbility().hasAbility(AbilitiesEnum.SWITCHER))
				if (player.getInventory().firstEmpty() == -1 && !player.getInventory().contains(Material.SNOW_BALL)) {
					player.getWorld().dropItem(player.getLocation(),
							ItemUtils.getInstance().getItemStack(new ItemStack(Material.SNOW_BALL, 2),
									ChatColor.RED + pm.getAbility().get().getSpecialItemName(), null));
				} else {
					player.getInventory().addItem(
							new ItemStack[] { ItemUtils.getInstance().getItemStack(new ItemStack(Material.SNOW_BALL, 2),
									ChatColor.RED + pm.getAbility().get().getSpecialItemName(), null) });
				}
		}
	}
}
