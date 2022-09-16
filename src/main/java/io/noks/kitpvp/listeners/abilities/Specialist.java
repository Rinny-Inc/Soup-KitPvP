package io.noks.kitpvp.listeners.abilities;

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
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Specialist implements Listener {
	private Main plugin;

	public Specialist(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.SPECIALIST)
				&& event.getItem() != null && event.getItem().getType() == Material.ENCHANTED_BOOK) {
			event.setCancelled(true);
			player.getPlayer().openEnchanting(player.getPlayer().getLocation(), true);
		}
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		if (PlayerManager.get(event.getEnchanter().getUniqueId()).getAbility().hasAbility(AbilitiesEnum.SPECIALIST)
				&& event.getEnchanter().getItemInHand().getType() == Material.ENCHANTED_BOOK) {
			event.getEnchantsToAdd().clear();
			event.getEnchantsToAdd().put(Enchantment.DAMAGE_ALL, Integer.valueOf(
					(event.getEnchanter().getLevel() == 1) ? 1 : ((event.getEnchanter().getLevel() == 2) ? 2 : 3)));
		}
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player killer = event.getEntity().getKiller();

			if (PlayerManager.get(killer.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.SPECIALIST))
				if (killer.getInventory().firstEmpty() == -1 && !killer.getInventory().contains(Material.EXP_BOTTLE)) {
					killer.getWorld().dropItem(killer.getLocation(), new ItemStack(Material.EXP_BOTTLE, 1));
				} else {
					killer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.EXP_BOTTLE, 1) });
				}
		}
	}
}
