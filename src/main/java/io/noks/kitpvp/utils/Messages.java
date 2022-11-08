package io.noks.kitpvp.utils;

import org.bukkit.ChatColor;

public class Messages {
	public static final String[] WELCOME_MESSAGE = {
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			ChatColor.GRAY + "Welcome on " + ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Rastacraft" + ChatColor.GRAY + " the home of soup!",
			"", 
			ChatColor.GRAY + "Our Discord -> " + ChatColor.DARK_AQUA + "discord.rastactaft.eu",
			ChatColor.GRAY + "Our Website -> " + ChatColor.DARK_AQUA + "rastactaft.eu",
			ChatColor.GRAY + "Our Twitter -> " + ChatColor.DARK_AQUA + "twitter.rastacraft.eu",
			ChatColor.RED + "-> Keep in mind this is a beta ^^", 
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------" };
	public static final String NO_PERMISSION = ChatColor.RED + "No permission.";
	public static final String PLAYER_NOT_ONLINE = ChatColor.RED + "This player is not online.";
	public static final String YOU_ARENT_IN_THE_SPAWN = ChatColor.RED + "You are not in the spawn!";
}
