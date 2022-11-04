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
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.utils.ItemUtils;

public class Switcher extends Abilities implements Listener {
	private Main plugin;

	public Switcher(Main main) {
		super("Switcher", new ItemStack(Material.SNOW_BALL), Rarity.UNIQUE, 0L, new String[] { ChatColor.AQUA + "Switch your position with another player" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return new ItemStack(this.getIcon().getType(), 6);
	}
	
	@Override
	public String specialItemName() {
		return "Switcher Ball";
	}

	@EventHandler
	public void onSwitcherSwitch(ProjectileHitEvent event) {
		if (event.getEntity() instanceof org.bukkit.entity.Snowball && event.getEntity().getShooter() instanceof Player
				&& event.getHitEntity() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();

			if (PlayerManager.get(shooter.getUniqueId()).getAbility().hasAbility(this)) {
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

			if (pm.getAbility().hasAbility(this))
				if (player.getInventory().firstEmpty() == -1 && !player.getInventory().contains(Material.SNOW_BALL)) {
					player.getWorld().dropItem(player.getLocation(),
							ItemUtils.getInstance().getItemStack(new ItemStack(Material.SNOW_BALL, 2),
									ChatColor.RED + this.specialItemName(), null));
				} else {
					player.getInventory().addItem(
							new ItemStack[] { ItemUtils.getInstance().getItemStack(new ItemStack(Material.SNOW_BALL, 2),
									ChatColor.RED + this.specialItemName(), null) });
				}
		}
	}
}
