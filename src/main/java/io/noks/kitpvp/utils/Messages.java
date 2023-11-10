package io.noks.kitpvp.utils;

import org.bukkit.ChatColor;

public class Messages {
	public final String[] WELCOME_MESSAGE;
	public final String NO_PERMISSION;
	public final String PLAYER_NOT_ONLINE;
	public final String YOU_ARENT_IN_THE_SPAWN;
	
	public Messages(String domain) {
		WELCOME_MESSAGE = new String[]{ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
				ChatColor.GRAY + "Welcome on " + ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + domain.split(".")[0] + ChatColor.GRAY + " the home of soup!",
				"", 
				ChatColor.GRAY + "Our Discord -> " + ChatColor.DARK_AQUA + "discord." + domain,
				ChatColor.GRAY + "Our Website -> " + ChatColor.DARK_AQUA + domain,
				ChatColor.GRAY + "Our Twitter -> " + ChatColor.DARK_AQUA + "twitter." + domain,
				ChatColor.RED + "-> Keep in mind this is a beta ^^", 
				ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------" };
		NO_PERMISSION = "No permission.";
		PLAYER_NOT_ONLINE = ChatColor.RED + "This player is not online.";
		YOU_ARENT_IN_THE_SPAWN = ChatColor.RED + "You are not in the spawn!";
	}
}
