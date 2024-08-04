package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Stats;

public class BountyCommand implements CommandExecutor {
	
	private Main main;
	public BountyCommand(Main main) {
		this.main = main;
		main.getCommand("bounty").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /bounty <player> <amount>");
			return false;
		}
		final Player player = (Player) sender;
		if (args.length == 0) {
			final Stats ps = PlayerManager.get(player.getUniqueId()).getStats();
			player.sendMessage(ChatColor.GRAY + "You have a Bounty of " + ChatColor.YELLOW + ps.getBounty() + ChatColor.GRAY + " credits.");
			return true;
		}
		final Player target = this.main.getServer().getPlayer(args[0]);
		if (target == null) {
			player.sendMessage(ChatColor.RED + "Player's not connected!"); // TODO: allow offline bounty
			return false;
		}
		final Stats ts = PlayerManager.get(target.getUniqueId()).getStats();
		if (args.length == 1) {
			player.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " have a Bounty of " + ChatColor.YELLOW + ts.getBounty() + ChatColor.GRAY + " credits.");
			return true;
		}
		if (!isInteger(args[1])) {
			sender.sendMessage(ChatColor.RED + "Usage: /bounty <player> <amount>");
			return false;
		}
		final Integer amount = Integer.valueOf(args[1]);
		final Economy pe = PlayerManager.get(player.getUniqueId()).getEconomy();
		if (amount > pe.getMoney()) {
			player.sendMessage(ChatColor.RED + "Not enough credits!");
			return false;
		}
		pe.remove(amount);
		ts.addBounty(amount);
		this.main.getServer().broadcastMessage(player.getDisplayName() + ChatColor.YELLOW + " has placed a " + ChatColor.DARK_AQUA + amount + ChatColor.YELLOW + " credit bounty on " + target.getDisplayName());
		return true;
	}
	
	private boolean isInteger(String str) {
        if (str == null || str.isEmpty() || str.isBlank()) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
