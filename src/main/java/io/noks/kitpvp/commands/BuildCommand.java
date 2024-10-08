package io.noks.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.managers.PlayerManager;

public class BuildCommand implements CommandExecutor {
	private final Main main;
	public BuildCommand(Main main) {
		this.main = main;
		main.getCommand("build").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (!sender.hasPermission("command.build")) {
			sender.sendMessage(this.main.getMessages().NO_PERMISSION);
			return false;
		}
		if (args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /build <player>");
			return false;
		}

		final Player player = (Player) sender;
		if (args.length == 1) {
			final Player target = Bukkit.getPlayer(args[0]);

			if (target == null) {
				player.sendMessage(this.main.getMessages().PLAYER_NOT_ONLINE);
				return false;
			}
			final PlayerManager tm = PlayerManager.get(target.getUniqueId());
			if (tm.hasAbility()) {
				player.sendMessage(ChatColor.RED + "This player is in the map!");
				return false;
			}
			tm.setAllowBuild(!tm.isAllowBuild());
			if (target != player) {
				player.sendMessage(ChatColor.GRAY + "Build state updated for " + target.getName() + ": " + ChatColor.YELLOW + tm.isAllowBuild());
			}
			target.sendMessage(ChatColor.GRAY + "Build state updated: " + ChatColor.YELLOW + tm.isAllowBuild());
			return true;
		}
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm.hasAbility()) {
			player.sendMessage(ChatColor.RED + "You are in the map!");
			return false;
		}
		pm.setAllowBuild(!pm.isAllowBuild());
		player.sendMessage(ChatColor.GRAY + "Build state updated: " + ChatColor.YELLOW + pm.isAllowBuild());
		return true;
	}
}
