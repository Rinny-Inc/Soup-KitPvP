package us.noks.kitpvp.utils;

import org.bukkit.ChatColor;

public class Messages {
	private static Messages instance = new Messages();

	public static Messages getInstance() {
		return instance;
	}

	public final String[] WELCOME_MESSAGE = {
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH
					+ "----------------------------------------------------",
			ChatColor.GRAY + "Welcome on " + ChatColor.RED.toString() + ChatColor.BOLD + "SoupZone" + ChatColor.GRAY
					+ " the home of soup!",
			"", ChatColor.GRAY + "Our Discord -> " + ChatColor.RED + "discord.soupzone.eu",
			ChatColor.GRAY + "Our Website -> " + ChatColor.RED + "soupzone.eu",
			ChatColor.GRAY + "Our Twitter -> " + ChatColor.RED + "twitter.soupzone.eu",
			ChatColor.RED + "-> Keep in mind this is a beta ^^", ChatColor.DARK_GRAY

					.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------" };
	public final String NO_PERMISSION = ChatColor.RED + "No permission.";
	public final String PLAYER_NOT_ONLINE = ChatColor.RED + "This player is not online.";
	public final String YOU_ARENT_IN_THE_SPAWN = ChatColor.RED + "You are not in the spawn!";
}
