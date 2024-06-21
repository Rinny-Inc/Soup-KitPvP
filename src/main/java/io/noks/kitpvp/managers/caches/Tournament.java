package io.noks.kitpvp.managers.caches;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import io.noks.kitpvp.enums.TournamentState;

public class Tournament {
	private List<UUID> attendees;
	private List<UUID> spectators;
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
	
	public void addAttendee(UUID uuid) {
		this.attendees.add(uuid);
	}
	
	public void removeAttendee(UUID uuid) {
		this.attendees.remove(uuid);
	}
	
	public void killAttendee(UUID uuid) {
		removeAttendee(uuid);
	}
	
	public List<UUID> getAttendees() {
		return this.attendees;
	}
	
	public boolean containsAttendee(UUID uuid) {
		return this.attendees.contains(uuid);
	}
	
	public boolean hasHost() {
		return this.hostUUID != null;
	}
	
	public TournamentState getState() {
		return this.state;
	}
	
	public boolean hasWinner() {
		return this.attendees.size() == 1;
	}
}
