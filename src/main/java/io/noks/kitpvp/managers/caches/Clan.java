package io.noks.kitpvp.managers.caches;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import io.noks.kitpvp.utils.LightList;

public class Clan {
	public static Map<UUID, Clan> teamList = new HashMap<UUID, Clan>(); 
	private String name;
	private UUID leaderUUID, coLeaderUUID;
	private LightList<UUID> membersUUIDList;
	private boolean open;
	
	public Clan(UUID leaderUUID, String name) {
		this.name = name;
		this.leaderUUID = leaderUUID;
		this.membersUUIDList = new LightList<UUID>();
		this.open = false;
		teamList.putIfAbsent(leaderUUID, this);
	}
	public Clan(UUID leaderUUID, @Nullable UUID coLeaderUUID, String name, LightList<UUID> membersList, boolean open) {
		this.name = name;
		this.leaderUUID = leaderUUID;
		this.coLeaderUUID = coLeaderUUID;
		this.membersUUIDList = membersList;
		this.open = open;
		teamList.putIfAbsent(leaderUUID, this);
	}
}
