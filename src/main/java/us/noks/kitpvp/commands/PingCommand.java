package us.noks.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /ping <player>");
			return false;
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			player.sendMessage(ChatColor.GRAY + "Your ping: " + ChatColor.RED + player.getPing() + "ms");
			return true;
		}
		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);

			if (target == null) {
				player.sendMessage(ChatColor.RED + "This player is not online.");
				return false;
			}
			if (target == player) {
				player.sendMessage(ChatColor.GRAY + "Your ping: " + ChatColor.RED + player.getPing() + "ms");
				return true;
			}
			player.sendMessage(ChatColor.GREEN + target.getName() + ChatColor.GRAY + "'s ping: " + ChatColor.RED + target.getPing() + "ms");
			player.sendMessage(ChatColor.GRAY + "Ping difference: " + ChatColor.RED + Math.abs(player.getPing() - target.getPing()) + "ms");
			return true;
		}
		return false;
	}
}
