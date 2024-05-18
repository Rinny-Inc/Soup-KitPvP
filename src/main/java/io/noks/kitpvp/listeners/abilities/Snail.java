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

public class Snail extends Abilities implements Listener {
	private Main plugin;

	public Snail(Main main) {
		super("Snail", new ItemStack(Material.POTION, 1, (short) 16426), Rarity.RARE, 0L, new String[] { ChatColor.AQUA + "20% chance to give slowness II", ChatColor.AQUA + "to your opponents" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof org.bukkit.entity.Player && event.getEntity() instanceof org.bukkit.entity.Player && PlayerManager.get(event.getDamager().getUniqueId()).hasAbility(this)) {
			final int rand = new Random().nextInt(100);
			if (rand > 20) {
				return;
			}
			final LivingEntity living = (LivingEntity) event.getEntity();
			living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, (new Random()).nextInt(1)));
		}
	}
}
