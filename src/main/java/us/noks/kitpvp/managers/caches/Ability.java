package us.noks.kitpvp.managers.caches;

import java.util.Map;

import com.google.common.collect.Maps;

import us.noks.kitpvp.enums.AbilitiesEnum;

public class Ability {
	private AbilitiesEnum ability = AbilitiesEnum.NONE;
	private Map<AbilitiesEnum, Long> abilityCooldown = Maps.newHashMap();
	private int abilityUseTime = 0;
	private AbilitiesEnum lastAbility = AbilitiesEnum.NONE;

	public AbilitiesEnum getAbility() {
		return this.ability;
	}

	public AbilitiesEnum getLastAbility() {
		return this.lastAbility;
	}

	public void setAbility(AbilitiesEnum ability) {
		this.ability = ability;
	}

	public boolean hasAbility() {
		return (this.ability != AbilitiesEnum.NONE);
	}

	public boolean hasAbility(AbilitiesEnum ability) {
		return (this.ability == ability);
	}

	public void removeAbility() {
		removeAbilityCooldown();
		this.abilityUseTime = 0;
		if (this.lastAbility != this.ability)
			this.lastAbility = this.ability;
		this.ability = AbilitiesEnum.NONE;
	}

	public Long getAbilityCooldown() {
		if (this.abilityCooldown.containsKey(this.ability))
			return Long.valueOf(Math.max(0L,
					((Long) this.abilityCooldown.get(this.ability)).longValue() - System.currentTimeMillis()));
		return Long.valueOf(0L);
	}

	public void setAbilityCooldown() {
		if (!this.ability.hasCooldown())
			return;
		this.abilityCooldown.put(this.ability,
				Long.valueOf(System.currentTimeMillis() + this.ability.getCooldown().longValue() * 1000L));
	}

	public boolean hasAbilityCooldown() {
		if (!this.abilityCooldown.containsKey(this.ability))
			return false;
		return (((Long) this.abilityCooldown.get(this.ability)).longValue() > System.currentTimeMillis());
	}

	public void removeAbilityCooldown() {
		if (!this.abilityCooldown.containsKey(this.ability))
			return;
		this.abilityCooldown.remove(this.ability);
	}

	public int getAbilityUseTime() {
		return this.abilityUseTime;
	}

	public void resetAbilityUseTime() {
		this.abilityUseTime = 0;
	}

	public void addAbilityUseTime() {
		this.abilityUseTime++;
	}
}
