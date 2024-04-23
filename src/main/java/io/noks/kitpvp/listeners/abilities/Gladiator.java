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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.avaje.ebean.validation.NotNull;
import com.google.common.collect.Lists;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class Gladiator extends Abilities implements Listener {
	private @Nullable UUID opps, gladiator;
	private @Nullable Location gladiatorLastLocation, oppsLastLocation;
	private @Nullable Map<Location, Material> cage;
	private final @NotNull Main plugin;
	public Gladiator(Main main) {
		super("Gladiator", new ItemStack(Material.IRON_FENCE), Rarity.LEGENDARY, 20L, new String[] { ChatColor.AQUA + "Duel your opponent" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getIcon();
	}
	
	@Override
	public String specialItemName() {
		return "Gladiator Fence";
	}

	@EventHandler
	public void onGladiator(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			final Player p = e.getPlayer();
			final Ability ability = PlayerManager.get(p.getUniqueId()).getAbility();
			if (p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.IRON_FENCE && ability.hasAbility(this)) {
				if (ability.hasActiveCooldown()) {
					final double cooldown = ability.getActiveCooldown().longValue() / 1000.0D;
					p.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
					return;
				}
				final Player r = (Player) e.getRightClicked();
				if (r.getUniqueId() == this.opps) {
					return;
				}
				final Ability clickedAbility = PlayerManager.get(r.getUniqueId()).getAbility();
				if (clickedAbility.get() instanceof AntiGladiator) {
					ability.applyCooldown();
					return;
				}
				setupGladiatorsDuel(p, r);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		if (opps == null) {
			return;
		}
		if (e.getEntity() instanceof Player) {
			final List<ItemStack> loot = Lists.newArrayList(e.getDrops());
			e.getDrops().clear();
			final Player deadPlayer = this.plugin.getServer().getPlayer((e.getEntity().getUniqueId() == opps ? opps : gladiator));
			final Player winner = this.plugin.getServer().getPlayer((deadPlayer.getUniqueId() == opps ? gladiator : opps));
			for (Player allPlayers : this.plugin.getServer().getOnlinePlayers()) {
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

	@EventHandler
	public void onGladiatorsQuit(PlayerQuitEvent e) {
		this.leaveAction(e.getPlayer());
	}
	@EventHandler
	public void onGladiatorsKick(PlayerKickEvent e) {
		this.leaveAction(e.getPlayer());
	}
	private void leaveAction(Player p) {
		if (opps != null) {
			final List<ItemStack> loot = Lists.newArrayList(p.getInventory().getContents());
			final Player deadPlayer = this.plugin.getServer().getPlayer((p.getUniqueId() == opps ? opps : gladiator));
			final Player winner = this.plugin.getServer().getPlayer((deadPlayer.getUniqueId() == opps ? gladiator : opps));
			for (Player allPlayers : this.plugin.getServer().getOnlinePlayers()) {
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

	@EventHandler
	public void onJoinForGladiators(PlayerJoinEvent event) {
		if (opps == null) {
			return;
		}
		final Player player = event.getPlayer();
		for (Player fightings : this.plugin.getServer().getOnlinePlayers()) {
			if (fightings.getUniqueId() == opps || fightings.getUniqueId() == gladiator) {
				continue;
			}
			fightings.hidePlayer(player);
		}
	}

	@EventHandler
	public void onDropReceive(PlayerPickupItemEvent event) {
		final Player receiver = event.getPlayer();
		if (receiver.getUniqueId() == opps || receiver.getUniqueId() == gladiator) {
			return;
		}
		if (event.getItem().getOwner() instanceof Player) {
			final Player dropper = (Player) event.getItem().getOwner();
			if (!receiver.canSee(dropper))
				event.setCancelled(true);
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
		for (Player allPlayers : this.plugin.getServer().getOnlinePlayers()) {
			gladiator.hidePlayer(allPlayers);
			target.hidePlayer(allPlayers);
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
		int centerX = loc.getBlockX();
        int centerY = loc.getBlockX();
        int centerZ = loc.getBlockX();
		int halfSize = 7; // Half of the cube size - 1
        
		for (int x = centerX - halfSize; x <= centerX + halfSize; x++) {
            for (int y = centerY - halfSize; y <= centerY + halfSize; y++) {
                for (int z = centerZ - halfSize; z <= centerZ + halfSize; z++) {
                    // Place glass blocks only on the outer layer and inside the bottom layer
                    if ((x == centerX - halfSize || x == centerX + halfSize || y == centerY - halfSize || y == centerY + halfSize || z == centerZ - halfSize || z == centerZ + halfSize) || (y == centerY - halfSize && (x != centerX || z != centerZ))) {
                        this.cage.put(loc.getWorld().getBlockAt(x, y, z).getLocation(), loc.getWorld().getBlockAt(x, y, z).getType());
                    	loc.getWorld().getBlockAt(x, y, z).setType(Material.GLASS);
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
