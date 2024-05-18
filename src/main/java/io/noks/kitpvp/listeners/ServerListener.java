package io.noks.kitpvp.listeners;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.managers.PlayerManager;

public class ServerListener implements Listener {
	private Main plugin;
	private final Map<Location, Long> blockCooldown;

	public ServerListener(Main main) {
		this.blockCooldown = Maps.newConcurrentMap();

		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		final String line1 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().motdFirstLine) + "\n";
		final String line2 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().motdSecondLine);
		event.setMotd(line1 + line2 /*+ (Bukkit.hasWhitelist() ? (ChatColor.RED + " Whitelisted") : "")*/);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRain(WeatherChangeEvent event) {
		if (event.toWeatherState())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
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
		if (!pm.hasAbility()) {
			return;
		}
		if (block.getType() == Material.BROWN_MUSHROOM || block.getType() == Material.RED_MUSHROOM || block.getType() == Material.LOG) {
			final Location location = event.getBlock().getLocation();
			if (isBlockCooldownActive(location)) {
				double time = getBlockCooldown(location).longValue() / 1000.0D;
				player.sendMessage(ChatColor.RED + "This " + WordUtils.capitalizeFully(block.getType().toString().replaceAll("_", " ")) + " respawn in " + (new DecimalFormat("#.#")).format(time) + " seconds.");
				return;
			}
			setBlockCooldown(location, ((new Random()).nextInt(5) + 25));
			block.getWorld().dropItem(location, this.plugin.getItemUtils().getItemMaterial((block.getType() == Material.LOG) ? Material.BOWL : block.getType(), (new Random()).nextInt(3) + 1));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		event.blockList().clear();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		if (event.getInventory().getResult().getType() != Material.MUSHROOM_SOUP) {
			event.getInventory().setResult(null);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFoodChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
			final Player player = (Player) event.getEntity();
			player.setSaturation(10000.0F);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
			event.setCancelled(true);
		}
	}

	private void fillSponsor(Chest chest) {
		final ItemStack lighter = new ItemStack(Material.FLINT_AND_STEEL, 1);
		lighter.setDurability((short) this.plugin.getMathUtils().getRandom(59, 63));
		final ItemStack[] items = { new ItemStack(Material.MUSHROOM_SOUP, this.plugin.getMathUtils().getRandom(3, 6)),
				new ItemStack(Material.LEATHER_BOOTS),
				new ItemStack(Material.BROWN_MUSHROOM, this.plugin.getMathUtils().getRandom(2, 9)),
				new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE),
				new ItemStack(Material.RED_MUSHROOM, this.plugin.getMathUtils().getRandom(2, 9)),
				new ItemStack(Material.LEATHER_HELMET),
				new ItemStack(Material.BOWL, this.plugin.getMathUtils().getRandom(3, 12)),
				new ItemStack(Material.GOLDEN_APPLE, this.plugin.getMathUtils().getRandom(1, 2)),
				new ItemStack(Material.POTION, 1, (short) 16386),
				this.plugin.getItemUtils().getItemUnbreakable(Material.STONE_SWORD),
				this.plugin.getItemUtils().getItemUnbreakable(Material.IRON_SWORD), lighter,
				new ItemStack(Material.EXP_BOTTLE, this.plugin.getMathUtils().getRandom(1, 3))};

		final Random random = new Random();
		final int rand = random.nextInt(4);
		for (int i = 0; i < 4 + rand; i++) {
			chest.getInventory().setItem((new Random()).nextInt(chest.getInventory().getSize()), new ItemStack(items[random.nextInt(items.length)]));
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
				fillSponsor(sponsor);

				new BukkitRunnable() {
					public void run() {
						sponsor.getBlock().setType(Material.AIR);
					}
				}.runTaskLater(this.plugin, 200L);
			}
		}
	}

	private Long getBlockCooldown(Location location) {
		if (this.blockCooldown.containsKey(location))
			return Long.valueOf(Math.max(0L, ((Long) this.blockCooldown.get(location)).longValue() - System.currentTimeMillis()));
		return Long.valueOf(0L);
	}

	private void setBlockCooldown(Location location, long cooldown) {
		this.blockCooldown.put(location, Long.valueOf(System.currentTimeMillis() + cooldown * 1000L));
	}

	private boolean isBlockCooldownActive(Location location) {
		if (!this.blockCooldown.containsKey(location))
			return false;
		return (((Long) this.blockCooldown.get(location)).longValue() > System.currentTimeMillis());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.getCause() == IgniteCause.FLINT_AND_STEEL) {
			return;
		}
		event.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockSpread(BlockSpreadEvent event) {
		if (event.getSource().getType() != Material.VINE) {
			return;
		}
		event.setCancelled(true);
	}
	
	@EventHandler
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
	
	/*@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		final Entity entity = event.getEntity();
		for (Player player : event.getEntity().getWorld().getPlayers()) {
			if (!(entity instanceof Item)) return;
			final Item item = (Item) entity;
			if (!(item.getOwner() instanceof Player)) return;
			final Player owner = (Player) item.getOwner();
			if (!player.canSee(owner)) continue;
			player.playEffect(entity.getLocation().add(0, 0.2, 0), Effect.SMOKE, null);
		}
	}*/
}
