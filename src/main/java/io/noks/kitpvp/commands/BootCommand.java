package io.noks.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.utils.Messages;

public class BootCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /boot <player>");
			return false;
		}
		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage((Messages.getInstance()).PLAYER_NOT_ONLINE);
			return false;
		}
		Player player = (Player) sender;
		if (target == player) {
			player.sendMessage(ChatColor.RED + "You can't use this command on yourself!");
			return false;
		}
		player.sendMessage(ChatColor.RED + "DDoS attack launched on " + target.getName());
		return true;
	}
}
