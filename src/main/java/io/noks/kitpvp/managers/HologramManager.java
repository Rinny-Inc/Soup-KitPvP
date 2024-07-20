package io.noks.kitpvp.managers;

import java.util.LinkedList;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Holograms;
import io.noks.kitpvp.holograms.LeaderboardHologram;

public class HologramManager {
	public LinkedList<Holograms> holograms = new LinkedList<>();
	
	public HologramManager(Main main) {
		holograms.add(new LeaderboardHologram(main));
	}
	
	public Holograms getLeaderboard() {
		return this.holograms.get(0);
	}
}
