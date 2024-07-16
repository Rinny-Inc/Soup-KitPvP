package io.noks.kitpvp;

import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.avaje.ebean.validation.NotNull;

import io.noks.Hologram;
import io.noks.kitpvp.abstracts.AbstractBossTask;
import io.noks.kitpvp.commands.AbilityListCommand;
import io.noks.kitpvp.commands.BootCommand;
import io.noks.kitpvp.commands.BuildCommand;
import io.noks.kitpvp.commands.EconomyCommand;
import io.noks.kitpvp.commands.FeastCommand;
import io.noks.kitpvp.commands.GuildCommand;
import io.noks.kitpvp.commands.PingCommand;
import io.noks.kitpvp.commands.RepairCommand;
import io.noks.kitpvp.commands.ReportCommand;
import io.noks.kitpvp.commands.ShopCommand;
import io.noks.kitpvp.commands.ShoutCommand;
import io.noks.kitpvp.commands.SkullCommand;
import io.noks.kitpvp.commands.SocialCommand;
import io.noks.kitpvp.commands.SpawnCommand;
import io.noks.kitpvp.commands.SponsorCommand;
import io.noks.kitpvp.commands.StatisticCommand;
import io.noks.kitpvp.database.DBUtils;
import io.noks.kitpvp.interfaces.ItemHelper;
import io.noks.kitpvp.interfaces.TournamentManager;
import io.noks.kitpvp.listeners.AbilityListener;
import io.noks.kitpvp.listeners.InventoryListener;
import io.noks.kitpvp.listeners.PlayerListener;
import io.noks.kitpvp.listeners.ServerListener;
import io.noks.kitpvp.managers.AbilitiesManager;
import io.noks.kitpvp.managers.ConfigManager;
import io.noks.kitpvp.managers.HologramManager;
import io.noks.kitpvp.managers.InventoryManager;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.RefillInventoryManager;
import io.noks.kitpvp.managers.caches.Feast;
import io.noks.kitpvp.managers.caches.Guild;
import io.noks.kitpvp.managers.caches.Tournament;
import io.noks.kitpvp.task.MapTask;
import io.noks.kitpvp.task.event.FallenGolemTask;
import io.noks.kitpvp.utils.Cuboid;
import io.noks.kitpvp.utils.Messages;

public class Main extends JavaPlugin implements TournamentManager, ItemHelper /*<-- REMOVE IT FROM MAIN AND ADD IT WHERE NEEDED */ {
	private @NotNull ConfigManager configManager;
	private @NotNull DBUtils database;
	private @NotNull AbilitiesManager abilitiesManager;
	private @NotNull InventoryManager inventoryManager;
	private @NotNull Messages messages;
	private @NotNull AbstractBossTask abstractBossTask;
	public @Nullable Feast feast = null;
	public @NotNull HologramManager hologramManager;
	private @NotNull Cuboid spawnCuboid;
	public @Nullable MapTask mapTask = null;
	private @Nullable Tournament tournament;
	
	private static Main instance;
	public static Main getInstance() {
		return instance;
	}

