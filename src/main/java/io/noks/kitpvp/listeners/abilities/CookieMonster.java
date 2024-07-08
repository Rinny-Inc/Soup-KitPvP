package io.noks.kitpvp.listeners.abilities;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class CookieMonster extends Abilities implements Listener {
	// TODO: REWORK NEEDED
	private Main plugin;

	public CookieMonster(Main main) {
		super("CookieMonster", new ItemStack(Material.COOKIE), Rarity.UNCOMMON, 0L, new String[] { ChatColor.AQUA + "COOKIIIIIIES" });
	    this.plugin = main;
	    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return new ItemStack(this.getIcon().getType(), 8);
	}
	
	@Override
	public String specialItemName() {
		return "Cookie";
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity().getItemInHand().getType() == Material.COOKIE) {
			event.setCancelled(true);
			event.setFoodLevel(19);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && PlayerManager.get(event.getPlayer().getUniqueId()).hasAbility(this)) {
			final Player p = event.getPlayer();
			if (p.getItemInHand().getType() == Material.COOKIE && p.getFoodLevel() == 20) {
				p.setSaturation(0);
				p.setFoodLevel(19);
			}
		}
	}

	@EventHandler
	public void onConsumeCookie(PlayerItemConsumeEvent event) {
		if (PlayerManager.get(event.getPlayer().getUniqueId()).hasAbility(this) && event.getItem().getType() == Material.COOKIE) {
			final Player eater = event.getPlayer();

			eater.setHealth(Math.min(eater.getHealth() * 2.0D, eater.getMaxHealth()));
			eater.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 140, (new Random()).nextInt(1) + 1));
			eater.updateInventory();
			eater.setFoodLevel(20);
			eater.setSaturation(10000.0F);
		}
	}
	
	@Override
	public void onKill(Player killer) {
		if (killer.getInventory().firstEmpty() == -1 && (!killer.getInventory().contains(this.specialItem()))) {
			killer.getWorld().dropItem(killer.getLocation(), Main.getInstance().getItemStack(new ItemStack(this.specialItem().getType(), 2), ChatColor.RED + this.specialItemName(), null));
			return;
		}
		killer.getInventory().addItem(new ItemStack[] { Main.getInstance().getItemStack(new ItemStack(this.specialItem().getType(), 2), ChatColor.RED + this.specialItemName(), null) });
	}
}
