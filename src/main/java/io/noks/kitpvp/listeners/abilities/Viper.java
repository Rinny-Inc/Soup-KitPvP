package io.noks.kitpvp.listeners.abilities;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Viper extends Abilities implements Listener {
	private Main plugin;

	public Viper(Main main) {
		super("Viper", new ItemStack(Material.POTION, 1, (short) 16388), Rarity.RARE, 0L, new String[] { ChatColor.AQUA + "20% chance to give poison I/II", ChatColor.AQUA + "to your opponents" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void Damage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof org.bukkit.entity.Player && event.getEntity() instanceof org.bukkit.entity.Player && PlayerManager.get(event.getDamager().getUniqueId()).hasAbility(this)) {
			final Random random = new Random();
			final int rand = random.nextInt(100);
			if (rand > 20) {
				return;
			}
			final LivingEntity living = (LivingEntity) event.getEntity();
			living.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, random.nextInt(1)));
		}
	}
}
