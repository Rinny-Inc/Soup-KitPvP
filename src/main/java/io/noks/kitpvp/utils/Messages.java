package io.noks.kitpvp.utils;

import org.bukkit.ChatColor;

public class Messages {
	public static String[] WELCOME_MESSAGE;
	public static String NO_PERMISSION;
	public static String PLAYER_NOT_ONLINE;
	public static String YOU_ARENT_IN_THE_SPAWN;
	
	public Messages() {
		WELCOME_MESSAGE = new String[]{ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
				ChatColor.GRAY + "Welcome on " + ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Rastacraft" + ChatColor.GRAY + " the home of soup!",
				"", 
				ChatColor.GRAY + "Our Discord -> " + ChatColor.DARK_AQUA + "discord.rastactaft.eu",
				ChatColor.GRAY + "Our Website -> " + ChatColor.DARK_AQUA + "rastactaft.eu",
				ChatColor.GRAY + "Our Twitter -> " + ChatColor.DARK_AQUA + "twitter.rastacraft.eu",
				ChatColor.RED + "-> Keep in mind this is a beta ^^", 
				ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------" };
		NO_PERMISSION = "No permission.";
		PLAYER_NOT_ONLINE = ChatColor.RED + "This player is not online.";
		YOU_ARENT_IN_THE_SPAWN = ChatColor.RED + "You are not in the spawn!";
	}
}
