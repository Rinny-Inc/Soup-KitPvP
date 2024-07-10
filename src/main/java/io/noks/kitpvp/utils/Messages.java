package io.noks.kitpvp.utils;

import org.bukkit.ChatColor;

import com.avaje.ebean.validation.NotNull;

public class Messages {
	public final @NotNull String[] WELCOME_MESSAGE;
	public final @NotNull String NO_PERMISSION;
	public final @NotNull String PLAYER_NOT_ONLINE;
	public final @NotNull String YOU_ARENT_IN_THE_SPAWN;
	
	public Messages(String domain) {
		WELCOME_MESSAGE = new String[]{ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------------",
				ChatColor.DARK_AQUA + "| " + ChatColor.RESET + "Welcome on the BETA phase of " + ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + domain + ChatColor.RESET + " the home of soup pvp!",
				"", 
				ChatColor.DARK_AQUA + "| " + ChatColor.RESET + "Our Website >> " + ChatColor.RED + "http://soupworld.net",
				ChatColor.DARK_AQUA + "| " + ChatColor.RESET + "Our Discord >> " + ChatColor.DARK_AQUA + "http://discord." + domain,
				ChatColor.RED + "-> Keep in mind this is a beta ^^", 
				ChatColor.RED + "If you encounter a bug, report it on the discord ^^",
				ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------------" };
		NO_PERMISSION = "No permission.";
		PLAYER_NOT_ONLINE = ChatColor.RED + "This player is not online.";
		YOU_ARENT_IN_THE_SPAWN = ChatColor.RED + "You are not in the spawn!";
	}
}
