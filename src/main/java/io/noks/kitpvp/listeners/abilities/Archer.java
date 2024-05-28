package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Archer extends Abilities implements Listener {

	private Main main;
	
	public Archer(Main main) {
		super("Archer", new ItemStack(Material.BOW), Rarity.COMMON, 0L, new String[] { "(Distance x 1.3) = damage" });
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@Override
	public ItemStack specialItem() {
		return this.main.getItemUtils().getItemUnbreakable(Material.BOW);
	}

	@Override
	public String specialItemName() {
		return "Enchanced Bow";
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow && event.getHitEntity() != null) {
			final Arrow arrow = (Arrow) event.getEntity();
			final Player shooter = (Player) arrow.getShooter();
			
			if (PlayerManager.get(shooter.getUniqueId()).hasAbility(this)) {
				final Player hitted = (Player) event.getHitEntity();
				if (hitted == shooter) return;
				final double damage = shooter.getLocation().distance(hitted.getLocation()) * 1.3D;
				hitted.damage(damage, shooter);
				shooter.sendMessage(ChatColor.GRAY + "Damage given " + (new DecimalFormat("#.#")).format(damage));
			}
		}
	}
	
	@Override
	public void onKill(Player killer) {
		if (killer.getInventory().firstEmpty() == -1 && !killer.getInventory().contains(Material.ARROW)) {
			killer.getWorld().dropItem(killer.getLocation(), this.main.getItemUtils().getItemStack(new ItemStack(Material.ARROW, 2), null, null));
			return;
		}
		killer.getInventory().addItem(new ItemStack[] { this.main.getItemUtils().getItemStack(new ItemStack(Material.ARROW, 2), null, null) });
	}

	/*@EventHandler
	public void onArrowDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			Player shooter = (Player) arrow.getShooter();

			if (PlayerManager.get(shooter.getUniqueId()).getAbility().hasAbility(this)) {
				Player hitted = (Player) event.getEntity();
				if (hitted == shooter) return;
				double damage = shooter.getLocation().distance(hitted.getLocation()) * 0.35D;
				event.setDamage(damage);
				shooter.sendMessage(ChatColor.GRAY + "Damage given " + (new DecimalFormat("#.#")).format(damage));
			}
		}
	}*/
}
