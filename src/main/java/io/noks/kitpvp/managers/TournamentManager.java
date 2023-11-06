package io.noks.kitpvp.managers;

import javax.annotation.Nullable;

import io.noks.kitpvp.managers.caches.Tournament;

public class TournamentManager {
	private @Nullable Tournament tournament;
	
	public boolean isActive() {
		return this.tournament != null;
	}
	
	public Tournament getActive() {
		return this.tournament;
	}
	
	public void setActive(Tournament tournament) {
		this.tournament = tournament;
	}
}
