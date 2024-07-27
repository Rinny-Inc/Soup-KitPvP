package io.noks.kitpvp.holograms;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import io.noks.Hologram;
import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Holograms;
import io.noks.kitpvp.database.DBUtils;
import io.noks.kitpvp.enums.RefreshType;
import net.minecraft.util.com.google.common.collect.Lists;

public class LeaderboardHologram implements Holograms {
	private List<Hologram> content;
	
	private final DBUtils db;
	public LeaderboardHologram(Main main) {
		this.db = main.getDataBase();
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
		return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Kill Leaderboard";
	}

	@Override
	public String footer() {
		return ChatColor.RED + "More Coming Soon :)";
	}
	
	public List<Hologram> content() {
		return this.content;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void spawn() {
		List<Hologram> holow = Lists.newLinkedList();
		Hologram parent = Bukkit.getServer().newHologram(location(), header());
		int i = 0;
		for (Map.Entry<String, Integer> entry : this.db.getLeaderboard(RefreshType.KILLS).entrySet()) {
			i++;
			String name = entry.getKey();
			int kills = entry.getValue();
			holow.add(parent = parent.addLineBelow(ChatColor.YELLOW.toString() + i + ". " + ChatColor.AQUA + name + " " + ChatColor.GRAY + "- " + ChatColor.YELLOW + kills));
		}
		holow.add(parent = parent.addLineBelow(footer()));
		this.content = holow;
	}
}
