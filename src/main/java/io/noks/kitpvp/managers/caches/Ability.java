package io.noks.kitpvp.managers.caches;

import java.util.Map;
import java.util.WeakHashMap;

import io.noks.kitpvp.abstracts.Abilities;

public class Ability {
	private Abilities ability = null;
	private Map<Abilities, Long> abilityCooldown = new WeakHashMap<Abilities, Long>();
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
		if (this.lastAbility != this.ability)
			this.lastAbility = this.ability;
		this.ability = null;
	}

	public Long getActiveCooldown() {
		if (this.abilityCooldown.containsKey(this.ability)) return Long.valueOf(Math.max(0L,
					((Long) this.abilityCooldown.get(this.ability)).longValue() - System.currentTimeMillis()));
		return Long.valueOf(0L);
	}

	public void applyCooldown() {
		if (!this.ability.hasCooldown()) return;
		this.abilityCooldown.put(this.ability,
				Long.valueOf(System.currentTimeMillis() + this.ability.getCooldown().longValue() * 1000L));
	}

	public boolean hasActiveCooldown() {
		if (!this.abilityCooldown.containsKey(this.ability))
			return false;
		return (((Long) this.abilityCooldown.get(this.ability)).longValue() > System.currentTimeMillis());
	}

	public void removeCooldown() {
		if (!this.abilityCooldown.containsKey(this.ability))
			return;
		this.abilityCooldown.remove(this.ability);
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
