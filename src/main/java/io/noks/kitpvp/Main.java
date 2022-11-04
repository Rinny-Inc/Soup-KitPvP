package io.noks.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import io.noks.kitpvp.commands.AbilityListCommand;
import io.noks.kitpvp.commands.BalanceCommand;
import io.noks.kitpvp.commands.BootCommand;
import io.noks.kitpvp.commands.BuildCommand;
import io.noks.kitpvp.commands.PingCommand;
import io.noks.kitpvp.commands.RecraftCommand;
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
import io.noks.kitpvp.tasked.FallenGolemTask;
import io.noks.kitpvp.tasked.FeastTask;

public class Main extends JavaPlugin {
	private DBUtils database;
	private AbilitiesManager abilitiesManager;
	
	private static Main instance;
	public static Main getInstance() {
		return instance;
	}

	public void onEnable() {
		instance = this;
		this.database = new DBUtils(getConfig().getString("DATABASE.ADDRESS"), getConfig().getString("DATABASE.NAME"), getConfig().getString("DATABASE.USER"), getConfig().getString("DATABASE.PASSWORD"));
		this.abilitiesManager = new AbilitiesManager(this);
		
		try {
			FeastTask.getInstance().doFeast();

		} catch (Exception exception) {
		}
		registerListeners();
		registerCommands();
	}

	public void onDisable() {
		for (Entity entity : Bukkit.getWorld("world").getEntities()) {
			if (entity instanceof org.bukkit.entity.IronGolem) {
				entity.remove();
			}
		}
		PlayerManager.players.clear();
		InventoryManager.inventories.clear();
		if (this.database.getHikari() != null) {
			this.database.getHikari().close();
		}
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
		getCommand("stats").setExecutor(new StatisticCommand());
		getCommand("recraft").setExecutor(new RecraftCommand());
		getCommand("build").setExecutor(new BuildCommand());
		getCommand("boot").setExecutor(new BootCommand());
		getCommand("balance").setExecutor(new BalanceCommand());
	}
	
	public DBUtils getDataBase() {
		return this.database;
	}
	
	public AbilitiesManager getAbilitiesManager() {
		return this.abilitiesManager;
	}
}
