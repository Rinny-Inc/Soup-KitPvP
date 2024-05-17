package io.noks.kitpvp.listeners;

import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
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
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.RefreshType;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.RefillInventoryManager;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.managers.caches.CombatTag;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Economy.MoneyType;
import io.noks.kitpvp.managers.caches.Stats;
import io.noks.utils.EntityNPC;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerListener implements Listener {
	private final Main plugin;
	
	public PlayerListener(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (this.plugin.getConfigManager().sendJoinAndQuitMessageToOP && this.plugin.getServer().getOnlinePlayers().size() > 1) {
		    this.plugin.getServer().getOnlinePlayers().stream().filter(opPlayers -> opPlayers.isOp()).forEach(opPlayers -> opPlayers.sendMessage(event.getJoinMessage()));
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

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		if (this.plugin.getConfigManager().sendJoinAndQuitMessageToOP && this.plugin.getServer().getOnlinePlayers().size() > 1) {
		    this.plugin.getServer().getOnlinePlayers().stream().filter(opPlayers -> opPlayers.isOp()).forEach(opPlayers -> opPlayers.sendMessage(event.getQuitMessage()));
		}
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
						km.getAbility().ability().onKill(killer);
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		event.setDroppedExp(0);
		event.setDeathMessage(null);
		if (event.getEntity() instanceof Player) {
			final Player killed = event.getEntity();
			final PlayerManager pm = PlayerManager.get(killed.getUniqueId());

			if (killed.getItemOnCursor() != null) {
				killed.setItemOnCursor(null);
			}
			if (pm.hasCombatTag()) {
				final PlayerManager km = PlayerManager.get(pm.getCurrentCombatTag().getLastAttackerUUID());
				final Player killer = km.getPlayer();

				killed.sendMessage(ChatColor.RED + "You have been killed by " + killer.getDisplayName());
				if (killer != killed) {
					if (km.getAbility().hasAbility()) {
						km.getAbility().ability().onKill(killer);
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
			event.getDrops().clear();
			int random = new Random().nextInt(10) + 10;
			for (int i = 0; i < random; i++) {
				event.getDrops().add(new ItemStack(Material.MUSHROOM_SOUP, 1));
			}
			this.plugin.applySpawnProtection(killed, false);
			pm.kill(false);
			if (this.plugin.mapTask == null) {
				return;
			}
			if (this.plugin.mapTask.playersInMap.isEmpty()) {
				this.plugin.mapTask.clearTask();
				this.plugin.mapTask = null;
			}
		}
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
			if (!pm.isInSpawn()) {
				pm.getAbility().ability().onInteract(event);
				return;
			}
			final String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase());
			if (item.getType() == Material.ENCHANTED_BOOK && itemName.equals("ability selector")) {
				player.openInventory(this.plugin.getInventoryManager().loadKitsInventory(player));
				return;
			}
			if (item.getType() == Material.WATCH && itemName.equals("settings")) {
				player.openInventory(this.plugin.getInventoryManager().loadSettingsInventory(player));
				return;
			}
			if (item.getType() == Material.SKULL_ITEM && itemName.equals("stats")) {
				player.performCommand("stats");
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
		if (pm.getAbility().ability().specialItem().getType() != Material.MUSHROOM_SOUP && droppedItem.getType() == pm.getAbility().ability().specialItem().getType()) {
			event.setCancelled(true);
			return;
		}
	}
	
	// TODO: remake with critical damage and enchantment
	// TODO: FIX DOUBLE HIT????
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof EntityNPC) {
			event.setCancelled(true);
			return;
		}
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			if (event.getEntity() == event.getDamager()) {
				return;
			}
			final UUID damaged = event.getEntity().getUniqueId();
			final UUID damager = event.getDamager().getUniqueId();
			PlayerManager.get(damaged).updateCombatTag(new CombatTag(damager));
			PlayerManager.get(damager).updateCombatTag(new CombatTag(damaged));
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Villager) {
			event.setCancelled(true);
			return;
		}
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
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WALL_SIGN && event.getClickedBlock().getState() instanceof Sign) {
			final Block signBlock = event.getClickedBlock(); 
			final Sign sign = (Sign) event.getClickedBlock().getState();
			final Block block = this.getBlockBehindSign(signBlock, sign);
			if (block.getType() == Material.GLOWSTONE && block.getRelative(BlockFace.UP).getType() == Material.WOOL) {
				final Player player = event.getPlayer();
				// GET THE SIGN LINES
				if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility()) {
					Location loc = block.getLocation();
					final RefillInventoryManager im = RefillInventoryManager.get(loc);
					if (im.hasCooldown()) {
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
	
	private Block getBlockBehindSign(Block signBlock, Sign sign) {
        MaterialData signData = sign.getData();

        switch (signData.getData()) {
            case 2:
                return signBlock.getRelative(0, 0, 1);
            case 3:
                return signBlock.getRelative(0, 0, -1);
            case 4:
                return signBlock.getRelative(1, 0, 0);
            case 5:
                return signBlock.getRelative(-1, 0, 0);
            default:
                return null;
        }
    }
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || player.isDead()) {
			return;
		}
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm.isInSpawn() && !this.plugin.spawnCuboid().isIn(player.getLocation())) {
			final Ability ability = pm.getAbility();
			ability.set(ability.getSelected());
			this.plugin.getItemUtils().giveEquipment(player, ability.ability());
			this.plugin.applySpawnProtection(player, false);
			return;
		}
		if (!pm.isInSpawn()) {
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
			}
		}
	}
}
