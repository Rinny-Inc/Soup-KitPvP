package io.noks.kitpvp.listeners.abilities;

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Snail implements Listener {
	private Main plugin;

	public Snail(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof org.bukkit.entity.Player
				&& event.getEntity() instanceof org.bukkit.entity.Player
				&& PlayerManager.get(event.getDamager().getUniqueId()).getAbility().hasAbility(AbilitiesEnum.SNAIL)
				&& !PlayerManager.get(event.getEntity().getUniqueId()).getAbility().hasAbility(AbilitiesEnum.CONTRE)) {
			final int rand = new Random().nextInt(100);
			if (rand <= 33) {
				return;
			}
			LivingEntity living = null;
			if (!PlayerManager.get(event.getEntity().getUniqueId()).getAbility().hasAbility(AbilitiesEnum.CONTRE)) {
				living = (LivingEntity) event.getEntity();
				living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, (new Random()).nextInt(1)));
			} else {
				living = (LivingEntity) event.getDamager();
				living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, (new Random()).nextInt(1)));
			}
		}
	}
}
