package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Blink extends Abilities {
	private int amountUsed;
	public Blink() {
		super("Blink", new ItemStack(Material.NETHER_STAR), Rarity.UNCOMMON, 15L, new String[] { ChatColor.AQUA + "Use your star to get", ChatColor.AQUA + "away from dangerous situations" });
		this.amountUsed = 0;
	}
	
	@Override
	public ItemStack specialItem() {
		return new ItemStack(Material.NETHER_STAR);
	}
	
	@Override
	public String specialItemName() {
		return "Blink Star";
	}
	
	@Override
	public boolean needCloning() {
		return true;
	}

	@Override
	public void onInteract(PlayerInteractEvent e) {
		if (!e.hasItem()) {
			return;
		}
		final Player p = e.getPlayer();
		final Action action = e.getAction();
		final PlayerManager pm = PlayerManager.get(p.getUniqueId());
		if (action == Action.RIGHT_CLICK_AIR && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.NETHER_STAR && pm.hasAbility(this)) {
			if (pm.hasActiveAbilityCooldown()) {
				final double cooldown = pm.getActiveAbilityCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
				return;
			}
			final Block block = p.getTargetBlock(null, 10);
			if (block.getType() != Material.AIR) return;
			amountUsed++;
			block.setType(Material.LEAVES_2);
			p.teleport(new Location(block.getWorld(), block.getX() + 0.5D, block.getY() + 1.5D, block.getZ() + 0.5D, p.getLocation().getYaw(), p.getLocation().getPitch()));
			p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
			p.setFallDistance(0.0F);
			new BukkitRunnable() {
					
				@Override
				public void run() {
					block.setType(Material.AIR);
				}
			}.runTaskLaterAsynchronously(Main.getInstance(), 100L);
			if (amountUsed == 3) {
				pm.applyAbilityCooldown();
				amountUsed = 0;
			}
		}
	}
}
