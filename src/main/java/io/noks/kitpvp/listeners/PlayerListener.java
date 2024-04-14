package io.noks.kitpvp.listeners;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.avaje.ebean.validation.NotNull;

import io.noks.custom.utils.EntityNPC;
import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.RefreshType;
import io.noks.kitpvp.listeners.abilities.Boxer;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.managers.caches.CombatTag;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Economy.MoneyType;
import io.noks.kitpvp.task.MapTask;
import io.noks.kitpvp.managers.caches.Stats;
import io.noks.kitpvp.utils.Cuboid;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerListener implements Listener {
	private final Main plugin;
	private final Cuboid spawnCuboid;
	private @NotNull MapTask mapTask;
	
	public PlayerListener(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
		final World world = main.getServer().getWorld("world");
		this.spawnCuboid = new Cuboid(new Location(world, -34, 96, 31), new Location(world, 23, 102, -15));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		final Player player = event.getPlayer();
		player.setScoreboard(this.plugin.getServer().getScoreboardManager().getMainScoreboard());
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(10000.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		if (!player.getActivePotionEffects().isEmpty()) {
			for (PotionEffect activeEffects : player.getActivePotionEffects()) {
				player.removePotionEffect(activeEffects.getType());
			}
		}
		player.teleport(player.getWorld().getSpawnLocation());
		player.setAllowFlight(false);
		player.setFlying(false);
		player.sendMessage(this.plugin.getMessages().WELCOME_MESSAGE);
		player.setPlayerListHeaderFooter(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().tabHeader)), TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().tabFooter)));
		this.plugin.getDataBase().loadPlayer(player.getUniqueId());
		player.getInventory().setContents(this.plugin.getItemUtils().getSpawnItems(player.getName()));
		player.updateInventory();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		this.leaveAction(event.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onGettingKicked(PlayerKickEvent event) {
		event.setLeaveMessage(null);
		this.leaveAction(event.getPlayer());
	}
	
	private void leaveAction(Player player) {
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm.hasCombatTag()) {
			final PlayerManager km = PlayerManager.get(pm.getCurrentCombatTag().getLastAttackerUUID()); 
			if (km != null) {
				final Player killer = km.getPlayer();

				if (killer != player) {
					if (km.getAbility().hasAbility()) {
						km.getAbility().get().onKill(killer);
					}
					killer.sendMessage(ChatColor.GREEN + "You have killed " + player.getDisplayName());

					final Stats killerStats = km.getStats();
					killerStats.addKills();
					km.refreshScoreboardLine(RefreshType.KILLS);
					killerStats.addKillStreak();
					km.refreshScoreboardLine(RefreshType.KILLSTREAK);

					final Economy killerEconomy = km.getEconomy();
					killerEconomy.add(((new Random()).nextInt(1) + 1) * (killer.hasPermission("vip.reward") ? 20 : 10), MoneyType.BRONZE);
					km.refreshScoreboardLine(RefreshType.CREDITS);
				}
			}
		}
		pm.kill(false);
		this.plugin.getDataBase().savePlayer(pm);
		if (this.mapTask == null) {
			return;
		}
		final List<PlayerManager> playersInMap = PlayerManager.players.values().stream().filter(not(PlayerManager::isInSpawn)).collect(Collectors.toList());
		if (playersInMap.isEmpty()) {
			this.mapTask.clearTask();
			this.mapTask = null;
		}
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

				killed.sendMessage(ChatColor.RED + "You have been killed by" + killer.getDisplayName());
				if (killer != killed) {
					if (km.getAbility().hasAbility()) {
						km.getAbility().get().onKill(killer);
					}
					if (killer != null) {
						killer.sendMessage(ChatColor.GREEN + "You have killed " + killed.getDisplayName());
					}

					final Stats killerStats = km.getStats();
					killerStats.addKills();
					km.refreshScoreboardLine(RefreshType.KILLS);
					killerStats.addKillStreak();
					km.refreshScoreboardLine(RefreshType.KILLSTREAK);

					final Economy killerEconomy = km.getEconomy();
					killerEconomy.add(((new Random()).nextInt(1) + 1) * (killer.hasPermission("vip.reward") ? 20 : 10), MoneyType.BRONZE);
					km.refreshScoreboardLine(RefreshType.CREDITS);
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
			this.applySpawnProtection(killed, false);
			pm.kill(false);
			if (this.mapTask == null) {
				return;
			}
			final List<PlayerManager> playersInMap = PlayerManager.players.values().stream().filter(not(PlayerManager::isInSpawn)).collect(Collectors.toList());
			if (playersInMap.isEmpty()) {
				this.mapTask.clearTask();
				this.mapTask = null;
			}
		}
	}
	
	private static <T> Predicate<T> not(Predicate<T> p) { 
		return p.negate();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		event.setRespawnLocation(player.getWorld().getSpawnLocation());
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(0.0F);
		player.getInventory().setContents(this.plugin.getItemUtils().getSpawnItems(player.getName()));
		player.updateInventory();
		this.applySpawnProtection(player, true);
	}

	@EventHandler
	public void onPlayerInteractSoup(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Player player = event.getPlayer();
			if (!player.isDead() && player.getItemInHand().getType() == Material.MUSHROOM_SOUP && player.getHealth() < player.getMaxHealth()) {
				event.setUseItemInHand(Result.DENY);
				final double newHealth = Math.min(player.getHealth() + 7.0D, player.getMaxHealth());
				player.setHealth(newHealth);
				player.getItemInHand().setType(Material.BOWL);
				player.updateInventory();
			} 
		} 
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onConsumeSoup(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() == Material.MUSHROOM_SOUP) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onAbilitySelectorClick(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Player player = event.getPlayer();
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());
			if (!pm.isInSpawn()) {
				return;
			}
			if (player.getItemInHand().getType() == Material.ENCHANTED_BOOK && player.getItemInHand().getItemMeta().getDisplayName().toLowerCase().equals(ChatColor.DARK_AQUA + "ability selector")) {
				player.openInventory(this.plugin.getInventoryManager().loadKitsInventory(player));
				return;
			}
			if (player.getItemInHand().getType() == Material.WATCH && player.getItemInHand().getItemMeta().getDisplayName().toLowerCase().equals(ChatColor.DARK_AQUA + "settings")) {
				player.openInventory(this.plugin.getInventoryManager().loadSettingsInventory(player));
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
	
	// TODO: remake with critical damage and enchantment
	// TODO: FIX DOUBLE HIT????
	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof EntityNPC) {
			event.setCancelled(true);
			return;
		}
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			if (event.getEntity() != event.getDamager()) {
				PlayerManager.get(event.getEntity().getUniqueId()).updateCombatTag(new CombatTag(event.getDamager().getUniqueId()));
				PlayerManager.get(event.getDamager().getUniqueId()).updateCombatTag(new CombatTag(event.getEntity().getUniqueId()));
			}
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
				}
			}
		}
	}

	@EventHandler
	public void onWantRefill(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENDER_CHEST && event.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.GLOWSTONE) {
			final Player player = event.getPlayer();

			if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility()) {
				event.setCancelled(true);
				player.openInventory(this.plugin.getInventoryManager().loadRefillInventory(player));
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || player.isDead()) {
			return;
		}
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm.isInSpawn() && !this.spawnCuboid.isIn(player.getLocation())) {
			final Ability ability = pm.getAbility();
			ability.set(ability.getSelected());
			this.plugin.getItemUtils().giveEquipment(player, ability.get());
			this.applySpawnProtection(player, false);
			return;
		}
		if (!pm.isInSpawn()) {
			// TODO: KOTH CODE HERE
			final Block sponge = event.getTo().getBlock().getRelative(BlockFace.DOWN);
			if (sponge.getType() == Material.SPONGE) {
				final Block signBlock = sponge.getLocation().getBlock().getRelative(BlockFace.DOWN);
				
				if (!(signBlock.getState() instanceof Sign)) {
					return;
				}
				final Sign sign = (Sign) signBlock.getState();
				final String firstLine = sign.getLine(0);
				try {
					final double multiplier = Double.parseDouble(firstLine);
					player.setVelocity(new Vector(0, multiplier, 0));
				} catch (NumberFormatException e) {}
			}
		}
	}
	private void applySpawnProtection(final Player player, final boolean remove) {
		for (Location loc : this.spawnCuboid.getEdgeLocations()) {
			if (loc.getY() < 99) {
				continue;
			}
			Block block = loc.getBlock();
			
			if (block.getType() != Material.AIR) {
				continue;
			}
			if (!remove) {
				player.sendBlockChange(loc, Material.STAINED_GLASS.getId(), (byte)14);
				continue;
			}
			player.sendBlockChange(loc, 0, (byte)0);
		}
		if (!remove && this.mapTask == null) {
			this.mapTask = new MapTask(this.plugin).startTask();
		}
	}
	
	public MapTask getMapTask() {
		return this.mapTask;
	}
}
