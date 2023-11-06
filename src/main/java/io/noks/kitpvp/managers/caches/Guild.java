package io.noks.kitpvp.managers.caches;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import io.noks.kitpvp.enums.GuildRank;

public class Guild {
	public static Map<UUID, Guild> guildList = new HashMap<UUID, Guild>(); 
	private UUID uuid;
	private String name;
	private Map<UUID, GuildRank> membersUUIDList;
	private String motd;
	private String tag;
	private boolean open;
	
	public Guild(String name) {
		this.name = name;
		this.uuid = UUID.randomUUID();
		this.membersUUIDList = new LinkedHashMap<UUID, GuildRank>();
		this.open = false;
		guildList.putIfAbsent(this.uuid, this);
	}
	public Guild(UUID uuid, String name, String motd, String tag, Map<UUID, GuildRank> membersList, boolean open) {
		this.uuid = uuid;
		this.name = name;
		this.membersUUIDList = membersList;
		this.motd = motd;
		this.tag = tag;
		this.open = open;
		guildList.putIfAbsent(uuid, this);
	}
	
	public UUID leaderUUID() {
		for (Map.Entry<UUID, GuildRank> entry : this.membersUUIDList.entrySet()) {
			return entry.getKey(); // WE DONT CARE HE'S THE FIRST IN THE LIST
		}
		return null;
	}
}
