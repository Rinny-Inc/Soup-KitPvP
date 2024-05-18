package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Specialist extends Abilities implements Listener {
	private Main plugin;

	public Specialist(Main main) {
		super("Specialist", new ItemStack(Material.ENCHANTED_BOOK), Rarity.LEGENDARY, 0L, new String[] { ChatColor.AQUA + "Enchantment table in a book" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getIcon();
	}
	
	@Override
	public String specialItemName() {
		return "Enchantment Book";
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		final Player player = event.getPlayer();

		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && PlayerManager.get(player.getUniqueId()).hasAbility(this) && event.getItem() != null && event.getItem().getType() == Material.ENCHANTED_BOOK) {
			event.setCancelled(true);
			player.getPlayer().openEnchanting(player.getPlayer().getLocation(), true);
		}
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		if (PlayerManager.get(event.getEnchanter().getUniqueId()).hasAbility(this) && event.getEnchanter().getItemInHand().getType() == Material.ENCHANTED_BOOK) {
			event.getEnchantsToAdd().clear();
			event.getEnchantsToAdd().put(Enchantment.DAMAGE_ALL, Integer.valueOf((event.getEnchanter().getLevel() == 1) ? 1 : ((event.getEnchanter().getLevel() == 2) ? 2 : 3)));
		}
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			final Player killer = event.getEntity().getKiller();

			if (PlayerManager.get(killer.getUniqueId()).hasAbility(this)) {
				if (killer.getInventory().firstEmpty() == -1 && !killer.getInventory().contains(Material.EXP_BOTTLE)) {
					killer.getWorld().dropItem(killer.getLocation(), new ItemStack(Material.EXP_BOTTLE, 1));
				} else {
					killer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.EXP_BOTTLE, 1) });
				}
			}
		}
	}
}
