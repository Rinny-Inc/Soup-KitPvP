package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Horseman extends Abilities implements Listener {
	
	public Horseman() {
		super("Horseman", new ItemStack(Material.SADDLE), Rarity.BETA, 25L, new String[] {ChatColor.AQUA + "Summon a Horse and explore the world"});
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getIcon();
	}
	
	@Override
	public String specialItemName() {
		return ChatColor.RED + "Horse Invocator";
	}
	
	// DO interact to call the horse
	
	@EventHandler
	public void onExitHorse(EntityDismountEvent event) {
		if (event.getDismounted() instanceof Horse) {
			event.getDismounted().remove();
			PlayerManager pm = PlayerManager.get(event.getEntity().getUniqueId());
			if (pm.hasAbility(this)) {
				pm.applyAbilityCooldown();
			}
		}
	}
	
	@EventHandler
	public void onExitHorse(EntityMountEvent event) {
		if (event.getMount() instanceof Horse) {
			Horse horse = (Horse) event.getMount();
			
			if (horse.getOwner() != event.getEntity()) {
				event.setCancelled(true);
			}
		}
	}
}
