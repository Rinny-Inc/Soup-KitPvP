package io.noks.kitpvp.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.interfaces.Fillable;
import io.noks.kitpvp.managers.PlayerManager;

public class ServerListener implements Listener, Fillable {
	private Main plugin;

	public ServerListener(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		final String line1 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().motdFirstLine) + "\n";
		final String line2 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().motdSecondLine);
		event.setMotd(line1 + line2 /*+ (Bukkit.hasWhitelist() ? (ChatColor.RED + " Whitelisted") : "")*/);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRain(WeatherChangeEvent event) {
		if (event.toWeatherState())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());

		if (pm.isAllowBuild()) {
			return;
		}
		final Block block = event.getBlock();
		if (block.getType() == Material.FIRE) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());

		if (pm.isAllowBuild()) {
			return;
		}
		final Block block = event.getBlock();
		if (block.getType() == Material.FIRE) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		event.blockList().clear();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		if (event.getInventory().getResult().getType() != Material.MUSHROOM_SOUP) {
			event.getInventory().setResult(null);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFoodChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
			final Player player = (Player) event.getEntity();
			player.setSaturation(10000.0F);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSponsorTouchGround(EntityChangeBlockEvent event) {
		if (event.getEntity() instanceof FallingBlock) {
			final FallingBlock falling = (FallingBlock) event.getEntity();
			if (falling.getMaterial() == Material.CHEST) {
				event.setCancelled(true);
				event.getBlock().setType(Material.CHEST);
				final Chest sponsor = (Chest) event.getBlock().getState();
				this.fill(sponsor);

				new BukkitRunnable() {
					public void run() {
						sponsor.getBlock().setType(Material.AIR);
					}
				}.runTaskLater(this.plugin, 200L);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.getCause() == IgniteCause.FLINT_AND_STEEL) {
			return;
		}
		event.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent event) {
		if (event.getSource().getType() != Material.VINE) {
			return;
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onTntIgnite(BlockIgniteEvent event) {
		if (event.getCause() == IgniteCause.FLINT_AND_STEEL) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		final String[] lines = event.getLines();
		if (lines[0].equals("fs")) {
			event.setLine(0, null);
			event.setLine(1, ChatColor.DARK_AQUA + "[Free Soup]");
			if (lines[2] != null) {
				event.setLine(2, null);
			}
			if (lines[3] != null) {
				event.setLine(3, null);
			}
		}
	}
}
