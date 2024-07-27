package io.noks.kitpvp.abstracts;

import java.util.List;

import org.bukkit.Location;

import io.noks.Hologram;

public interface Holograms {
	public String title();
	
	public Location location();
	
	public String header();
	public String footer();
	
	public List<Hologram> content();
	
	public void update();
	public void spawn();
}
