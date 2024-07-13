package io.noks.kitpvp.managers.caches;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.noks.kitpvp.enums.GuildRank;

public class Guild {
	public static Map<String, Guild> guildList = new HashMap<String, Guild>(); 
	private UUID leaderUuid;
	private String name;
	private Map<UUID, GuildRank> membersUUIDList;
	private String motd;
	private String tag;
	private int money;
	private boolean open;
	
	public Guild(String name, UUID leaderUUID) {
		this.name = name;
		this.leaderUuid = leaderUUID;
		this.membersUUIDList = new LinkedHashMap<UUID, GuildRank>();
		this.open = false;
		guildList.putIfAbsent(name, this);
	}
	public Guild(String name, UUID leaderuuid, String motd, String tag, Map<UUID, GuildRank> membersList, int money, boolean open) {
		this.name = name;
		this.leaderUuid = leaderuuid;
		this.membersUUIDList = membersList;
		this.motd = motd;
		this.tag = tag;
		this.money = money;
		this.open = open;
		guildList.putIfAbsent(name, this);
	}
	
	public void drop() {
		guildList.remove(this.name);
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
	
	public static Guild getGuildFromName(String name) {
		return guildList.get(name);
	}
	
	public static Guild getGuildFromLeader(UUID uuid) {
		for (Guild guilds : guildList.values()) {
			if (guilds.leaderUuid == uuid) {
				return guilds;
			}
		}
		return null;
	}
	
	public static Guild getGuildFromMember(UUID uuid) {
		for (Guild guilds : guildList.values()) {
			for (UUID members : guilds.membersUUIDList.keySet()) {
				if (members != uuid) {
					continue;
				}
				return guilds;
			}
		}
		return null;
	}
	
	public boolean isAnyMemberOnline() {
		for (UUID uuids : this.membersUUIDList.keySet()) {
			Player player = Bukkit.getPlayer(uuids);
			
			if (player != null && player.isOnline()) {
				return true;
			}
		}
		return false;
	}
}
