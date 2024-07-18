package io.noks.kitpvp.managers;

import java.util.Set;

import com.avaje.ebean.validation.NotNull;
import com.google.common.collect.ImmutableSet;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.listeners.abilities.Batman;
import io.noks.kitpvp.listeners.abilities.Blink;
import io.noks.kitpvp.listeners.abilities.Boxer;
import io.noks.kitpvp.listeners.abilities.Chemist;
import io.noks.kitpvp.listeners.abilities.CookieMonster;
import io.noks.kitpvp.listeners.abilities.Fireman;
import io.noks.kitpvp.listeners.abilities.Fisherman;
import io.noks.kitpvp.listeners.abilities.Hulk;
import io.noks.kitpvp.listeners.abilities.Kangaroo;
import io.noks.kitpvp.listeners.abilities.Magma;
import io.noks.kitpvp.listeners.abilities.Monk;
import io.noks.kitpvp.listeners.abilities.Pacifist;
import io.noks.kitpvp.listeners.abilities.Phantom;
import io.noks.kitpvp.listeners.abilities.PvP;
import io.noks.kitpvp.listeners.abilities.Reaper;
import io.noks.kitpvp.listeners.abilities.Snail;
import io.noks.kitpvp.listeners.abilities.Sniper;
import io.noks.kitpvp.listeners.abilities.Spider;
import io.noks.kitpvp.listeners.abilities.Stomper;
import io.noks.kitpvp.listeners.abilities.Switcher;
import io.noks.kitpvp.listeners.abilities.Turtle;
import io.noks.kitpvp.listeners.abilities.Viper;
import io.noks.kitpvp.listeners.abilities.Zeus;

public class AbilitiesManager {
	private final @NotNull Set<Abilities> abilities;
	
	public AbilitiesManager(Main main) {
		ImmutableSet.Builder<Abilities> abilitiesBuilder = ImmutableSet.builder();
		
		abilitiesBuilder.add(new PvP());
		abilitiesBuilder.add(new Sniper(main));
		abilitiesBuilder.add(new Blink()); // Instancied
		abilitiesBuilder.add(new Boxer());
		abilitiesBuilder.add(new Fireman(main));
		abilitiesBuilder.add(new Fisherman(main));
		//abilitiesBuilder.add(new Gladiator(main)); // Instancied TODO: need to be TESTED
		abilitiesBuilder.add(new Hulk(main));
		abilitiesBuilder.add(new Kangaroo(main));
		//abilitiesBuilder.add(new Ninja()); // Instancied
		abilitiesBuilder.add(new Phantom(main));
		abilitiesBuilder.add(new Reaper(main));
		abilitiesBuilder.add(new Snail(main));
		abilitiesBuilder.add(new Stomper(main));
		abilitiesBuilder.add(new Switcher(main));
		abilitiesBuilder.add(new Turtle(main));
		abilitiesBuilder.add(new Viper(main));
		abilitiesBuilder.add(new Zeus(main));
		abilitiesBuilder.add(new CookieMonster(main));
		abilitiesBuilder.add(new Magma(main));
		abilitiesBuilder.add(new Monk(main));
		abilitiesBuilder.add(new Batman(main));
		abilitiesBuilder.add(new Spider(main));
		abilitiesBuilder.add(new Chemist());
		abilitiesBuilder.add(new Pacifist(main));
		
		this.abilities = abilitiesBuilder.build();
	}
	
	public Abilities getAbilityFromName(String name) {
		for (Abilities ability : this.abilities) {
			if (ability.getName().toLowerCase().equals(name.toLowerCase())) {
				return ability;
			}
		}
		return null;
	}
	
	public Set<Abilities> getAbilities() {
		return this.abilities;
	}
	
	public boolean contains(String name) {
		return (getAbilityFromName(name) != null);
	}
}
