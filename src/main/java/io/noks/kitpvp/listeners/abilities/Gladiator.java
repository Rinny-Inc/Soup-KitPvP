package io.noks.kitpvp.listeners.abilities;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.avaje.ebean.validation.NotNull;
import com.google.common.collect.Lists;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

// TODO: arena doesnt despawn

public class Gladiator extends Abilities {
	private @Nullable UUID opps, gladiator;
	private @Nullable Location gladiatorLastLocation, oppsLastLocation;
	private @Nullable Vector<Location> cage;
	private static @NotNull Main plugin;
	public Gladiator(Main main) {
		super("Gladiator", new ItemStack(Material.IRON_FENCE), Rarity.BETA/*Rarity.LEGENDARY*/, 20L, new String[] { ChatColor.AQUA + "Duel your opponent" });
		plugin = main;
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getIcon();
	}
	
	@Override
	public String specialItemName() {
		return "Gladiator Fence";
	}
	
	@Override
	public boolean needCloning() {
		return true;
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			final Player p = e.getPlayer();
			final PlayerManager pm = PlayerManager.get(p.getUniqueId());
			if (p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.IRON_FENCE && pm.hasAbility(this)) {
				if (pm.hasActiveAbilityCooldown()) {
					final double cooldown = pm.getActiveAbilityCooldown().longValue() / 1000.0D;
					p.sendMessage(ChatColor.RED + "You can use your ability in " + df.format(cooldown) + " seconds.");
					return;
				}
				final Player r = (Player) e.getRightClicked();
				if (r.getUniqueId() == this.opps) {
					return;
				}
				setupGladiatorsDuel(p, r);
			}
		}
	}
	
	@Override
	public void onDeath(PlayerDeathEvent e) {
		if (opps == null) {
			return;
		}
		if (opps != e.getEntity().getUniqueId() || gladiator != e.getEntity().getUniqueId()) {
			return;
		}
		if (e.getEntity() instanceof Player) {
			final List<ItemStack> loot = Lists.newArrayList(e.getDrops());
			e.getDrops().clear();
			final Player deadPlayer = plugin.getServer().getPlayer((e.getEntity().getUniqueId() == opps ? opps : gladiator));
			final Player winner = plugin.getServer().getPlayer((deadPlayer.getUniqueId() == opps ? gladiator : opps));
			for (Player allPlayers : plugin.getServer().getOnlinePlayers()) {
				deadPlayer.showPlayer(allPlayers);
				winner.showPlayer(allPlayers);
			}
			winner.sendMessage(ChatColor.GREEN + "You have defeated the gladiator!");
			winner.setNoDamageTicks(50);
			deadPlayer.sendMessage(ChatColor.RED + "You lost against the gladiator!");
			final Location loc = (winner.getUniqueId() == opps ? oppsLastLocation : gladiatorLastLocation);
			winner.teleport(loc);
			for (ItemStack loots : loot) {
				if (loots.getItemMeta().hasDisplayName()) {
					continue;
				}
				deadPlayer.getWorld().dropItemNaturally(loc, loots, deadPlayer);
			}
			this.clearMemory();
		}
	}

	@Override
	public void leaveAction(Player p) {
		if (opps != null) {
			if (opps != p.getUniqueId() || gladiator != p.getUniqueId()) {
				return;
			}
			final List<ItemStack> loot = Lists.newArrayList(p.getInventory().getContents());
			final Player deadPlayer = plugin.getServer().getPlayer((p.getUniqueId() == opps ? opps : gladiator));
			final Player winner = plugin.getServer().getPlayer((deadPlayer.getUniqueId() == opps ? gladiator : opps));
			for (Player allPlayers : plugin.getServer().getOnlinePlayers()) {
				deadPlayer.showPlayer(allPlayers);
				winner.showPlayer(allPlayers);
			}
			winner.sendMessage(ChatColor.GREEN + "You have defeated the gladiator!");
			winner.setNoDamageTicks(50);
			deadPlayer.sendMessage(ChatColor.RED + "You lost against the gladiator!");
			final Location loc = (winner.getUniqueId() == opps ? oppsLastLocation : gladiatorLastLocation);
			winner.teleport(loc);
			for (ItemStack loots : loot) {
				if (loots.getItemMeta().hasDisplayName()) {
					continue;
				}
				deadPlayer.getWorld().dropItemNaturally(loc, loots, deadPlayer);
			}
			this.clearMemory();
		}
	}

	private void setupGladiatorsDuel(Player gladiator, Player target) {
		if (target.getPassenger() != null) {
			target.getPassenger().eject();
		}
		gladiator.sendMessage(ChatColor.GREEN + "You have challenged " + target.getName());
		target.sendMessage(ChatColor.GREEN + "You have been challenged by " + gladiator.getName());
		this.gladiator = gladiator.getUniqueId();
		this.opps = target.getUniqueId();
		this.gladiatorLastLocation = gladiator.getLocation();
		this.oppsLastLocation = target.getLocation();
		for (Player allPlayers : plugin.getServer().getOnlinePlayers()) {
			gladiator.hidePlayer(allPlayers, false);
			target.hidePlayer(allPlayers, false);
		}
		
		this.createCage(gladiator, target);
		
		gladiator.showPlayer(target);
		target.showPlayer(gladiator);
		gladiator.setNoDamageTicks(75);
		target.setNoDamageTicks(75);
	}
	
	private void clearMemory() {
		// Let GC do his job
		this.gladiator = null;
		this.opps = null;
		this.gladiatorLastLocation = null;
		this.oppsLastLocation = null;
		this.clearCage();
	}
	
	private void createCage(Player gladiator, Player opps) {
		if (this.cage == null) {
			this.cage = new Vector<Location>();
		}
		final Location loc = gladiator.getLocation().clone().add(0, 100, 0);
		final World world = loc.getWorld();
		final int centerX = loc.getBlockX();
        int centerY = loc.getBlockY();
        int centerZ = loc.getBlockZ();
		final int halfSize = 8;
	    boolean blockFound = false;

	    foundBlock:
	    if (!blockFound) {
		    for (int x = centerX - halfSize; x <= centerX + halfSize; x++) {
		        for (int z = centerZ - halfSize; z <= centerZ + halfSize; z++) {
		        	 for (int y = centerY - halfSize; y <= centerY + halfSize; y++) {
				        if (y > world.getMaxHeight()) continue;
		                if (!world.getBlockAt(x, y, z).getType().equals(Material.AIR)) {
		                    blockFound = true;
		                    break foundBlock;
		                }
		            }
		        }
		    }
	    } else {
	    	centerY += 40;
	    }
        
		for (int x = centerX - halfSize; x <= centerX + halfSize; x++) {
            for (int z = centerZ - halfSize; z <= centerZ + halfSize; z++) {
            	for (int y = centerY - halfSize; y <= centerY + halfSize; y++) {
            		if (y > world.getMaxHeight()) continue;
            		
                    if ((x == centerX - halfSize || x == centerX + halfSize || y == centerY - halfSize || y == centerY + halfSize || z == centerZ - halfSize || z == centerZ + halfSize) || (y == centerY - halfSize && (x != centerX || z != centerZ))) {
                        Block block = world.getBlockAt(x, y, z);
                    	this.cage.add(block.getLocation());
                    	block.setType(Material.GLASS);
                    }
                }
            }
        }
		// TODO: set yaw and pitch
		gladiator.teleport(new Location (world, centerX - halfSize, centerY - halfSize, centerZ - halfSize));
		opps.teleport(new Location(world, centerX + halfSize, centerY + halfSize, centerZ + halfSize));
	}
	
	private void clearCage() {
		Iterator<Location> it = this.cage.iterator();
		while (it.hasNext()) {
			Location loc = it.next();
			loc.getBlock().setType(Material.AIR);
			it.remove();
		}
		this.cage = null; // Let GC do his job
	}
}
