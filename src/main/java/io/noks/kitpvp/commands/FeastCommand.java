package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.managers.caches.Feast;

public class FeastCommand implements CommandExecutor {
	private final Main main;
	
	public FeastCommand(Main main) {
		this.main = main;
		main.getCommand("feast").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		if (!sender.hasPermission("command.feast")) {
			sender.sendMessage(this.main.getMessages().NO_PERMISSION);
			return false;
		}
		if (args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /feast");
			return false;
		}
		if (this.main.feast != null) {
			sender.sendMessage(ChatColor.RED + "There's currently a feast! Try again later.");
			return false;
		}
		final Player player = (Player) sender;
		this.main.feast = new Feast(player.getLocation());
		new BukkitRunnable() {
			
			@Override
			public void run() {
				FeastCommand.this.main.feast.clearFeast();
				FeastCommand.this.main.feast = null;
			}
		}.runTaskLater(this.main, 2400L);
		return false;
	}
}
