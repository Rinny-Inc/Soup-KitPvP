package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class Gladiator extends Abilities implements Listener {
	private Map<UUID, Gladiators> gladiators;
	private Location[] gladiatorZones;
	private Main plugin;
	public Gladiator(Main main) {
		super("Gladiator", new ItemStack(Material.IRON_FENCE), Rarity.LEGENDARY, 20L, new String[] { ChatColor.AQUA + "Duel your opponent" });
		this.gladiators = Maps.newHashMap();
		final World world = main.getServer().getWorld("world");
		this.gladiatorZones = new Location[] {
				new Location(world, -50.5D, 170.5D, 775.5D, 135.0F, 0.0F),
				new Location(world, -68.5D, 170.5D, 756.5D, -45.0F, 0.0F),
				new Location(world, 58.5D, 177.5D, 788.5D, -135.0F, 0.0F),
				new Location(world, 77.5D, 177.5D, 769.5D, 42.0F, 0.0F)};

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
				if (this.gladiators.containsKey(r.getUniqueId())) {
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
	public void onGladiatorsDeath(PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = e.getEntity();
			if (this.gladiators.containsKey(p.getUniqueId())) {
				final List<ItemStack> loot = Lists.newArrayList(e.getDrops());
				e.getDrops().clear();
				final Player enemy = ((Gladiators) this.gladiators.get(p.getUniqueId())).getOpponentPlayer();
				for (Player allPlayers : this.plugin.getServer().getOnlinePlayers()) {
					p.showPlayer(allPlayers);
					enemy.showPlayer(allPlayers);
				}
				enemy.sendMessage(ChatColor.GREEN + "You have beated " + p.getName());
				enemy.setNoDamageTicks(50);
				p.sendMessage(ChatColor.RED + "You lost in face of " + enemy.getName());
				final Location loc = ((Gladiators) this.gladiators.get(enemy.getUniqueId())).getLastLocation();
				enemy.teleport(loc);
				for (ItemStack loots : loot) {
					if (loots.getItemMeta().hasDisplayName())
						continue;
					p.getWorld().dropItemNaturally(
							((Gladiators) this.gladiators.get(p.getUniqueId())).getLastLocation(), loots, p);
				}
				this.gladiators.remove(p.getUniqueId());
				this.gladiators.remove(enemy.getUniqueId());
				if (p.hasMetadata("Gladiator")) {
					p.removeMetadata("Gladiator", this.plugin);
				}
				if (enemy.hasMetadata("Gladiator")) {
					enemy.removeMetadata("Gladiator", this.plugin);
					PlayerManager.get(enemy.getUniqueId()).getAbility().applyCooldown();
				}
			}
		}
	}

	@EventHandler
	public void onGladiatorsQuit(PlayerQuitEvent e) {
		final Player p = e.getPlayer();
		if (this.gladiators.containsKey(p.getUniqueId())) {
			final Player enemy = ((Gladiators) this.gladiators.get(p.getUniqueId())).getOpponentPlayer();
			for (Player allPlayers : this.plugin.getServer().getOnlinePlayers()) {
				p.showPlayer(allPlayers);
				enemy.showPlayer(allPlayers);
			}
			enemy.sendMessage(ChatColor.GREEN + "You have beated " + p.getName());
			enemy.setNoDamageTicks(50);
			p.sendMessage(ChatColor.RED + "You lost in face of " + enemy.getName());
			final Location loc = ((Gladiators) this.gladiators.get(enemy.getUniqueId())).getLastLocation();
			enemy.teleport(loc);
			this.gladiators.remove(p.getUniqueId());
			this.gladiators.remove(enemy.getUniqueId());

			if (p.hasMetadata("Gladiator")) {
				p.removeMetadata("Gladiator", this.plugin);
			}
			if (enemy.hasMetadata("Gladiator")) {
				enemy.removeMetadata("Gladiator", this.plugin);
				PlayerManager.get(enemy.getUniqueId()).getAbility().applyCooldown();
			}
		}
	}

	@EventHandler
	public void onJoinForGladiators(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		for (Player fightings : this.plugin.getServer().getOnlinePlayers()) {
			if (fightings == null || !this.gladiators.containsKey(fightings.getUniqueId()))
				continue;
			fightings.hidePlayer(player);
		}
	}

	@EventHandler
	public void onDropReceive(PlayerPickupItemEvent event) {
		final Player receiver = event.getPlayer();
		if (!this.gladiators.containsKey(receiver.getUniqueId())) {
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
		gladiator.sendMessage(ChatColor.GREEN + "You have gladiator " + target.getName());
		target.sendMessage(ChatColor.GREEN + "You have been gladiator by " + gladiator.getName());
		this.gladiators.put(gladiator.getUniqueId(), new Gladiators(target.getUniqueId(), gladiator.getLocation()));
		this.gladiators.put(target.getUniqueId(), new Gladiators(gladiator.getUniqueId(), target.getLocation()));
		for (Player allPlayers : this.plugin.getServer().getOnlinePlayers()) {
			gladiator.hidePlayer(allPlayers);
			target.hidePlayer(allPlayers);
		}
		final int random = new Random().nextInt(1);
		gladiator.teleport(this.gladiatorZones[(random == 0 ? 0 : 2)]);
		target.teleport(this.gladiatorZones[(random == 0 ? 1 : 3)]);
		gladiator.showPlayer(target);
		target.showPlayer(gladiator);
		gladiator.setNoDamageTicks(75);
		target.setNoDamageTicks(75);
		gladiator.setMetadata("Gladiator", new FixedMetadataValue(this.plugin, Boolean.valueOf(true)));
	}

	private class Gladiators {
		private UUID uuid;
		private Location location;

		public Gladiators(UUID uuid, Location location) {
			this.uuid = uuid;
			this.location = location;
		}

		public Player getOpponentPlayer() {
			return Bukkit.getPlayer(this.uuid);
		}

		public Location getLastLocation() {
			return this.location;
		}
	}
}
