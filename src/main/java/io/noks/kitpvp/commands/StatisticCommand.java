package io.noks.kitpvp.commands;

import org.bukkit.Bukkit;
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
			final Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(this.main.getMessages().PLAYER_NOT_ONLINE);
				return false;
			}
			final Stats stats = PlayerManager.get(target.getUniqueId()).getStats();
			sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.GRAY + "'s Statistics:");
			sender.sendMessage(stats.toStrings());
			return true;
		}
		final Player player = (Player) sender;
		final Stats stats = PlayerManager.get(player.getUniqueId()).getStats();
		sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.GRAY + "'s Statistics:");
		sender.sendMessage(stats.toStrings());
		return true;
	}
}
