package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Fisherman extends Abilities implements Listener {
	private Main plugin;

	public Fisherman(Main main) {
		super("Fisherman", new ItemStack(Material.FISHING_ROD), Rarity.RARE, 0L, new String[] { ChatColor.AQUA + "Catch your opponent with your rod" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return this.plugin.getItemUnbreakable(this.getIcon().getType());
	}
	
	@Override
	public String specialItemName() {
		return "Fisherman Rod";
	}
	
	@Override
	public ItemStack sword() {
		final ItemStack sword = super.sword();
		sword.removeEnchantment(Enchantment.DAMAGE_ALL);
		return sword;
	}
	
	@Override
	public ItemStack[] armors() {
		ItemStack[] armor = super.armors();
		final ItemStack h = new ItemStack(Material.GOLD_HELMET);
		h.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		h.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
		final ItemStack b = new ItemStack(Material.GOLD_BOOTS);
		b.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		b.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
		armor[3] = h;
		armor[0] = b;
		return armor;
	}

	@EventHandler
	public void onFish(PlayerFishEvent event) {
		if (event.getState() == State.CAUGHT_FISH) {
			event.setCancelled(true);
			return;
		}
		if (event.getCaught() instanceof Player) {
			final Player player = event.getPlayer();
			if (PlayerManager.get(player.getUniqueId()).hasAbility(this)) {
				final Player target = (Player) event.getCaught();
				if (target == player) return;
				if (!PlayerManager.get(target.getUniqueId()).hasAbility()) {
					event.setCancelled(true);
					return;
				}
				target.teleport(player.getLocation()); // TODO: teleport player in front of the fisherman
				target.setFallDistance(0.0F);
			}
		}
	}
}
