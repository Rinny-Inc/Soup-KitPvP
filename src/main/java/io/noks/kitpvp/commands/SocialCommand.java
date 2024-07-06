package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SocialCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("discord")) {
			if (args.length > 0) {
				sender.sendMessage(ChatColor.RED + "Usage: /discord");
				return false;
			}
			sender.sendMessage(ChatColor.DARK_AQUA + "| " + ChatColor.WHITE + "Our discord link: " + ChatColor.DARK_AQUA + "http://discord.soupworld.net/");
			return true;
		}
		return false;
	}
}
