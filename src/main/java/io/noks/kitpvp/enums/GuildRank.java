package io.noks.kitpvp.enums;

public enum GuildRank {
	CO_LEADER("co_leader", (byte)1),
	MEMBER("member", (byte)0);
	
	private String name;
	private byte power;
	
	GuildRank(String name, byte power) {
		this.name = name;
		this.power = power;
	}
	
	public String getName() {
		return this.name;
	}
	
	public byte getPower() {
		return this.power;
	}
	
	public static GuildRank getRankFromName(String name) {
		for (GuildRank ranks : values()) {
			if (ranks.getName().toLowerCase().equals(name)) {
				return ranks;
			}
		}
		return null;
	}
	
	public static GuildRank getRankFromPower(byte powr) {
		for (GuildRank ranks : values()) {
			if (ranks.getPower() == powr) {
				return ranks;
			}
		}
		return null;
	}
}
