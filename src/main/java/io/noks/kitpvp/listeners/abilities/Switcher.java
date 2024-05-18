package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
		return new ItemStack(this.getIcon().getType(), 4);
	}
	
	@Override
	public String specialItemName() {
		return "Switcher Ball";
	}
	
	@Override
	public ItemStack[] armors() {
		ItemStack[] armor = super.armors();
		final ItemStack h = new ItemStack(Material.LEATHER_HELMET);
		h.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		h.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
		final ItemStack b = new ItemStack(Material.LEATHER_BOOTS);
		b.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		b.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
		armor[3] = h;
		armor[0] = b;
		return armor;
	}

	@EventHandler
	public void onSwitch(ProjectileHitEvent event) {
		if (event.getEntity() instanceof org.bukkit.entity.Snowball && event.getEntity().getShooter() instanceof Player && event.getHitEntity() instanceof Player) {
			final Player shooter = (Player) event.getEntity().getShooter();

			if (PlayerManager.get(shooter.getUniqueId()).hasAbility(this)) {
				final Player hit = (Player) event.getHitEntity();

				if (hit == shooter)return;
				if (PlayerManager.get(hit.getUniqueId()).isInSpawn()) {
					return;
				}
				final Location hittedLoc = hit.getLocation();
				hit.teleport(shooter.getLocation());
				shooter.teleport(hittedLoc);
			}
		}
	}
	
	@Override
	public void onKill(Player killer) {
		if (killer.getInventory().firstEmpty() == -1 && (!killer.getInventory().contains(this.specialItem()))) {
			killer.getWorld().dropItem(killer.getLocation(), Main.getInstance().getItemUtils().getItemStack(new ItemStack(this.specialItem().getType(), 2), ChatColor.RED + this.specialItemName(), null));
			return;
		}
		killer.getInventory().addItem(new ItemStack[] { Main.getInstance().getItemUtils().getItemStack(new ItemStack(this.specialItem().getType(), 2), ChatColor.RED + this.specialItemName(), null) });
	}
}
