package io.noks.kitpvp.managers.caches;

import io.noks.kitpvp.enums.PerksEnum;

public class Perks {
	private PerksEnum[] perks = new PerksEnum[3];
	
	public Perks(PerksEnum... tier) {
		this.perks = tier;
	}
	
	public PerksEnum first() {
		return this.perks[0];
	}
	
	public PerksEnum second() {
		return this.perks[1];
	}
	
	public PerksEnum third() {
		return this.perks[2];
	}
}
