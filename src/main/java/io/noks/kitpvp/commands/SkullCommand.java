package io.noks.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (!sender.hasPermission("command.skull")) {
			sender.sendMessage(ChatColor.RED + "No Permission.");
			return false;
		}
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /skull <player>");
			return false;
		}
		Player p = (Player) sender;
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta sitem = (SkullMeta) item.getItemMeta();
		sitem.setDisplayName(ChatColor.YELLOW + "Head of " + target.getName());
		sitem.setOwner(target.getName());
		item.setItemMeta(sitem);
		p.getInventory().addItem(new ItemStack[] { item });
		p.updateInventory();
		return true;
	}
}
