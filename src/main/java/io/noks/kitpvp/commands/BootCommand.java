package io.noks.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.Main;

public class BootCommand implements CommandExecutor {
	private Main main;
	public BootCommand(Main main) {
		this.main = main;
		main.getCommand("boot").setExecutor(this);
	}
	
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
			sender.sendMessage(this.main.getMessages().PLAYER_NOT_ONLINE);
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
