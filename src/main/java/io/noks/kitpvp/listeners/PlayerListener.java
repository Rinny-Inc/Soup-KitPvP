package io.noks.kitpvp.listeners;

import java.text.DecimalFormat;
import java.util.Random;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
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
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.RefreshType;
import io.noks.kitpvp.interfaces.SignRotation;
import io.noks.kitpvp.listeners.abilities.Gladiator;
import io.noks.kitpvp.listeners.abilities.Ninja;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.RefillInventoryManager;
import io.noks.kitpvp.managers.caches.CombatTag;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Stats;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerListener implements Listener, SignRotation {
	private final Main plugin;
	
	public PlayerListener(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (this.plugin.getConfigManager().sendJoinAndQuitMessageToOP && this.plugin.getServer().getOnlinePlayers().size() > 1) {
		    this.plugin.getServer().getOnlinePlayers().stream().filter(Player::isOp).forEach(opPlayers -> opPlayers.sendMessage(event.getJoinMessage()));
		}
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
		player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
		player.teleport(player.getWorld().getSpawnLocation());
		player.setAllowFlight(false);
		player.setFlying(false);
		player.sendMessage(this.plugin.getMessages().WELCOME_MESSAGE);
		player.setPlayerListHeaderFooter(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().tabHeader)), TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().tabFooter)));
		this.plugin.getDataBase().loadPlayer(player.getUniqueId());
		player.getInventory().setContents(this.plugin.getItemUtils().getSpawnItems(player.getName()));
		player.updateInventory();
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
		if (this.plugin.getConfigManager().sendJoinAndQuitMessageToOP && this.plugin.getServer().getOnlinePlayers().size() > 1) {
		    this.plugin.getServer().getOnlinePlayers().stream().filter(Player::isOp).forEach(opPlayers -> opPlayers.sendMessage(event.getQuitMessage()));
		}
		event.setQuitMessage(null);
		this.leaveAction(event.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onGettingKicked(PlayerKickEvent event) {
		event.setLeaveMessage(null);
		this.leaveAction(event.getPlayer());
	}
	
	private void leaveAction(Player player) {
		final @Nullable PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm == null) {
			return;
		}
		if (pm.hasAbility()) {
			pm.ability().leaveAction(player);
		}
		boolean abilityLeaveActionDone = false;
		if (pm.hasCombatTag()) {
			final PlayerManager km = PlayerManager.get(pm.getCurrentCombatTag().getLastAttackerUUID()); 
			if (km != null) {
				final Player killer = km.getPlayer();

				if (killer != player) {
					if (km.hasAbility()) {
						km.ability().leaveAction(player);
						km.ability().onKill(killer);
						abilityLeaveActionDone = true;
					}
					killer.sendMessage(ChatColor.GREEN + "You have killed " + player.getDisplayName());

					final Stats killerStats = km.getStats();
					killerStats.addKills();
					killerStats.addKillStreak();

					final Economy killerEconomy = km.getEconomy();
					killerEconomy.add(((new Random()).nextInt(1) + 1) * (killer.hasPermission("vip.reward") ? 20 : 10));
					km.refreshScoreboardLine(RefreshType.KILLS, RefreshType.KILLSTREAK, RefreshType.CREDITS);
				}
			}
		}
		if (!abilityLeaveActionDone && player.getLastInteractedByUUID() != null) {
			PlayerManager lastInteractedBy = PlayerManager.get(player.getLastInteractedByUUID());
			
			if (lastInteractedBy != null && lastInteractedBy.hasAbility() && (lastInteractedBy.ability() instanceof Gladiator || lastInteractedBy.ability() instanceof Ninja)) {
				lastInteractedBy.ability().leaveAction(player);
			}
		}
		pm.kill(false);
		if (this.plugin.isTournamentActive()) {
			this.plugin.getActiveTournament().killAttendee(pm.getPlayerUUID());
			// TODO
		}
		if (this.plugin.mapTask != null) {
			if (this.plugin.mapTask.playersInMap.contains(pm)) {
				this.plugin.mapTask.playersInMap.remove(pm);
			}
			if (this.plugin.mapTask.playersInMap.isEmpty()) {
				this.plugin.mapTask.clearTask();
				this.plugin.mapTask = null;
			}
		}
		this.plugin.getDataBase().savePlayer(pm);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		event.setDroppedExp(0);
		event.setDeathMessage(null);
		if (event.getEntity() instanceof Player) {
			final Player killed = event.getEntity();
			final PlayerManager pm = PlayerManager.get(killed.getUniqueId());

			if (pm.hasAbility()) {
				pm.ability().onDeath(event);
			}
			if (killed.getItemOnCursor() != null) {
				killed.setItemOnCursor(null);
			}
			boolean abilityDeathActionDone = false;
			if (pm.hasCombatTag()) {
				final PlayerManager km = PlayerManager.get(pm.getCurrentCombatTag().getLastAttackerUUID());
				final Player killer = km.getPlayer();

				killed.sendMessage(ChatColor.RED + "You have been killed by " + killer.getDisplayName());
				if (killer != killed) {
					if (km.hasAbility()) {
						km.ability().onKill(killer);
						km.ability().onDeath(event);
						abilityDeathActionDone = true;
					}
					if (killer != null) {
						killer.sendMessage(ChatColor.GREEN + "You have killed " + killed.getDisplayName());
					}

					final Stats killerStats = km.getStats();
					killerStats.addKills();
					killerStats.addKillStreak();

					final Economy killerEconomy = km.getEconomy();
					killerEconomy.add(((new Random()).nextInt(1) + 1) * (killer.hasPermission("vip.reward") ? 20 : 10));
					km.refreshScoreboardLine(RefreshType.KILLS, RefreshType.KILLSTREAK, RefreshType.CREDITS);
				}
			}
			if (!abilityDeathActionDone && killed.getLastInteractedByUUID() != null) {
				PlayerManager lastInteractedBy = PlayerManager.get(killed.getLastInteractedByUUID());
				
				if (lastInteractedBy != null && lastInteractedBy.hasAbility() && (lastInteractedBy.ability() instanceof Gladiator || lastInteractedBy.ability() instanceof Ninja)) {
					lastInteractedBy.ability().onDeath(event);
				}
			}
			event.getDrops().clear();
			final int random = new Random().nextInt(10) + 15;
			for (int i = 0; i < random; i++) {
				event.getDrops().add(new ItemStack(Material.MUSHROOM_SOUP, 1));
			}
			this.plugin.applySpawnProtection(killed, false);
			pm.kill(false);
			if (this.plugin.isTournamentActive()) {
				this.plugin.getActiveTournament().killAttendee(pm.getPlayerUUID());
				// TODO
			}
			if (this.plugin.mapTask == null) {
				return;
			}
			if (this.plugin.mapTask.playersInMap.isEmpty()) {
				this.plugin.mapTask.clearTask();
				this.plugin.mapTask = null;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
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
		this.plugin.applySpawnProtection(player, true);
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
				if (player.getItemInHand().getAmount() > 1) {
					final int amount = player.getItemInHand().getAmount();
					player.getInventory().addItem(new ItemStack(Material.BOWL, 1));
					player.getItemInHand().setAmount(amount - 1);
				} else {
					player.getItemInHand().setType(Material.BOWL);
				}
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
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			PlayerManager pm = PlayerManager.get(event.getPlayer().getUniqueId());
			
			if (!pm.hasAbility()) {
				Player npc = (Player) event.getRightClicked();
				
				if (npc.getName().toLowerCase().contains("shop")) {
					event.setCancelled(true);
					event.getPlayer().openInventory(this.plugin.getInventoryManager().openShopInventory());
				}
				return;
			}
			pm.ability().onPlayerInteractEntity(event);
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onAbilitySelectorClick(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		ItemStack item = event.getItem();
		if (item.getItemMeta().getDisplayName() == null) {
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Player player = event.getPlayer();
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());
			if (pm.hasAbility()) {
				pm.ability().onInteract(event);
				return;
			}
			final String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase());
			switch (itemName) {
				case "ability selector" -> player.openInventory(this.plugin.getInventoryManager().loadKitsInventory(player));
				case "settings" -> player.openInventory(this.plugin.getInventoryManager().loadSettingsInventory(player));
				case "stats" -> player.performCommand("stats");
				case "perk selector" -> player.sendMessage(ChatColor.RED + "Coming soon :)");
				case "shop" -> player.openInventory(this.plugin.getInventoryManager().openShopInventory());
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
		if (!pm.hasAbility()) {
			event.setCancelled(true);
			return;
		}
		final ItemStack droppedItem = event.getItemDrop().getItemStack();
		if (droppedItem.getType() == Material.BOWL) {
			event.getItemDrop().remove();
			return;
		}
		/*if (droppedItem == pm.getAbility().ability().sword()) {
			int swords = 0;
			for (ItemStack item : player.getInventory().getContents()) {
				if (item == droppedItem) {
					swords++;
				}
			}
			if (swords == 1) {
				event.setCancelled(true);
			}
			return;
		}*/
		if (pm.ability().specialItem().getType() != Material.MUSHROOM_SOUP && droppedItem.getType() == pm.ability().specialItem().getType()) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			if (event.getEntity().getUniqueId() == event.getDamager().getUniqueId()) {
				return;
			}
			final PlayerManager damagedM = PlayerManager.get(event.getEntity().getUniqueId());
			if (!damagedM.hasAbility()) {
				return;
			}
			final PlayerManager damagerM = PlayerManager.get(event.getDamager().getUniqueId());
			if (damagerM.hasAbility()) {
				damagedM.ability().onEntityDamageByEntity(event);
			}
			damagedM.updateCombatTag(new CombatTag(damagerM.getPlayerUUID()));
			damagerM.updateCombatTag(new CombatTag(damagedM.getPlayerUUID()));
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());
			if (!pm.hasAbility()) {
				event.setCancelled(true);
				if (event.getCause() == DamageCause.VOID && !pm.hasAbility()) {
					player.teleport(player.getWorld().getSpawnLocation());
				}
			}
		}
	}

	
	@EventHandler
	public void onWantRefill(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WALL_SIGN && event.getClickedBlock().getState() instanceof Sign) {
			final Block signBlock = event.getClickedBlock(); 
			final Sign sign = (Sign) event.getClickedBlock().getState();
			final Block block = this.getBlockBehindSign(signBlock, sign);
			if (block.getType() == Material.GLOWSTONE && block.getRelative(BlockFace.UP).getType() == Material.WOOL) {
				final Player player = event.getPlayer();
				if (PlayerManager.get(player.getUniqueId()).hasAbility()) {
					Location loc = block.getLocation();
					final RefillInventoryManager im = RefillInventoryManager.get(loc);
					if (im.hasCooldown()) {
						DecimalFormat df = new DecimalFormat("#.#");
						double cooldown = im.getCooldown() / 1000.0D;
						double cooldownPercentage = 100.0 - ((cooldown / 60) * 100.0);
						player.sendMessage(ChatColor.RED + "Refill in progress " + df.format(cooldownPercentage) + "% " + "(" + df.format(cooldown) + "s)");
						return;
					}
					if (!im.isFilled()) {
						im.setFilled(true);
					}
					player.openInventory(im.getInventory());
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || player.isDead()) {
			return;
		}
		if (this.plugin.isTournamentActive() && this.plugin.getActiveTournament().containsAttendee(player.getUniqueId())) {
			return;
		}
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (!pm.hasAbility() && !this.plugin.spawnCuboid().isIn(event.getFrom())) {
			pm.setAbility((pm.getSelectedAbility().needCloning() ? pm.getSelectedAbility().clone() : pm.getSelectedAbility()));
			this.plugin.getItemUtils().giveEquipment(player, pm.ability());
			this.plugin.applySpawnProtection(player, false);
			return;
		}
		if (pm.hasAbility()) {
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
					player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
				} catch (NumberFormatException e) {}
				return;
			}
			if (this.plugin.spawnCuboid().isIn(event.getTo())) {
				event.setTo(event.getFrom());
			}
		}
	}
}
