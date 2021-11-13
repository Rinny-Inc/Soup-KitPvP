package us.noks.kitpvp.listeners;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
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
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

import us.noks.kitpvp.Main;
import us.noks.kitpvp.managers.PlayerManager;
import us.noks.kitpvp.utils.ItemUtils;
import us.noks.kitpvp.utils.MathUtils;

public class ServerListener implements Listener {
	private Main plugin;
	private Map<Location, Long> blockCooldown;

	public ServerListener(Main main) {
		this.blockCooldown = Maps.newConcurrentMap();

		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		String line1 = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GRAY
				+ ChatColor.BOLD + "( " + ChatColor.RED + ChatColor.BOLD + "SoupZone " + ChatColor.GRAY + ChatColor.BOLD
				+ ")" + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------\n";
		String line2 = ChatColor.DARK_AQUA.toString() + ChatColor.ITALIC + "- Home of soup pvp -";
		event.setMotd(line1 + line2 + (Bukkit.hasWhitelist() ? (ChatColor.RED + " Whitelisted") : ""));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRain(WeatherChangeEvent event) {
		if (event.toWeatherState())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		PlayerManager pm = PlayerManager.get(player.getUniqueId());

		if (pm.isAllowBuild()) {
			return;
		}
		Block block = event.getBlock();
		if (block.getType() == Material.FIRE) {
			return;
		}
		event.setCancelled(true);
		if (!pm.getAbility().hasAbility()) {
			return;
		}
		if (block.getType() == Material.BROWN_MUSHROOM || block.getType() == Material.RED_MUSHROOM
				|| block.getType() == Material.LOG) {
			Location location = event.getBlock().getLocation();
			if (isBlockCooldownActive(location)) {
				double time = getBlockCooldown(location).longValue() / 1000.0D;
				player.sendMessage(ChatColor.RED + "This "
						+ WordUtils.capitalizeFully(block.getType().toString().replaceAll("_", " ")) + " respawn in "
						+ (new DecimalFormat("#.#")).format(time) + " seconds.");
				return;
			}
			setBlockCooldown(location, ((new Random()).nextInt(5) + 25));
			block.getWorld().dropItem(location,
					ItemUtils.getInstance().getItemMaterial(
							(block.getType() == Material.LOG) ? Material.BOWL : block.getType(),
							(new Random()).nextInt(3) + 1));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		PlayerManager pm = PlayerManager.get(player.getUniqueId());

		if (pm.isAllowBuild()) {
			return;
		}
		Block block = event.getBlock();
		if (block.getType() == Material.FIRE) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFireSpread(BlockSpreadEvent event) {
		if (event.getSource().getType() == Material.FIRE) {
			event.setCancelled(true);
			event.getSource().setType(Material.AIR);
		}
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
	public void onPortalTook(PlayerPortalEvent event) {
		event.setCancelled(true);
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
			event.getPlayer().sendMessage(ChatColor.RED + "No event in progress!");
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFoodChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
			Player player = (Player) event.getEntity();
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
		Random random = new Random();

		ItemStack[] items = { new ItemStack(Material.MUSHROOM_SOUP, MathUtils.getInstance().getRandom(3, 6)),
				new ItemStack(Material.LEATHER_BOOTS),
				new ItemStack(Material.BROWN_MUSHROOM, MathUtils.getInstance().getRandom(2, 9)),
				new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE),
				new ItemStack(Material.RED_MUSHROOM, MathUtils.getInstance().getRandom(2, 9)),
				new ItemStack(Material.LEATHER_HELMET),
				new ItemStack(Material.BOWL, MathUtils.getInstance().getRandom(3, 12)),
				new ItemStack(Material.GOLDEN_APPLE, MathUtils.getInstance().getRandom(1, 2)),
				new ItemStack(Material.POTION, 1, (short) 16386),
				ItemUtils.getInstance().getItemUnbreakable(Material.STONE_SWORD),
				ItemUtils.getInstance().getItemUnbreakable(Material.IRON_SWORD) };

		Random r = new Random();
		int rand = r.nextInt(4);
		for (int i = 0; i < 4 + rand; i++) {
			chest.getInventory().setItem((new Random()).nextInt(chest.getInventory().getSize()),
					new ItemStack(items[random.nextInt(items.length)]));
		}
	}

	@EventHandler
	public void onSponsorTouchGround(EntityChangeBlockEvent event) {
		if (event.getEntity() instanceof FallingBlock) {
			FallingBlock falling = (FallingBlock) event.getEntity();
			if (falling.getMaterial() == Material.CHEST) {
				event.setCancelled(true);
				event.getBlock().setType(Material.CHEST);
				final Chest sponsor = (Chest) event.getBlock().getState();
				fillSponsor(sponsor);

				(new BukkitRunnable() {
					public void run() {
						sponsor.getBlock().setType(Material.AIR);
					}
				}).runTaskLater(Main.getInstance(), 200L);
			}
		}
	}

	private Long getBlockCooldown(Location location) {
		if (this.blockCooldown.containsKey(location))
			return Long.valueOf(
					Math.max(0L, ((Long) this.blockCooldown.get(location)).longValue() - System.currentTimeMillis()));
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
}
