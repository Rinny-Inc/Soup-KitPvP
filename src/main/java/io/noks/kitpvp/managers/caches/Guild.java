package io.noks.kitpvp.managers.caches;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import io.noks.kitpvp.enums.GuildRank;

public class Guild {
	public static Map<String, Guild> guildList = new HashMap<String, Guild>(); 
	private UUID leaderUuid;
	private String name;
	private Map<UUID, GuildRank> membersUUIDList;
	private String motd;
	private String tag;
	private boolean open;
	
	public Guild(String name, UUID leaderUUID) {
		this.name = name;
		this.leaderUuid = leaderUUID;
		this.membersUUIDList = new LinkedHashMap<UUID, GuildRank>();
		this.open = false;
		guildList.putIfAbsent(name, this);
	}
	public Guild(String name, UUID leaderuuid, String motd, String tag, Map<UUID, GuildRank> membersList, boolean open) {
		this.name = name;
		this.leaderUuid = leaderuuid;
		this.membersUUIDList = membersList;
		this.motd = motd;
		this.tag = tag;
		this.open = open;
		guildList.putIfAbsent(name, this);
	}
	
	public UUID leaderUUID() {
		return this.leaderUuid;
	}
	
	public void addMember(UUID uuid) {
		this.membersUUIDList.putIfAbsent(uuid, GuildRank.MEMBER);
	}
	
	public void kick(UUID uuid) {
		if (this.membersUUIDList.containsKey(uuid)) {
			this.membersUUIDList.remove(uuid);
		}
	}
}
