package io.noks.kitpvp.holograms;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import io.noks.Hologram;
import io.noks.kitpvp.abstracts.Holograms;

public class LeaderboardHologram implements Holograms {
	private List<Hologram> content;
	
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
	
	public List<Hologram> content() {
		return this.content;
	}

	@Override
	public void spawn() {
		Hologram parent = Bukkit.getServer().newHologram(location(), header());
		for (int i = 0; i < 10; i++) {
			String name = ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + "NAME " + ChatColor.GRAY + "- " + ChatColor.YELLOW + "STATS";
			parent.addLineBelow(name);
		}
		parent.addLineBelow(footer());
		this.content = parent.getChild();
	}
}
