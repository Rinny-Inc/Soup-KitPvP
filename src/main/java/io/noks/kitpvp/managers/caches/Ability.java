package io.noks.kitpvp.managers.caches;

import java.util.Map;
import java.util.WeakHashMap;

import io.noks.kitpvp.enums.AbilitiesEnum;

public class Ability {
	private AbilitiesEnum ability = AbilitiesEnum.NONE;
	private Map<AbilitiesEnum, Long> abilityCooldown = new WeakHashMap<AbilitiesEnum, Long>();
	private int abilityUseTime = 0;
	private AbilitiesEnum lastAbility = AbilitiesEnum.NONE;

	public AbilitiesEnum get() {
		return this.ability;
	}

	public AbilitiesEnum getLastUsed() {
		return this.lastAbility;
	}

	public void set(AbilitiesEnum ability) {
		this.ability = ability;
	}

	public boolean hasAbility() {
		return (this.ability != AbilitiesEnum.NONE);
	}

	public boolean hasAbility(AbilitiesEnum ability) {
		return (this.ability == ability);
	}

	public void remove() {
		removeCooldown();
		this.abilityUseTime = 0;
		if (this.lastAbility != this.ability)
			this.lastAbility = this.ability;
		this.ability = AbilitiesEnum.NONE;
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
