package io.noks.kitpvp.managers.caches;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import io.noks.kitpvp.enums.TournamentState;

public class Tournament {
	private List<UUID> attendees;
	private @Nullable UUID hostUUID;
	private TournamentState state;

	public Tournament(int size) {
		this.attendees = new ArrayList<UUID>(size);
		this.state = TournamentState.WAITING;
	}
	
	public Tournament(UUID host, int size) {
		this.attendees = new ArrayList<UUID>(size);
		this.state = TournamentState.WAITING;
	}
}
