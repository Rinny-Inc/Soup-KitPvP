package io.noks.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import io.noks.kitpvp.commands.PingCommand;
import io.noks.kitpvp.commands.ReportCommand;
import io.noks.kitpvp.commands.ShoutCommand;
import io.noks.kitpvp.commands.SkullCommand;
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
	
	private static Main instance;
	public static Main getInstance() {
		return instance;
	}

	public void onEnable() {
		instance = this;
		this.mathUtils = new MathUtils();
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
	}

	public void onDisable() {
		for (Entity entity : Bukkit.getWorld("world").getEntities()) {
			if (entity instanceof org.bukkit.entity.IronGolem || entity instanceof Item) {
				entity.remove();
			}
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
		new PlayerListener(this);
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
