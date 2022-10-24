package io.noks.kitpvp.managers.caches;

import java.util.UUID;

public class CombatTag {
	private UUID lastAttackerUUID;
	private Long time;
	
	public CombatTag(UUID lastAttackerUUID) {
		this.lastAttackerUUID = lastAttackerUUID;
		this.time = System.currentTimeMillis() + (14 * 1000);
	}
	
	public UUID getLastAttackerUUID() {
		return this.lastAttackerUUID;
	}
	
	public Long getTime() {
		return this.time;
	}
}
