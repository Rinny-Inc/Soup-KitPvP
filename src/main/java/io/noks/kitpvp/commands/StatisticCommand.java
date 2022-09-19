package io.noks.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Stats;
import io.noks.kitpvp.utils.Messages;

public class StatisticCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /stats <player>");
			return false;
		}

		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(Messages.PLAYER_NOT_ONLINE);
				return false;
			}
			Stats stats = PlayerManager.get(target.getUniqueId()).getStats();
			sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.GRAY + "'s Statistics:");
			sender.sendMessage(stats.toStrings());
			return true;
		}
		Player player = (Player) sender;
		Stats stats = PlayerManager.get(player.getUniqueId()).getStats();
		sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.GRAY + "'s Statistics:");
		sender.sendMessage(stats.toStrings());
		return true;
	}
}
