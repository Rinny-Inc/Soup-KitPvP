package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

// TODO: NEED TO CHECK IF BLOCK ARE IN THE CREATING ZONE OF THE CAGE, MOVE IT UP

public class Gladiator extends Abilities {
	private @Nullable UUID opps, gladiator;
	private @Nullable Location gladiatorLastLocation, oppsLastLocation;
	private @Nullable Map<Location, Material> cage;
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
					p.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
					return;
				}
				final Player r = (Player) e.getRightClicked();
				if (r.getUniqueId() == this.opps) {
					return;
				}
				final PlayerManager rm = PlayerManager.get(r.getUniqueId());
				if (rm.ability() instanceof AntiGladiator) {
					pm.applyAbilityCooldown();
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
		this.gladiator = null;
		this.opps = null;
		this.gladiatorLastLocation = null;
		this.oppsLastLocation = null;
		this.clearCage();
	}
	
	private void createCage(Player gladiator, Player opps) {
		if (this.cage == null) {
			this.cage = new HashMap<Location, Material>();
		}
		final Location loc = gladiator.getLocation().clone().add(0, 100, 0);
		final int centerX = loc.getBlockX();
        int centerY = loc.getBlockX();
        int centerZ = loc.getBlockX();
		int halfSize = 7;
	    boolean blockFound = false;

	    for (int x = centerX - halfSize; x <= centerX + halfSize; x++) {
	        for (int y = centerY - halfSize; y <= centerY + halfSize; y++) {
	            for (int z = centerZ - halfSize; z <= centerZ + halfSize; z++) {
	                if (!loc.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR)) {
	                    blockFound = true;
	                    break;
	                }
	            }
	            if (blockFound) break;
	        }
	        if (blockFound) break;
	    }

	    if (blockFound) {
	        centerY += 50;
	    }
        
		for (int x = centerX - halfSize; x <= centerX + halfSize; x++) {
            for (int y = centerY - halfSize; y <= centerY + halfSize; y++) {
                for (int z = centerZ - halfSize; z <= centerZ + halfSize; z++) {
                    if ((x == centerX - halfSize || x == centerX + halfSize || y == centerY - halfSize || y == centerY + halfSize || z == centerZ - halfSize || z == centerZ + halfSize) || (y == centerY - halfSize && (x != centerX || z != centerZ))) {
                        Block block = loc.getWorld().getBlockAt(x, y, z);
                    	this.cage.put(block.getLocation(), block.getType());
                    	block.setType(Material.GLASS);
                    }
                }
            }
        }
		
		gladiator.teleport(new Location (loc.getWorld(), centerX - halfSize, centerY - halfSize, centerZ - halfSize));
		opps.teleport(new Location(loc.getWorld(), centerX + halfSize, centerY + halfSize, centerZ + halfSize));
	}
	
	private void clearCage() {
		for (Map.Entry<Location, Material> entry : this.cage.entrySet()) {
			Location loc = entry.getKey();
			Material oldMaterial = entry.getValue();
			loc.getWorld().getBlockAt(loc).setType(oldMaterial);
		}
		this.cage = null;
	}
}
