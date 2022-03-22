package us.noks.kitpvp.enums;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public enum EventsType {
	FEAST("Feast", toLocation(-24.0D, 127.0D, 855.0D), 9600, org.bukkit.block.Chest.class),
	GOLEM("Fallen Golem", toLocation(0.0D, 0.0D, 0.0D), 9600, org.bukkit.entity.IronGolem.class);

	private String name;
	private Location position;
	private int countdown;
	private Class entityClass;

	EventsType(String name, Location position, int countdown, Class entityClass) {
		this.name = name;
		this.position = position;
		this.countdown = countdown;
		this.entityClass = entityClass;
	}

	public String getName() {
		return this.name;
	}

	public String getPrefix(ChatColor color) {
		return ChatColor.GRAY.toString() + ChatColor.BOLD + "| " + color + ChatColor.BOLD + this.name + ChatColor.GRAY + ChatColor.BOLD + " > ";
	}

	public Location getPosition() {
		return this.position;
	}

	public int getCountdown() {
		return this.countdown;
	}

	public Class getEntityClass() {
		return this.entityClass;
	}

	private static Location toLocation(double x, double y, double z) {
		return new Location(Bukkit.getWorld("world"), x, y, z);
	}
}
