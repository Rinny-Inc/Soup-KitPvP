package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Kangaroo extends Abilities implements Listener {
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
		final ItemStack[] armor = super.armors();
		final ItemStack l = new ItemStack(Material.GOLD_LEGGINGS);
		l.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
		armor[1] = l;
		return armor;
	}

	@EventHandler
	public void onKangarooInteract(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		final Player player = event.getPlayer();
		if (player.getItemInHand().getType() != null && player.getItemInHand().getType() == Material.FIREWORK && PlayerManager.get(player.getUniqueId()).hasAbility(this)) {
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());
			event.setCancelled(true);
			if (pm.hasActiveCooldown()) {
				final double cooldown = pm.getActiveCooldown().longValue() / 1000.0D;
				player.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
				return;
			}
			final boolean sneak = player.isSneaking();
			final float multiplier = !sneak ? 0.775F : 1.5F;
			final float vertical = !sneak ? 0.885F : 0.5F;
			final Vector kangaVel = player.getEyeLocation().getDirection();
			player.setVelocity(kangaVel.multiply(multiplier).setY(vertical));
			player.setFallDistance(0.0F);
			pm.applyCooldown();
		}
	}
}
