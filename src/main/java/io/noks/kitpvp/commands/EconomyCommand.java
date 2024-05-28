package io.noks.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Economy;

public class EconomyCommand implements CommandExecutor {
	
	public EconomyCommand(Main main) {
		main.getCommand("balance").setExecutor(this);
		main.getCommand("pay").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (command.getName().equalsIgnoreCase("balance")) {
			if (args.length > 1) {
				sender.sendMessage(ChatColor.RED + "Usage: /balance (player)");
				return false;
			}
			final Player player = (Player) sender;
			Economy economy = PlayerManager.get(player.getUniqueId()).getEconomy();
			if (args.length == 1) {
				final Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					return false;
				}
				if (target == player) {
					player.sendMessage(economy.toString());
					return true;
				}
				economy = PlayerManager.get(target.getUniqueId()).getEconomy();
				player.sendMessage(ChatColor.GREEN + target.getName() + " " + economy.toString());
				return true;
			}
			player.sendMessage(economy.toString());
			return true;
		}
		if (command.getName().equalsIgnoreCase("pay")) {
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount>");
				return false;
			}
			final Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				return false;
			}
			final Player player = (Player) sender;
			if (target == player) {
				player.sendMessage(ChatColor.RED + "Why paying yourself?!");
				return false;
			}
			if (!isInteger(args[1])) {
				player.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount>");
				return false;
			}
			final Integer amount = Integer.valueOf(args[1]);
			final Economy pe = PlayerManager.get(player.getUniqueId()).getEconomy();
			if (pe.getMoney() < amount) {
				player.sendMessage(ChatColor.RED + "Not enough credits!");
				return false;
			}
			pe.remove(amount);
			PlayerManager.get(target.getUniqueId()).getEconomy().add(amount);
			player.sendMessage(ChatColor.GREEN + "You've sent " + target.getName() + " " + ChatColor.YELLOW + amount + " credits.");
			player.sendMessage(ChatColor.GREEN + player.getName() + " sent you " + ChatColor.YELLOW + amount + " credits.");
			return true;
		}
		return false;
	}
	
	public boolean isInteger(String str) {
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
