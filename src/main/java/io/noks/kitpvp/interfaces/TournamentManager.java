package io.noks.kitpvp.interfaces;

import io.noks.kitpvp.managers.caches.Tournament;

public interface TournamentManager {
	public boolean isTournamentActive();
	
	public Tournament getActiveTournament();
	
	public void setActiveTournament(Tournament tournament);
}
