package io.noks.kitpvp.holograms;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import io.noks.Hologram;
import io.noks.kitpvp.abstracts.Holograms;

public class LeaderboardHologram implements Holograms {
	private Set<Hologram> content;
	
	public LeaderboardHologram() {
		spawn();
	}
	
	@Override
	public String title() {
		return "Leaderboard";
	}
	
	@Override
	public Location location() {
		return new Location(Bukkit.getWorld("world"), -4.5, 102, -5.5);
	}

	@Override
	public String header() {
		return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Leaderboard";
	}

	@Override
	public String footer() {
		return ChatColor.RED + "Coming Soon :)";
	}
	
	public Set<Hologram> content() {
		return this.content;
	}

	@Override
	public void spawn() {
		final Set<Hologram> content = new HashSet<>();
		Bukkit.getServer().newHologram(location(), header());
		Location lastLocation = location().clone();
		for (int i = 0; i < 10; i++) {
			String name = ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + "NAME " + ChatColor.GRAY + "- " + ChatColor.YELLOW + "STATS";
			Hologram holo = Bukkit.getServer().newHologram(lastLocation = lastLocation.subtract(0, 0.25, 0), name);
			content.add(holo);
		}
		Bukkit.getServer().newHologram(lastLocation.subtract(0, 0.25, 0), footer());
		this.content = content;
	}
}
