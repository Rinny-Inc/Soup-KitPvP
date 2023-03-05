package io.noks.kitpvp.managers.caches;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.noks.kitpvp.enums.GuildRank;
import io.noks.kitpvp.utils.LightMap;

public class Guild {
	public static Map<String, Guild> guildList = new HashMap<String, Guild>(); 
	private String name;
	private UUID leaderUUID;
	private LightMap<UUID, GuildRank> membersUUIDList;
	private String tag;
	private boolean open;
	
	public Guild(UUID leaderUUID, String name) {
		this.name = name;
		this.leaderUUID = leaderUUID;
		this.membersUUIDList = new LightMap<UUID, GuildRank>();
		this.open = false;
		guildList.putIfAbsent(name, this);
	}
	public Guild(UUID leaderUUID, String name, String tag, LightMap<UUID, GuildRank> membersList, boolean open) {
		this.name = name;
		this.leaderUUID = leaderUUID;
		this.membersUUIDList = membersList;
		this.tag = tag;
		this.open = open;
		guildList.putIfAbsent(name, this);
	}
}
