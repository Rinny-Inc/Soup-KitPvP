package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.managers.PlayerManager;

public class SponsorCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only player can do this command!");
			return false;
		}
		if (!sender.hasPermission("command.sponsor")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return false;
		}
		if (args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /sponsor");
			return false;
		}
		Player player = (Player) sender;
		PlayerManager pm = PlayerManager.get(player.getUniqueId());

		if (!pm.getAbility().hasAbility()) {
			player.sendMessage(ChatColor.RED + "You can't do this command here.");
			return false;
		}
		if (pm.hasUsedSponsor()) {
			player.sendMessage(ChatColor.RED + "You already do this command!");
			return false;
		}
		pm.setUsedSponsor(true);
		Location loc = player.getLocation();
		loc.getWorld().spawnFallingBlock(loc.clone().add(0.0D, 45.0D, 0.0D), Material.CHEST, (byte) 0);
		player.sendMessage(ChatColor.GREEN + "Your sponsor is falling down!");
		return true;
	}
}
