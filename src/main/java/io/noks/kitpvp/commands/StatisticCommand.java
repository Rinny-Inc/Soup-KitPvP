package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Stats;

public class StatisticCommand implements CommandExecutor {
	private Main main;
	public StatisticCommand(Main main) {
		this.main = main;
		main.getCommand("stats").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /stats <player>");
			return false;
		}

		if (args.length == 1) {
			final String name = args[0];
			Stats stats = PlayerManager.get(this.main.getServer().getPlayer(name).getUniqueId()).getStats();
			if (stats == null && (stats = this.main.getDataBase().getOfflinePlayerStats(name)) == null) {
				sender.sendMessage(this.main.getMessages().PLAYER_NOT_EXIST);
				return false;
			}
			sender.sendMessage(ChatColor.GOLD + name + ChatColor.GRAY + "'s Statistics:");
			sender.sendMessage(stats.toString());
			return true;
		}
		final Player player = (Player) sender;
		final Stats stats = PlayerManager.get(player.getUniqueId()).getStats();
		sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.GRAY + "'s Statistics:");
		sender.sendMessage(stats.toString());
		return true;
	}
}
