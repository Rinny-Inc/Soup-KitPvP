package io.noks.kitpvp.listeners;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.listeners.abilities.Boxer;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.managers.caches.CombatTag;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Economy.MoneyType;
import io.noks.kitpvp.managers.caches.Stats;

public class PlayerListener implements Listener {
	private Main plugin;
	public PlayerListener(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		final Player player = event.getPlayer();
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(10000.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		player.teleport(player.getWorld().getSpawnLocation());
		player.setAllowFlight(false);
		player.setFlying(false);
		player.sendMessage(this.plugin.getMessages().WELCOME_MESSAGE);
		this.plugin.getDataBase().loadPlayer(player.getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		final Player player = event.getPlayer();
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm.hasCombatTag()) {
			final PlayerManager km = PlayerManager.get(pm.getCurrentCombatTag().getLastAttackerUUID());
			final Player killer = km.getPlayer();

			if (killer != player) {
				km.getAbility().get().onKill(killer);
				killer.sendMessage(ChatColor.GRAY + killer.getName() + "(" + ChatColor.RED + km.getAbility().get().getName() + ChatColor.GRAY + ") killed " + player.getName() + "(" + ChatColor.RED + pm.getAbility().get().getName() + ChatColor.GRAY + ")");

				final Stats killerStats = km.getStats();
				killerStats.addKills();
				killerStats.addKillStreak();

				final Economy killerEconomy = km.getEconomy();
				killerEconomy.add(((new Random()).nextInt(1) + 1) * (killer.hasPermission("vip.reward") ? 20 : 10), MoneyType.BRONZE);
			}
		}
		pm.kill();
		this.plugin.getDataBase().savePlayer(pm);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onGettingKicked(PlayerKickEvent event) {
		this.onQuit(new PlayerQuitEvent(event.getPlayer(), event.getLeaveMessage()));
		event.setLeaveMessage(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		event.setDroppedExp(0);
		event.setDeathMessage(null);
		if (event.getEntity() instanceof Player) {
			final Player killed = event.getEntity();
			final PlayerManager pm = PlayerManager.get(killed.getUniqueId());
			final Ability killedAbility = pm.getAbility();

			if (killed.getItemOnCursor() != null) {
				killed.setItemOnCursor(null);
			}
			if (!killedAbility.hasAbility()) {
				event.getDrops().clear();
				return;
			}
			if (killed.getKiller() instanceof Player && pm.hasCombatTag()) {
				final PlayerManager km = PlayerManager.get(pm.getCurrentCombatTag().getLastAttackerUUID());
				final Player killer = km.getPlayer();
				final String message = ChatColor.GRAY + killer.getName() + "(" + ChatColor.RED + km.getAbility().get().getName() + ChatColor.GRAY + ") killed " + killed.getName() + "(" + ChatColor.RED + killedAbility.get().getName() + ChatColor.GRAY + ")";

				killed.sendMessage(message);
				if (killer != killed) {
					km.getAbility().get().onKill(killer);
					killer.sendMessage(message);

					final Stats killerStats = km.getStats();
					killerStats.addKills();
					killerStats.addKillStreak();

					final Economy killerEconomy = km.getEconomy();
					killerEconomy.add(((new Random()).nextInt(1) + 1) * (killer.hasPermission("vip.reward") ? 20 : 10), MoneyType.BRONZE);
				}
			}

			if (killedAbility.get().specialItem().getType() != Material.MUSHROOM_SOUP) {
				Iterator<ItemStack> dropsIt = event.getDrops().iterator();
				while (dropsIt.hasNext()) {
					final ItemStack loot = (ItemStack) dropsIt.next();
					if (!loot.getItemMeta().hasDisplayName())
						continue;
					dropsIt.remove();
				}
			}
			pm.kill();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		event.setRespawnLocation(player.getWorld().getSpawnLocation());
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(0.0F);
		PlayerManager.get(player.getUniqueId()).giveMainItem();
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerInteractSoup(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Player player = event.getPlayer();
			if (!player.isDead() && player.getItemInHand().getType() == Material.MUSHROOM_SOUP && player.getHealth() < player.getMaxHealth()) {
				player.setHealth(Math.min(player.getHealth() + 7.0D, player.getMaxHealth()));
				player.getItemInHand().setType(Material.BOWL);
				player.updateInventory();
			}
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onAbilitySelectorClick(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Player player = event.getPlayer();
			if (player.getItemInHand().getType() == Material.BOOK && player.getItemInHand().getItemMeta().getDisplayName().toLowerCase().equals(ChatColor.GRAY + "your abilities")) {
				player.openInventory(this.plugin.getInventoryManager().loadKitsInventory(player));
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (player.getGameMode() == GameMode.CREATIVE && pm.isAllowBuild()) {
			return;
		}
		if (!pm.getAbility().hasAbility()) {
			event.setCancelled(true);
			return;
		}
		final ItemStack dropppedItem = event.getItemDrop().getItemStack();
		if (dropppedItem.getType() == Material.MUSHROOM_SOUP) return;
		if (dropppedItem.getType() == pm.getAbility().get().specialItem().getType()) {
			event.setCancelled(true);
			return;
		}
			/*if (dropppedItem.getType().toString().toLowerCase().contains("sword")) {
				int swords = 0;
				for (ItemStack item : player.getInventory().getContents()) {
					if (item.getType().toString().toLowerCase().contains("sword")) {
						swords++;
					}
				}
				if (swords == 1) {
					event.setCancelled(true);
				}
			}*/
	}

	@EventHandler
	public void combatTagOnHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			PlayerManager.get(event.getEntity().getUniqueId()).updateCombatTag(new CombatTag(event.getDamager().getUniqueId()));
			PlayerManager.get(event.getDamager().getUniqueId()).updateCombatTag(new CombatTag(event.getEntity().getUniqueId()));
		}
	}
	
	// TODO: remake with critical damage and enchantment
	// TODO: FIX DOUBLE HIT????
	@EventHandler
	public void nerfDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final Player damager = (Player) event.getDamager();
			final ItemStack handItem = damager.getItemInHand();
			if (handItem.getType() == Material.MUSHROOM_SOUP) {
				return;
			}
			double damage = event.getDamage();
			if (handItem.getType() == Material.AIR && PlayerManager.get(damager.getUniqueId()).getAbility().get() instanceof Boxer)
				damage += 2.0D;
			if (handItem.getType() == Material.WOOD_SWORD || handItem.getType() == Material.STONE_SWORD)
				damage -= 2.5D;
			if (handItem.getType() == Material.IRON_SWORD)
				damage -= 2.5D;
			if (handItem.containsEnchantment(Enchantment.DAMAGE_ALL))
				damage += (handItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL) / 2) + 0.25D;
			event.setDamage(damage);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());
			if (!pm.getAbility().hasAbility()) {
				event.setCancelled(true);
				if (event.getCause() == DamageCause.VOID && !pm.getAbility().hasAbility()) {
					player.teleport(player.getWorld().getSpawnLocation());
					return;
				}
				if (event.getCause() == DamageCause.FALL && player.hasMetadata("Sponged")) {
					event.setCancelled(true);
					player.removeMetadata("Sponged", this.plugin);
				}
			}
		}
	}
	
	@EventHandler
	public void onGround(PlayerOnGroundEvent event) {
		final Player player = event.getPlayer();
		if (player.hasMetadata("Sponged")) {
			player.removeMetadata("Sponged", this.plugin);
		}
	}

	@EventHandler
	public void onWantRefill(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENDER_CHEST && event.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.GLOWSTONE) {
			Player player = event.getPlayer();

			if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility()) {
				event.setCancelled(true);
				player.openInventory(this.plugin.getInventoryManager().loadRefillInventory(player));
			}
		}
	}

	@EventHandler
	public void onUseTracker(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Player player = event.getPlayer();
	
			if (player.getItemInHand().getType() == Material.COMPASS && player.getItemInHand().getItemMeta().getDisplayName().toLowerCase().equals(ChatColor.YELLOW + "tracker")) {
				Player nearest = null;
				double distance = 200.0D;
				for (Player onlineWorld : player.getWorld().getPlayers()) {
					double calc = player.getLocation().distance(onlineWorld.getLocation());
					if (calc > 1.0D && calc < distance) {
						distance = calc;
						if (onlineWorld == player || !player.canSee(onlineWorld) || !onlineWorld.canSee(player) || onlineWorld.getGameMode() != GameMode.SURVIVAL || onlineWorld.isDead())
							continue;
						nearest = onlineWorld;
					}
				}
				if (nearest == null) {
					return;
				}
				player.setCompassTarget(nearest.getLocation());
				player.sendMessage(ChatColor.YELLOW + "Compass pointing at " + nearest.getName() + " (" + ChatColor.GOLD + (new DecimalFormat("#.#")).format(distance) + ChatColor.YELLOW + ")");
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onWalkOnSponge(PlayerMoveEvent event) {
		final Block block = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		
		if (block.getType() == Material.SPONGE) {
			final Player player = event.getPlayer();
			int sponge = 0;
			for (int y = 20; y < 250; y++) {
				if (block.getWorld().getBlockAt(block.getX(), y, block.getZ()).getType() == Material.SPONGE) continue;
				sponge++;
			}
			final double boost = 2.15D + (0.05D * sponge);
			player.setVelocity(new Vector(0.0D, boost, 0.0D));
			player.setMetadata("Sponged", new FixedMetadataValue(this.plugin, Boolean.valueOf(true)));
		}
	}
}
