package io.noks.kitpvp.listeners.abilities;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

public class Reaper extends Abilities implements Listener {
	private Main plugin;
	public Reaper(Main main) {
		super("Reaper", new ItemStack(Material.WOOD_HOE), Rarity.RARE, 0L, new String[] { ChatColor.AQUA + "20% chance to give wither II", ChatColor.AQUA + "to your opponents" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getUnbreakableItemStack(this.getIcon().getType());
	}
	
	@Override
	public String specialItemName() {
		return "Scythe";
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof org.bukkit.entity.Player && event.getEntity() instanceof org.bukkit.entity.Player && PlayerManager.get(event.getDamager().getUniqueId()).getAbility().hasAbility(this)) {
			if (((Player)event.getDamager()).getItemInHand().getType() != Material.WOOD_HOE) {
				return;
			}
			final int rand = new Random().nextInt(100);
			if (rand > 20) {
				return;
			}
			LivingEntity living = (LivingEntity) event.getEntity();
			living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, (new Random()).nextInt(1)));
		}
	}
}
