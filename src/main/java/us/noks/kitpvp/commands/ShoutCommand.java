package us.noks.kitpvp.commands;

import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShoutCommand implements CommandExecutor {
	private String voice = ChatColor.GRAY + "(" + ChatColor.RED + "Rastacraft" + ChatColor.GRAY + ") " + ChatColor.GOLD + ChatColor.BOLD;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("command.shout")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return false;
		}
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /shout <message>");
			return false;
		}

		StringJoiner message = new StringJoiner(" ");
		for (int i = 0; i < args.length; i++) {
			message.add(args[i]);
		}
		Bukkit.broadcastMessage(this.voice + message.toString());
		return true;
	}
}
