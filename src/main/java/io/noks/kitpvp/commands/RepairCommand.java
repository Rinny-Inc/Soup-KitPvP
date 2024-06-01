package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.managers.PlayerManager;

public class RepairCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return false;
		}
		if (args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /repair");
			return false;
		}
		final Player player = (Player) sender;
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm.isInSpawn()) {
			player.sendMessage(ChatColor.RED + "You need to be in the map to do this!");
			return false;
		}
		if (pm.getEconomy().getMoney() < 50) {
			player.sendMessage(ChatColor.RED + "Missing " + (50 - pm.getEconomy().getMoney()) + "credits!");
			return false;
		}
		pm.getEconomy().remove(50);
		for (ItemStack armor : player.getInventory().getArmorContents()) {
			armor.setDurability(armor.getType().getMaxDurability());
		}
		player.sendMessage(ChatColor.GREEN + "Successfuly repaired amor! " + ChatColor.GOLD + "(Cost: 150 credits)");
		return false;
	}
}
