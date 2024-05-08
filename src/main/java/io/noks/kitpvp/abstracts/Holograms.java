package io.noks.kitpvp.abstracts;

import org.bukkit.Location;

public interface Holograms {
	public String title();
	
	public Location location();
	
	public String header();
	public String footer();
	
	public void spawn();
}