	public void onEnable() {
		instance = this;
		final World world = this.getServer().getWorld("world");
		this.spawnCuboid = new Cuboid(new Location(world, -34, 96, 31), new Location(world, 23, 106, -15));
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		this.configManager = new ConfigManager(this);
		this.database = new DBUtils(getConfig().getString("DATABASE.ADDRESS"), getConfig().getString("DATABASE.NAME"), getConfig().getString("DATABASE.USER"), getConfig().getString("DATABASE.PASSWORD"), this);
		this.messages = new Messages(this.configManager.domainName);
		this.inventoryManager = new InventoryManager();
		this.abilitiesManager = new AbilitiesManager(this);
		this.registerScoreboard();
		this.registerListeners();
		this.registerCommands();
		this.hologramManager = new HologramManager();
		final Hologram parent = this.getServer().newHologram(new Location(world, 5.5, 102, -5.5), ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Active Event");
		parent.addLineBelow(ChatColor.RED + "Coming Soon :)");
		this.spawnNPC(world);
	}

	public void onDisable() {
		final World world = Bukkit.getWorld("world");
		if (this.feast != null) {
			this.feast.clearFeast();
		}
		final Iterator<Entity> worldentities = world.getEntities().iterator();
		while (worldentities.hasNext()) {
			Entity entity = worldentities.next();
			
			if (entity == null) {
				continue;
			}
			entity.remove();
		}
		this.getServer().getScheduler().cancelAllTasks();
		// TODO: are players kicked before the firing of onDisable?!?
		PlayerManager.players.clear();
		if (!RefillInventoryManager.inventories.isEmpty()) {
			for (RefillInventoryManager invs : RefillInventoryManager.inventories) {
				Block blockAbove = invs.getLocation().getBlock().getRelative(BlockFace.UP);
				if (blockAbove.getData() != (byte)5) {
					blockAbove.setTypeIdAndData(35, (byte)5, false);
				}
			}
			RefillInventoryManager.inventories.clear();
		}
		// TODO: are players kicked before the firing of onDisable?!?
		// TODO update guilds DB info before clearing
		Guild.guildList.clear();
		if (RefillInventoryManager.cooldownTask != null) {
			RefillInventoryManager.cooldownTask = null;
		}
		if (this.database.getHikari() != null) {
			this.database.close();
		}
	}
	
	private void registerScoreboard() {
		final Scoreboard board = this.getServer().getScoreboardManager().getMainScoreboard();
		if (board.getObjective("life") == null) {
			final Objective life = board.registerNewObjective("life", "health");
			life.setDisplaySlot(DisplaySlot.BELOW_NAME);
			final char heart = '\u2764';
			life.setDisplayName(ChatColor.RED.toString() + heart);
		}
		/*if (board.getObjective("bounty") == null) {
			final Objective life = board.registerNewObjective("bounty", "health");
			life.setDisplaySlot(DisplaySlot.BELOW_NAME);
			life.setDisplayName(ChatColor.YELLOW + "Bounty:");
		}*/
	}
	
	private void registerListeners() {
		new PlayerListener(this);
		new ServerListener(this);
		new InventoryListener(this);
		new AbilityListener(this);
		getServer().getPluginManager().registerEvents(new FallenGolemTask(), this);
	}

	private void registerCommands() {
		getCommand("abilitylist").setExecutor(new AbilityListCommand());
		getCommand("ping").setExecutor(new PingCommand());
		getCommand("shout").setExecutor(new ShoutCommand());
		new ReportCommand(this);
		getCommand("skull").setExecutor(new SkullCommand());
		getCommand("sponsor").setExecutor(new SponsorCommand());
		new StatisticCommand(this);
		new BuildCommand(this);
		new EconomyCommand(this);
		new BootCommand(this);
		new FeastCommand(this);
		new SpawnCommand(this);
		getCommand("repair").setExecutor(new RepairCommand());
		getCommand("shop").setExecutor(new ShopCommand());
		getCommand("discord").setExecutor(new SocialCommand());
		new GuildCommand(this);
	}
	
	private void spawnNPC(World world) {
		final String fs = "ewogICJ0aW1lc3RhbXAiIDogMTY5NzQzMDY1NjUxOCwKICAicHJvZmlsZUlkIiA6ICIzNWIxMjg0OWYxYTY0YTc4YTM0ZTMyMzc5NjIxOGNmMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb2tzaW8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBmYjhkZmMyNTliZmIxYzBlZGIzNGFlYmNlMmVkZjQ2YmFjZWEzNTQ2ODllOTQ1ZDFkMDAwNWM5NWRhZmMwZCIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=";
		final String ss = "Edwcaev9uctdzc8q2r3SOfn4It5x735SNf4tHP7kZJ3hGY7F+Aa/9kXkMVAbBokWWkQ71ZdXbg7K4NRK0++gu4OJ8WBaPLYVA5GXzBpYS4XMChitweEkqoeV58Gb4F4FxOEdbRckwnGXEyCqC8lobLXhz1sOn7e5DC7DqX0GElS3dp4RpzV7jWrqVeMTw1dRZZ2Wfqr2rixf5zWEagJSPyzh9hqZPbp6wygZToLya9w+7jzfgM0QOD8M85N+awRuAU90VVcvB2TG4ekB6SjYqt+GC0YTD/Yoy2wY4R9Nf5v6Vc1M+VXITqtKQ2Ie5vsmJaDc0X+fkGZrFNj+5HPo5qXbW/BITGhwVRooOF5tI2xv+ecK0suj7XWRk34yu/eJSf2z4rNod6YZaaVjm6NCJtlOuklUC4aXScjIXdr1a5ag49TaWa/JC8yDdkmKCWTADKzrl0MotarIchUL6SZPZMCw+mO+yzW0qyYs5WlBaMo9B4p9MYRSba3+xtTd2m0ZIvty1JhVfUDOH+wQ35nDmemqThJh32DoWas761iiWhSfGnMIxmm/4oifP74jIk9KzrwDmlcZWAgKwsNVfpyb3DypQWWRwZdn/ntdVnFMjorISd+nIDauCV9lD6iZXgd+mH9eZaVjUa2FDyCs3S7bOjDELA6f3SmWzbhtRKCkOSQ=";
		world.spawnNPC(UUID.randomUUID(), ChatColor.GREEN + "Shop", fs, ss, new Location(world, -13.5, 99.0D, -0.5D, -54.0F, 0.0F));
		world.spawnNPC(UUID.randomUUID(), ChatColor.YELLOW + "Storage", fs, ss, new Location(world, -19.5, 99.0D, 8.5D, -63.0F, 0.0F));
		world.spawnNPC(UUID.randomUUID(), ChatColor.RED + "Battle Pass", fs, ss, new Location(world, -17.5, 99.0D, 5.5D, -49.0F, 0.0F));
	}
	
	public DBUtils getDataBase() {
		return this.database;
	}
	
	public AbilitiesManager getAbilitiesManager() {
		return this.abilitiesManager;
	}
	
	public InventoryManager getInventoryManager() {
		return this.inventoryManager;
	}
	
	public Messages getMessages() {
		return this.messages;
	}
	
	public AbstractBossTask getEventsTask() {
		return this.abstractBossTask;
	}
	
	public ConfigManager getConfigManager() {
		return this.configManager;
	}
	
	public HologramManager getHologramManager() {
		return this.hologramManager;
	}
	
	public Cuboid spawnCuboid() {
		return this.spawnCuboid;
	}
	
	public void applySpawnProtection(Player player, boolean remove) {
		for (Location loc : spawnCuboid.getEdgeLocations()) {
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
		if (mapTask == null && !remove) {
			mapTask = new MapTask(this).startTask();
			return;
		}
		if (mapTask != null) {
			PlayerManager pm = PlayerManager.get(player.getUniqueId());
			if (!remove) {
				if (!mapTask.playersInMap.contains(pm)) {
					mapTask.playersInMap.add(pm);
				}
				return;
			}
			if (mapTask.playersInMap.contains(pm)) {
				mapTask.playersInMap.remove(pm);
			}
			if (mapTask.playersInMap.isEmpty()) {
				this.mapTask.clearTask();
				this.mapTask = null;
			}
		}
	}
	
	@Override
	public boolean isTournamentActive() {
		return this.tournament != null;
	}
	
	@Override
	public Tournament getActiveTournament() {
		return this.tournament;
	}
	
	@Override
	public void setActiveTournament(Tournament tournament) {
		this.tournament = tournament;
	}
}
