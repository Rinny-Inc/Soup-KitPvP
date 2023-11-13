package io.noks.kitpvp.managers.caches;

import javax.annotation.Nullable;

import com.avaje.ebean.validation.NotNull;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.listeners.abilities.PvP;

public class Ability {
	private @Nullable Abilities ability;
	private @NotNull Abilities selectedAbility;
	private Long abilityCooldown;
	private int abilityUseTime;
	
	public Ability() {
		this.ability = null;
		this.selectedAbility = new PvP();
		this.abilityCooldown = 0L;
		this.abilityUseTime = 0;
	}

	public Abilities get() {
		return this.ability;
	}

	public Abilities getSelected() {
		return this.selectedAbility;
	}

	public void set(Abilities ability) {
		this.ability = ability;
	}
	
	public void setSelected(Abilities ability) {
		this.selectedAbility = ability;
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
		if (this.selectedAbility != this.ability) {
			this.selectedAbility = this.ability;
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
