package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

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
		if (event.getEntity() instanceof org.bukkit.entity.Snowball && event.getEntity().getShooter() instanceof Player && event.getHitEntity() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();

			if (PlayerManager.get(shooter.getUniqueId()).getAbility().hasAbility(this)) {
				final Player hit = (Player) event.getHitEntity();

				if (hit == shooter)return;
				hit.teleport(shooter.getLocation());
				shooter.teleport(hit.getLocation());
			}
		}
	}
}
