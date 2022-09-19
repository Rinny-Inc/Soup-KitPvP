package io.noks.kitpvp.listeners;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.Bukkit;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.inventories.CreateInventory;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Economy.MoneyType;
import io.noks.kitpvp.managers.caches.Stats;
import io.noks.kitpvp.utils.Messages;

public class PlayerListener implements Listener {
	private Main plugin;
	private Player wantedPlayer;

	public PlayerListener(Main main) {
		this.wantedPlayer = null;
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		final Player player = event.getPlayer();
		final PlayerManager pm = new PlayerManager(player.getUniqueId());
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
		player.sendMessage(Messages.WELCOME_MESSAGE);
		pm.giveMainItem();
		//DBUtils.getInstance().loadPlayer(PlayerManager.get(player.getUniqueId()));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		final Player player = event.getPlayer();

		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (player.getLastDamage() > 0.0D)
			pm.getStats().addDeaths();
		if (pm.getStats().getKillStreak() > pm.getStats().getBestKillStreak()) {
			pm.getStats().updateBestKillStreak();
		}

		if (this.wantedPlayer == player) {
			Bukkit.broadcastMessage("(WANTED) " + player.getName() + " killed himself!");
			this.wantedPlayer = null;
		}
		//DBUtils.getInstance().savePlayer(pm);
		pm.remove();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		event.setDroppedExp(0);
		event.setDeathMessage(null);
		if (event.getEntity() instanceof Player) {
			final Player killed = event.getEntity();
			final PlayerManager pm = PlayerManager.get(killed.getUniqueId());
			final Ability killedAbility = pm.getAbility();

			if (!killedAbility.hasAbility()) {
				event.getDrops().clear();
				return;
			}
			if (killed.getKiller() instanceof Player) {
				Player killer = killed.getKiller();
				PlayerManager km = PlayerManager.get(killer.getUniqueId());
				int[] killedStuff = { 0, 0, 0, 0 };
				for (ItemStack items : killed.getInventory().getContents()) {
					if (items != null) {
						if (items.getType() == Material.MUSHROOM_SOUP)
							killedStuff[0]++;
						if (items.getType() == Material.BOWL)
							killedStuff[1] = killedStuff[1] + items.getAmount();
						if (items.getType() == Material.BROWN_MUSHROOM)
							killedStuff[2] = killedStuff[2] + items.getAmount();
						if (items.getType() == Material.RED_MUSHROOM)
							killedStuff[3] = killedStuff[3] + items.getAmount();
					}
				}
				int[] killerStuff = { 0, 0, 0, 0 };
				for (ItemStack items : killer.getInventory().getContents()) {
					if (items != null) {
						if (items.getType() == Material.MUSHROOM_SOUP)
							killerStuff[0]++;
						if (items.getType() == Material.BOWL)
							killerStuff[1] = killerStuff[1] + items.getAmount();
						if (items.getType() == Material.BROWN_MUSHROOM)
							killerStuff[2] = killerStuff[2] + items.getAmount();
						if (items.getType() == Material.RED_MUSHROOM)
							killerStuff[3] = killerStuff[3] + items.getAmount();
					}
				}
				int soupDiff = Math.abs(killedStuff[0] - killerStuff[0]);
				int recraftDiff = Math.abs((killedStuff[1] + killedStuff[2] + killedStuff[3]) / 3
						- (killerStuff[1] + killerStuff[2] + killerStuff[3]) / 3);
				String message = ChatColor.GRAY + killer.getName() + "(" + ChatColor.RED
						+ km.getAbility().get().getName() + ChatColor.GRAY + ") killed " + killed.getName() + "("
						+ ChatColor.RED + killedAbility.get().getName() + ChatColor.GRAY + ") " + ChatColor.BLUE
						+ "[Soup Diff: " + soupDiff + "; Recraft Diff: " + recraftDiff + "]";

				killed.sendMessage(message);
				if (killer != killed) {
					killer.sendMessage(message);

					int randomLoots = (new Random()).nextInt(6) + 26;
					int[] missing = { 64 - killerStuff[1] - randomLoots, 64 - killerStuff[2] - randomLoots,
							64 - killerStuff[3] - randomLoots };
					int[] total = { killerStuff[1] + randomLoots, killerStuff[2] + randomLoots,
							killerStuff[3] + randomLoots };
					if (missing[0] > 0) {
						killer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.BOWL,
								Math.max(0, Math.min(total[0] + missing[0], randomLoots))) });
					}
					if (missing[1] > 0) {
						killer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.BROWN_MUSHROOM,
								Math.max(0, Math.min(total[1] + missing[1], randomLoots))) });
					}
					if (missing[2] > 0) {
						killer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.RED_MUSHROOM,
								Math.max(0, Math.min(total[2] + missing[2], randomLoots))) });
					}
					Stats killerStats = km.getStats();
					killerStats.addKills();
					killerStats.addKillStreak();

					Economy killerEconomy = km.getEconomy();
					int moneyToAdd = ((new Random()).nextInt(1) + 1) * (killer.hasPermission("vip.reward") ? 2 : 1);
					killerEconomy.add(moneyToAdd, MoneyType.BRONZE);

					if (this.wantedPlayer == null && killerStats.getKillStreak() >= 8) {
						int wantedKill = (new Random()).nextInt(30 + killerStats.getKillStreak() - 8) + 8;
						if (killerStats.getKillStreak() == wantedKill) {
							launchWantedEvent(killer, wantedKill);
						}
					}
					if (this.wantedPlayer == killed) {
						Bukkit.broadcastMessage("(WANTED) " + killer.getName() + " got the prime for killed the wanted player!");
						this.wantedPlayer = null;
					}

				}
			} else if (this.wantedPlayer == killed) {
				Bukkit.broadcastMessage("(WANTED) " + killed.getName() + " killed himself!");
				this.wantedPlayer = null;
			}

			if (killedAbility.get().getSpecialItem().getType() != Material.MUSHROOM_SOUP) {
				Iterator<ItemStack> dropsIt = event.getDrops().iterator();
				while (dropsIt.hasNext()) {
					ItemStack loot = (ItemStack) dropsIt.next();
					if (!loot.getItemMeta().hasDisplayName())
						continue;
					dropsIt.remove();
				}
			}
			Stats killedStats = pm.getStats();
			if (killed.getLastDamage() > 0.0D)
				killedStats.addDeaths();
			if (killedStats.getKillStreak() > killedStats.getBestKillStreak()) {
				killedStats.updateBestKillStreak();
			}
			killedAbility.remove();
			if (pm.hasUseSponsor())
				pm.setUseSponsor(false);
			if (pm.hasUseRecraft())
				pm.setUseRecraft(false);
			killed.eject();
		}
	}

	protected void launchWantedEvent(Player wanted, int kill) {
		this.wantedPlayer = wanted;
		Bukkit.broadcastMessage( "(WANTED) " + wanted.getName() + " is now wanted due to murder of " + kill + " people!");
		wanted.sendMessage("(WANTED) You are the wanted player!");
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
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Player player = event.getPlayer();
			if (!player.isDead() && player.getItemInHand().getType() == Material.MUSHROOM_SOUP && player.getHealth() < player.getMaxHealth()) {
				final Ability ability = PlayerManager.get(player.getUniqueId()).getAbility();
				final double newHealth = Math.min(player.getHealth() + 7.0D, player.getMaxHealth());
				player.setHealth(newHealth);
				//
				if (!ability.hasAbility(AbilitiesEnum.QUICKDROPPER)) {
					player.getItemInHand().setType(Material.BOWL);
				} else {
					player.getItemInHand().setAmount(0);
					player.getItemInHand().setType(null);
				}
				player.updateInventory();
			}
		}
	}

	@EventHandler
	public void onAbilitySelectorClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && player.getItemInHand().getType() == Material.BOOK && player.getItemInHand().getItemMeta().getDisplayName().toLowerCase().equals(ChatColor.GRAY + "your abilities")) {
			player.openInventory(CreateInventory.getInstance().loadKitsInventory(player));
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
		if (pm.getAbility().hasAbility()) {
			if (pm.getAbility().get().getSpecialItem().getType() == Material.MUSHROOM_SOUP) return;
			final ItemStack dropppedItem = event.getItemDrop().getItemStack();
			if (dropppedItem.getType() == pm.getAbility().get().getSpecialItem().getType()) {
				event.setCancelled(true);
				return;
			}
			if (dropppedItem.getType().toString().toLowerCase().contains("sword")) {
				int swords = 0;
				for (ItemStack item : player.getInventory().getContents()) {
					if (item.getType().toString().toLowerCase().contains("sword")) {
						swords++;
					}
				}
				if (swords == 1) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void nerfDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			ItemStack handItem = damager.getItemInHand();
			if (handItem.getType() == Material.MUSHROOM_SOUP) {
				return;
			}
			double damage = event.getDamage();
			if (handItem.getType() == Material.AIR && PlayerManager.get(damager.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.BOXER))
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
			Player player = (Player) event.getEntity();
			if (!PlayerManager.get(player.getUniqueId()).getAbility().hasAbility()) {
				event.setCancelled(true);
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
					player.teleport(player.getWorld().getSpawnLocation());
				}
			}
		}
	}

	@EventHandler
	public void onWantRefill(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENDER_CHEST && event.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.GLOWSTONE) {
			Player player = event.getPlayer();

			if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility()) {
				event.setCancelled(true);
				player.openInventory(CreateInventory.getInstance().loadRefillInventory(player));
			}
		}
	}

	@EventHandler
	public void onUseTracker(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && player.getItemInHand().getType() == Material.COMPASS && player.getItemInHand().getItemMeta().getDisplayName().toLowerCase().equals(ChatColor.YELLOW + "tracker")) {
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
			player.sendMessage(ChatColor.YELLOW + "Compass pointing at " + nearest.getName() + " (" + ChatColor.GOLD + (new DecimalFormat("#.#")).format(distance) + ChatColor.YELLOW + ")");
			player.setCompassTarget(nearest.getLocation());
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onWalkOnSponge(PlayerMoveEvent event) {
		Block block = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		
		if (block.getType() == Material.SPONGE) {
			final Player player = event.getPlayer();
			double boost = block.getRelative(BlockFace.DOWN).getType() == Material.SPONGE ? 2.5D : 2.15D;
			player.setVelocity(new Vector(0.0D, boost, 0.0D));
		}
	}
}
