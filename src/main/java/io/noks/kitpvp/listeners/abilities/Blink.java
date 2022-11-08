package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class Blink extends Abilities implements Listener {
	private Main plugin;
	private int maxUseTime;

	public Blink(Main main) {
		super("Blink", new ItemStack(Material.NETHER_STAR), Rarity.UNCOMMON, 15L, new String[] { ChatColor.AQUA + "Use your star to get", ChatColor.AQUA + "away from dangerous situations" });
		this.maxUseTime = 3;
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return new ItemStack(Material.NETHER_STAR);
	}
	
	@Override
	public String specialItemName() {
		return "Blink Star";
	}

	@EventHandler
	public void onBlink(PlayerInteractEvent e) {
		if (!e.hasItem()) {
			return;
		}
		Player p = e.getPlayer();
		Action action = e.getAction();
		Ability ability = PlayerManager.get(p.getUniqueId()).getAbility();
		if (action == Action.RIGHT_CLICK_AIR && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.NETHER_STAR && ability.hasAbility(this)) {
			if (ability.hasActiveCooldown()) {
				double cooldown = ability.getActiveCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
				return;
			}
			final Block block = p.getTargetBlock(null, 10);
			if (block.getType() != Material.AIR) return;
			ability.addUseTime();
			block.setType(Material.LEAVES_2);
			p.teleport(new Location(block.getWorld(), block.getX() + 0.5D, block.getY() + 1.5D, block.getZ() + 0.5D, p.getLocation().getYaw(), p.getLocation().getPitch()));
			p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
			p.setFallDistance(0.0F);
			new BukkitRunnable() {
					
				@Override
				public void run() {
					block.setType(Material.AIR);
				}
			}.runTaskLaterAsynchronously(this.plugin, 100L);
			if (ability.getUseTime() == this.maxUseTime) {
				ability.applyCooldown();
				ability.resetUseTime();
			}
		}
	}
}
