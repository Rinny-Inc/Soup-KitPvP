package io.noks.kitpvp.listeners.abilities;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Magma extends Abilities implements Listener {

	public Magma(Main main) {
		super("Magma", new ItemStack(Material.FIRE), Rarity.COMMON, 0L, new String[] { ChatColor.AQUA + "10% chance to put in fire", ChatColor.AQUA + "your opponent." });
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof org.bukkit.entity.Player && event.getDamager() instanceof org.bukkit.entity.Player && PlayerManager.get(event.getDamager().getUniqueId()).getAbility().hasAbility(this)) {
			final int rand = new Random().nextInt(100);
			if (rand > 10) {
				return;
			}
			final Biome biome = event.getDamager().getLocation().getBlock().getBiome();
			event.getEntity().setFireTicks(((biome == Biome.HELL) ? 20 : 10) * 4);
		}
	}
}
