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

	public Archer(Main main) {
		super("Archer", new ItemStack(Material.BOW), Rarity.COMMON, 0L, new String[] { "(Distance x 0.25) = damage" });
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@Override
	public ItemStack specialItem() {
		return Main.getInstance().getItemUtils().getItemUnbreakable(Material.BOW);
	}

	@Override
	public String specialItemName() {
		return "Special Bow";
	}

	@Override
	public Material sword() {
		return null;
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			final Arrow arrow = (Arrow) event.getEntity();
			final Player shooter = (Player) arrow.getShooter();
			
			if (PlayerManager.get(shooter.getUniqueId()).getAbility().hasAbility(this)) {
				final Player hitted = (Player) event.getHitEntity();
				if (hitted == shooter) return;
				final double damage = shooter.getLocation().distance(hitted.getLocation()) * 0.35D;
				hitted.damage(damage, shooter);
				shooter.sendMessage(ChatColor.GRAY + "Damage given " + (new DecimalFormat("#.#")).format(damage));
			}
		}
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
