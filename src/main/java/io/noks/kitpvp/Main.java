package io.noks.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

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
import io.noks.kitpvp.managers.InventoryManager;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.RefillInventoryManager;
import io.noks.kitpvp.managers.caches.Tournament;
import io.noks.kitpvp.task.event.FallenGolemTask;
import io.noks.kitpvp.task.event.FeastTask;
import io.noks.kitpvp.utils.ItemUtils;
import io.noks.kitpvp.utils.MathUtils;
import io.noks.kitpvp.utils.Messages;

public class Main extends JavaPlugin {
	private DBUtils database;
	private MathUtils mathUtils;
	private AbilitiesManager abilitiesManager;
	private ItemUtils itemUtils;
	private InventoryManager inventoryManager;
	private Tournament tournament;
	private Messages messages;
	
	private static Main instance;
	public static Main getInstance() {
		return instance;
	}

	public void onEnable() {
		instance = this;
		this.mathUtils = new MathUtils();
		this.database = new DBUtils(getConfig().getString("DATABASE.ADDRESS"), getConfig().getString("DATABASE.NAME"), getConfig().getString("DATABASE.USER"), getConfig().getString("DATABASE.PASSWORD"));
		this.messages = new Messages();
		this.itemUtils = new ItemUtils();
		this.inventoryManager = new InventoryManager();
		this.abilitiesManager = new AbilitiesManager(this);
		try {
			FeastTask.getInstance().doFeast();
		} catch (Exception exception) {
		}
		this.registerScoreboard();
		this.registerListeners();
		this.registerCommands();
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
			life.setDisplayName(ChatColor.RED + "❤");
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
		getCommand("report").setExecutor(new ReportCommand());
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
	
	public boolean isTournamentActive() {
		return this.tournament != null;
	}
	
	public Tournament getActiveTournament() {
		return this.tournament;
	}
	
	public void setActiveTournament(Tournament tournament) {
		this.tournament = tournament;
	}
	
	public Messages getMessages() {
		return this.messages;
	}
}
