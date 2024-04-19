package io.noks.kitpvp;

import java.util.Iterator;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.avaje.ebean.validation.NotNull;

import io.noks.kitpvp.commands.AbilityListCommand;
import io.noks.kitpvp.commands.BalanceCommand;
import io.noks.kitpvp.commands.BootCommand;
import io.noks.kitpvp.commands.BuildCommand;
import io.noks.kitpvp.commands.FeastCommand;
import io.noks.kitpvp.commands.PingCommand;
import io.noks.kitpvp.commands.ReportCommand;
import io.noks.kitpvp.commands.ShoutCommand;
import io.noks.kitpvp.commands.SkullCommand;
import io.noks.kitpvp.commands.SpawnCommand;
import io.noks.kitpvp.commands.SponsorCommand;
import io.noks.kitpvp.commands.StatisticCommand;
import io.noks.kitpvp.database.DBUtils;
import io.noks.kitpvp.listeners.InventoryListener;
import io.noks.kitpvp.listeners.PlayerListener;
import io.noks.kitpvp.listeners.ServerListener;
import io.noks.kitpvp.managers.AbilitiesManager;
import io.noks.kitpvp.managers.ConfigManager;
import io.noks.kitpvp.managers.InventoryManager;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.RefillInventoryManager;
import io.noks.kitpvp.managers.TournamentManager;
import io.noks.kitpvp.managers.caches.Feast;
import io.noks.kitpvp.task.event.EventsTask;
import io.noks.kitpvp.task.event.FallenGolemTask;
import io.noks.kitpvp.utils.ItemUtils;
import io.noks.kitpvp.utils.MathUtils;
import io.noks.kitpvp.utils.Messages;

public class Main extends JavaPlugin {
	private @NotNull ConfigManager configManager;
	private @NotNull DBUtils database;
	private @NotNull MathUtils mathUtils;
	private @NotNull AbilitiesManager abilitiesManager;
	private @NotNull ItemUtils itemUtils;
	private @NotNull InventoryManager inventoryManager;
	private @NotNull TournamentManager tournamentManager;
	private @NotNull Messages messages;
	private @NotNull EventsTask eventsTask;
	public @Nullable Feast feast = null;
	public @NotNull PlayerListener playerListener;
	
	private static Main instance;
	public static Main getInstance() {
		return instance;
	}

	public void onEnable() {
		instance = this;
		this.mathUtils = new MathUtils();
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		this.configManager = new ConfigManager(this);
		this.database = new DBUtils(getConfig().getString("DATABASE.ADDRESS"), getConfig().getString("DATABASE.NAME"), getConfig().getString("DATABASE.USER"), getConfig().getString("DATABASE.PASSWORD"));
		this.messages = new Messages(this.configManager.domainName);
		this.itemUtils = new ItemUtils();
		this.inventoryManager = new InventoryManager();
		this.abilitiesManager = new AbilitiesManager(this);
		this.registerScoreboard();
		this.registerListeners();
		this.registerCommands();
		this.eventsTask = new EventsTask(this); // TODO: need to execute it (see in class)
		this.getServer().newHologram(this.getServer().getWorld("world").getSpawnLocation(), "Hello World!");
		/*final World world = this.getServer().getWorld("world");
		final String fs = "ewogICJ0aW1lc3RhbXAiIDogMTY5NzQzMDY1NjUxOCwKICAicHJvZmlsZUlkIiA6ICIzNWIxMjg0OWYxYTY0YTc4YTM0ZTMyMzc5NjIxOGNmMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb2tzaW8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBmYjhkZmMyNTliZmIxYzBlZGIzNGFlYmNlMmVkZjQ2YmFjZWEzNTQ2ODllOTQ1ZDFkMDAwNWM5NWRhZmMwZCIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=";
		final String ss = "Edwcaev9uctdzc8q2r3SOfn4It5x735SNf4tHP7kZJ3hGY7F+Aa/9kXkMVAbBokWWkQ71ZdXbg7K4NRK0++gu4OJ8WBaPLYVA5GXzBpYS4XMChitweEkqoeV58Gb4F4FxOEdbRckwnGXEyCqC8lobLXhz1sOn7e5DC7DqX0GElS3dp4RpzV7jWrqVeMTw1dRZZ2Wfqr2rixf5zWEagJSPyzh9hqZPbp6wygZToLya9w+7jzfgM0QOD8M85N+awRuAU90VVcvB2TG4ekB6SjYqt+GC0YTD/Yoy2wY4R9Nf5v6Vc1M+VXITqtKQ2Ie5vsmJaDc0X+fkGZrFNj+5HPo5qXbW/BITGhwVRooOF5tI2xv+ecK0suj7XWRk34yu/eJSf2z4rNod6YZaaVjm6NCJtlOuklUC4aXScjIXdr1a5ag49TaWa/JC8yDdkmKCWTADKzrl0MotarIchUL6SZPZMCw+mO+yzW0qyYs5WlBaMo9B4p9MYRSba3+xtTd2m0ZIvty1JhVfUDOH+wQ35nDmemqThJh32DoWas761iiWhSfGnMIxmm/4oifP74jIk9KzrwDmlcZWAgKwsNVfpyb3DypQWWRwZdn/ntdVnFMjorISd+nIDauCV9lD6iZXgd+mH9eZaVjUa2FDyCs3S7bOjDELA6f3SmWzbhtRKCkOSQ=";
		world.spawnNPC(UUID.randomUUID(), "Storage", fs, ss, new Location(world, -19.5, 99.0D, 8.5D, -63.0F, 0.0F));
		world.spawnNPC(UUID.randomUUID(), "Battle Pass", fs, ss, new Location(world, -17.5, 99.0D, 5.5D, -49.0F, 0.0F));
		world.spawnNPC(UUID.randomUUID(), "Shop", fs, ss, new Location(world, -13.5, 99.0D, -0.5D, -54.0F, 0.0F));*/
	}

	public void onDisable() {
		if (this.playerListener.getMapTask() != null) {
			this.playerListener.getMapTask().clearTask();
		}
		final World world = Bukkit.getWorld("world");
		if (this.feast != null) {
			this.feast.clearFeast();
		}
		final Iterator<Entity> worldentities = world.getEntities().iterator();
		while (worldentities.hasNext()) {
			Entity entity = worldentities.next();
			
			if (entity == null || !(entity instanceof Item)) {
				continue;
			}
			entity.remove();
		}
		PlayerManager.players.clear();
		RefillInventoryManager.inventories.clear();
		if (this.database.getHikari() != null) {
			this.database.getHikari().close();
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
		this.playerListener = new PlayerListener(this);
		new ServerListener(this);
		new InventoryListener(this);
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
		getCommand("balance").setExecutor(new BalanceCommand());
		new BootCommand(this);
		new FeastCommand(this);
		new SpawnCommand(this);
	}
	
	public MathUtils getMathUtils() {
		return this.mathUtils;
	}
	
	public DBUtils getDataBase() {
		return this.database;
	}
	
	public AbilitiesManager getAbilitiesManager() {
		return this.abilitiesManager;
	}
	
	public ItemUtils getItemUtils() {
		return this.itemUtils;
	}
	
	public InventoryManager getInventoryManager() {
		return this.inventoryManager;
	}
	
	public TournamentManager getTournamentManager() {
		return this.tournamentManager;
	}
	
	public Messages getMessages() {
		return this.messages;
	}
	
	public EventsTask getEventsTask() {
		return this.eventsTask;
	}
	
	public ConfigManager getConfigManager() {
		return this.configManager;
	}
}
