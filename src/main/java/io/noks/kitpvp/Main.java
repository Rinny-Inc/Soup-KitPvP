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
import io.noks.kitpvp.listeners.abilities.Anchor;
import io.noks.kitpvp.listeners.abilities.Archer;
import io.noks.kitpvp.listeners.abilities.Batman;
import io.noks.kitpvp.listeners.abilities.Blink;
import io.noks.kitpvp.listeners.abilities.Camel;
import io.noks.kitpvp.listeners.abilities.CookieMonster;
import io.noks.kitpvp.listeners.abilities.Endermage;
import io.noks.kitpvp.listeners.abilities.Fireman;
import io.noks.kitpvp.listeners.abilities.Fisherman;
import io.noks.kitpvp.listeners.abilities.Flash;
import io.noks.kitpvp.listeners.abilities.Ganjaman;
import io.noks.kitpvp.listeners.abilities.Gladiator;
import io.noks.kitpvp.listeners.abilities.Hulk;
import io.noks.kitpvp.listeners.abilities.Jellyfish;
import io.noks.kitpvp.listeners.abilities.Jumper;
import io.noks.kitpvp.listeners.abilities.Kangaroo;
import io.noks.kitpvp.listeners.abilities.Magma;
import io.noks.kitpvp.listeners.abilities.Monk;
import io.noks.kitpvp.listeners.abilities.Ninja;
import io.noks.kitpvp.listeners.abilities.Phantom;
import io.noks.kitpvp.listeners.abilities.Poseidon;
import io.noks.kitpvp.listeners.abilities.Reaper;
import io.noks.kitpvp.listeners.abilities.Snail;
import io.noks.kitpvp.listeners.abilities.Specialist;
import io.noks.kitpvp.listeners.abilities.Spectre;
import io.noks.kitpvp.listeners.abilities.Stomper;
import io.noks.kitpvp.listeners.abilities.Switcher;
import io.noks.kitpvp.listeners.abilities.TimeLord;
import io.noks.kitpvp.listeners.abilities.Turtle;
import io.noks.kitpvp.listeners.abilities.Viper;
import io.noks.kitpvp.listeners.abilities.Zeus;
import io.noks.kitpvp.managers.InventoryManager;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.tasked.FallenGolemTask;
import io.noks.kitpvp.tasked.FeastTask;

public class Main extends JavaPlugin {
	private static Main instance;

	public static Main getInstance() {
		return instance;
	}

	public void onEnable() {
		instance = this;
		//DBUtils.getInstance().connectDatabase();

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
		if (DBUtils.getInstance().getHikari() != null) {
			DBUtils.getInstance().getHikari().close();
		}
	}

	private void registerListeners() {
		new PlayerListener(this);
		new ServerListener(this);
		new InventoryListener(this);

		new Anchor(this);
		new Archer(this);
		new Blink(this);
		new Camel(this);
		new Fireman(this);
		new Fisherman(this);
		new Gladiator(this);
		new Hulk(this);
		new Kangaroo(this);
		new Ninja(this);
		new Phantom(this);
		new Poseidon(this);
		new Reaper(this);
		new Snail(this);
		new Spectre(this);
		new Stomper(this);
		new Switcher(this);
		new TimeLord(this);
		new Turtle(this);
		new Viper(this);
		new Zeus(this);
		new Jumper(this);
		new Specialist(this);
		new CookieMonster(this);
		new Magma(this);
		new Monk(this);
		new Endermage(this);
		new Batman(this);
		new Jellyfish(this);
		new Flash(this);
		new Ganjaman(this);
		new Flash(this);
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
}
