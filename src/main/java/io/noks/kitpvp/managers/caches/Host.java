package io.noks.kitpvp.managers.caches;

import java.util.Map;
import java.util.UUID;

import io.noks.kitpvp.enums.TournamentState;
import io.noks.kitpvp.utils.LightList;
import net.minecraft.util.com.google.common.collect.Maps;

public class Host {
	public static Map<UUID, Host> hostList = Maps.newConcurrentMap();
	private UUID hostUUID;
	private LightList<UUID> attendees;
	private TournamentState state;

	public Host(UUID hostUUID) {
		this.hostUUID = hostUUID;
		this.attendees = new LightList<UUID>();
		this.state = TournamentState.WAITING;
		hostList.putIfAbsent(hostUUID, this);
	}
}
