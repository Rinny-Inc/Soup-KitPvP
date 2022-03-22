package us.noks.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import us.noks.kitpvp.commands.AbilityListCommand;
import us.noks.kitpvp.commands.BalanceCommand;
import us.noks.kitpvp.commands.BootCommand;
import us.noks.kitpvp.commands.BuildCommand;
import us.noks.kitpvp.commands.PingCommand;
import us.noks.kitpvp.commands.RecraftCommand;
import us.noks.kitpvp.commands.ReportCommand;
import us.noks.kitpvp.commands.ShoutCommand;
import us.noks.kitpvp.commands.SkullCommand;
import us.noks.kitpvp.commands.SponsorCommand;
import us.noks.kitpvp.commands.StatisticCommand;
import us.noks.kitpvp.database.DBUtils;
import us.noks.kitpvp.listeners.ChatListener;
import us.noks.kitpvp.listeners.InventoryListener;
import us.noks.kitpvp.listeners.PlayerListener;
import us.noks.kitpvp.listeners.ServerListener;
import us.noks.kitpvp.listeners.abilities.Anchor;
import us.noks.kitpvp.listeners.abilities.Archer;
import us.noks.kitpvp.listeners.abilities.Batman;
import us.noks.kitpvp.listeners.abilities.Blink;
import us.noks.kitpvp.listeners.abilities.Camel;
import us.noks.kitpvp.listeners.abilities.CookieMonster;
import us.noks.kitpvp.listeners.abilities.Endermage;
import us.noks.kitpvp.listeners.abilities.Fireman;
import us.noks.kitpvp.listeners.abilities.Fisherman;
import us.noks.kitpvp.listeners.abilities.Flash;
import us.noks.kitpvp.listeners.abilities.Ganjaman;
import us.noks.kitpvp.listeners.abilities.Gladiator;
import us.noks.kitpvp.listeners.abilities.Hulk;
import us.noks.kitpvp.listeners.abilities.Jellyfish;
import us.noks.kitpvp.listeners.abilities.Jumper;
import us.noks.kitpvp.listeners.abilities.Kangaroo;
import us.noks.kitpvp.listeners.abilities.Magma;
import us.noks.kitpvp.listeners.abilities.Monk;
import us.noks.kitpvp.listeners.abilities.Ninja;
import us.noks.kitpvp.listeners.abilities.Phantom;
import us.noks.kitpvp.listeners.abilities.Poseidon;
import us.noks.kitpvp.listeners.abilities.Reaper;
import us.noks.kitpvp.listeners.abilities.Snail;
import us.noks.kitpvp.listeners.abilities.Specialist;
import us.noks.kitpvp.listeners.abilities.Spectre;
import us.noks.kitpvp.listeners.abilities.Stomper;
import us.noks.kitpvp.listeners.abilities.Switcher;
import us.noks.kitpvp.listeners.abilities.TimeLord;
import us.noks.kitpvp.listeners.abilities.Turtle;
import us.noks.kitpvp.listeners.abilities.Viper;
import us.noks.kitpvp.listeners.abilities.Zeus;
import us.noks.kitpvp.managers.InventoryManager;
import us.noks.kitpvp.managers.PlayerManager;
import us.noks.kitpvp.tasked.FallenGolemTask;
import us.noks.kitpvp.tasked.FeastTask;

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
		new ChatListener(this);

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
