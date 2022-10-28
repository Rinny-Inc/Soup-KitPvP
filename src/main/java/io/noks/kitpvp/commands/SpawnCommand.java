package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.managers.PlayerManager;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /spawn");
			return false;
		}
		final Player player = (Player) sender;
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		
		final String spawnMessage = ChatColor.GREEN + "Teleporting to spawn.";
		if (!pm.getAbility().hasAbility() && !pm.hasCombatTag()) {
			player.teleport(player.getWorld().getSpawnLocation());
			player.sendMessage(spawnMessage);
			return true;
		}
		// TODO countdown of 5
		return true;
	}
}
