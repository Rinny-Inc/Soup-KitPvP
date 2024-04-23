package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Kangaroo extends Abilities implements Listener {
	private boolean used;
	private Main plugin;
	public Kangaroo(Main main) {
		super("Kangaroo", new ItemStack(Material.FIREWORK), Rarity.UNIQUE, 10L, new String[] { ChatColor.AQUA + "Jump like a kangaroo" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getIcon();
	}
	
	@Override
	public String specialItemName() {
		return "Kangaroo Rocket";
	}
	
	@Override
	public ItemStack[] armors() {
		final ItemStack h = new ItemStack(Material.IRON_HELMET);
		final ItemStack c = new ItemStack(Material.IRON_CHESTPLATE);
		final ItemStack l = new ItemStack(Material.GOLD_LEGGINGS);
		l.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
		final ItemStack b = new ItemStack(Material.IRON_BOOTS);
		return new ItemStack[] {b, l, c, h};
	}

	@EventHandler
	public void onKangarooInteract(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		final Player player = event.getPlayer();
		if (player.getItemInHand().getType() != null && player.getItemInHand().getType() == Material.FIREWORK && PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(this)) {
			event.setCancelled(true);
			if (!this.used) {
				final boolean sneak = player.isSneaking();
				final float multiplier = !sneak ? 0.775F : 1.5F;
				final float vertical = !sneak ? 0.885F : 0.5F;
				final Vector kangaVel = player.getEyeLocation().getDirection();
				player.setVelocity(kangaVel.multiply(multiplier).setY(vertical));
				player.setFallDistance(0.0F);
				this.used = true;
			}
		}
	}

	@EventHandler
	public void onKangarooGround(PlayerOnGroundEvent event) {
		if (!event.getOnGround()) {
			return;
		}
		if (this.used) {
			this.used = false;
		}
	}
}
