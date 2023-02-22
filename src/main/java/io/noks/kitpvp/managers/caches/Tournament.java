package io.noks.kitpvp.managers.caches;

import java.util.UUID;

import io.noks.kitpvp.enums.TournamentState;
import io.noks.kitpvp.utils.LightList;

public class Tournament {
	private LightList<UUID> attendees;
	private TournamentState state;

	public Tournament() {
		this.attendees = new LightList<UUID>();
		this.state = TournamentState.WAITING;
	}
}
