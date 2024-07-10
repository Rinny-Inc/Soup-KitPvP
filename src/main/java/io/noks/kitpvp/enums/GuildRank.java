package io.noks.kitpvp.enums;

public enum GuildRank {
	CO_LEADER("co_leader"),
	MEMBER("member");
	
	private String name;
	
	GuildRank(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public GuildRank getRankFromName(String name) {
		for (GuildRank ranks : values()) {
			if (this.name.equals(name)) {
				return ranks;
			}
		}
		return null;
	}
}
