package io.noks.kitpvp.managers.caches;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.noks.kitpvp.enums.GuildRank;

public class Guild {
	public static final Map<String, Guild> guildList = new HashMap<String, Guild>(); 
	private UUID leaderUuid;
	private String name;
	private final Map<UUID, GuildRank> membersUUIDList;
	private String motd;
	private String tag;
	private final Economy economy;
	private boolean open;
	// TODO invite
	
	public Guild(String name, UUID leaderUUID) {
		this.name = name;
		this.leaderUuid = leaderUUID;
		this.membersUUIDList = new LinkedHashMap<UUID, GuildRank>();
		this.open = false;
		this.economy = new Economy();
		guildList.putIfAbsent(name, this);
	}
	public Guild(String name, UUID leaderuuid, String motd, String tag, Map<UUID, GuildRank> membersList, int money, boolean open) {
		this.name = name;
		this.leaderUuid = leaderuuid;
		this.membersUUIDList = membersList;
		this.motd = motd;
		this.tag = tag;
		this.economy = new Economy(money);
		this.open = open;
		guildList.putIfAbsent(name, this);
	}
	
	public void drop() {
		guildList.remove(this.name);
	}
	
	public UUID leaderUUID() {
		return this.leaderUuid;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Map<UUID, GuildRank> getMembers() {
		return this.membersUUIDList;
	}
	
	public GuildRank getMemberRank(UUID uuid) {
		return this.membersUUIDList.get(uuid);
	}
	
	public String getMOTD() {
		return this.motd;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public int getMoney() {
		return this.economy.getMoney();
	}
	
	public boolean isOpen() {
		return this.open;
	}
	
	public void addMember(UUID uuid) {
		this.membersUUIDList.putIfAbsent(uuid, GuildRank.MEMBER);
	}
	
	public void kick(UUID uuid) {
		if (this.membersUUIDList.containsKey(uuid)) {
			this.membersUUIDList.remove(uuid);
		}
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
	
	public static Guild getGuildFromName(String name) {
		return guildList.get(name);
	}
	
	public static Guild getGuildByPlayer(UUID uuid) {
		for (Guild guilds : guildList.values()) {
			if (guilds.leaderUuid == uuid) {
				return guilds;
			}
		}
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
}
