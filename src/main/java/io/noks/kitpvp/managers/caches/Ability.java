package io.noks.kitpvp.managers.caches;

import io.noks.kitpvp.abstracts.Abilities;

public class Ability {
	private Abilities ability = null;
	private Long abilityCooldown = 0L;
	private int abilityUseTime = 0;
	private Abilities lastAbility = null;

	public Abilities get() {
		return this.ability;
	}

	public Abilities getLastUsed() {
		return this.lastAbility;
	}

	public void set(Abilities ability) {
		this.ability = ability;
	}

	public boolean hasAbility() {
		return (this.ability != null);
	}

	public boolean hasAbility(Abilities ability) {
		return (this.ability == ability);
	}

	public void remove() {
		removeCooldown();
		this.abilityUseTime = 0;
		if (this.lastAbility != this.ability) {
			this.lastAbility = this.ability;
		}
		this.ability = null;
	}

	public Long getActiveCooldown() {
		if (this.abilityCooldown != 0L) return Long.valueOf(Math.max(0L, this.abilityCooldown - System.currentTimeMillis()));
		return Long.valueOf(0L);
	}

	public void applyCooldown() {
		if (!this.ability.hasCooldown()) return;
		this.abilityCooldown = Long.valueOf(System.currentTimeMillis() + this.ability.getCooldown().longValue() * 1000L);
	}

	public boolean hasActiveCooldown() {
		if (this.abilityCooldown == 0L) return false;
		return this.abilityCooldown > System.currentTimeMillis();
	}

	public void removeCooldown() {
		if (this.abilityCooldown == 0L) return;
		this.abilityCooldown = 0L;
	}

	public int getUseTime() {
		return this.abilityUseTime;
	}

	public void resetUseTime() {
		this.abilityUseTime = 0;
	}

	public void addUseTime() {
		this.abilityUseTime++;
	}
}
