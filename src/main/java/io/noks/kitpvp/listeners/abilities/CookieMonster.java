package io.noks.kitpvp.listeners.abilities;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.utils.ItemUtils;

public class CookieMonster extends Abilities implements Listener {
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

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && PlayerManager.get(event.getPlayer().getUniqueId()).getAbility().hasAbility(this)) {
			Player p = event.getPlayer();
			if (p.getItemInHand().getType() == Material.COOKIE && p.getFoodLevel() == 20 && (!p.hasPotionEffect(PotionEffectType.SPEED) || p.getHealth() * 2.0D < p.getMaxHealth())) {
				p.setFoodLevel(19);
			}
		}
	}

	@EventHandler
	public void onConsumeCookie(PlayerItemConsumeEvent event) {
		if (PlayerManager.get(event.getPlayer().getUniqueId()).getAbility().hasAbility(this) && event.getItem().getType() == Material.COOKIE) {
			Player eater = event.getPlayer();

			eater.setHealth(Math.min(eater.getHealth() * 2.0D, eater.getMaxHealth()));
			eater.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 140, (new Random()).nextInt(1) + 1));
			eater.updateInventory();
			eater.setFoodLevel(20);
		}
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = event.getEntity().getKiller();
			Ability ability = PlayerManager.get(player.getUniqueId()).getAbility();

			if (ability.hasAbility(this)) {
				if (player.getInventory().firstEmpty() == -1 && !player.getInventory().contains(Material.COOKIE)) {
					player.getWorld().dropItem(player.getLocation(), ItemUtils.getInstance().getItemStack(new ItemStack(Material.COOKIE, 2), ChatColor.RED + specialItemName(), null));
				} else {
					player.getInventory().addItem(new ItemStack[] { ItemUtils.getInstance().getItemStack(new ItemStack(Material.COOKIE, 2), ChatColor.RED + specialItemName(), null) });
				}
			}
		}
	}
}
