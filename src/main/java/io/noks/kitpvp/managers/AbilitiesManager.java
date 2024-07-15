package io.noks.kitpvp.managers;

import java.util.HashSet;
import java.util.Set;

import com.avaje.ebean.validation.NotNull;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.listeners.abilities.Sniper;
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
import io.noks.kitpvp.listeners.abilities.Spider;
import io.noks.kitpvp.listeners.abilities.Stomper;
import io.noks.kitpvp.listeners.abilities.Switcher;
import io.noks.kitpvp.listeners.abilities.Turtle;
import io.noks.kitpvp.listeners.abilities.Viper;
import io.noks.kitpvp.listeners.abilities.Zeus;

public class AbilitiesManager {
	private final @NotNull Set<Abilities> abilities = new HashSet<Abilities>();
	
	public AbilitiesManager(Main main) {
		abilities.add(new PvP());
		abilities.add(new Sniper(main));
		abilities.add(new Blink()); // Instancied
		abilities.add(new Boxer());
		abilities.add(new Fireman(main));
		abilities.add(new Fisherman(main));
		//abilities.add(new Gladiator(main)); // Instancied TODO: need to be TESTED
		abilities.add(new Hulk(main));
		abilities.add(new Kangaroo(main));
		//abilities.add(new Ninja()); // Instancied
		abilities.add(new Phantom(main));
		abilities.add(new Reaper(main));
		abilities.add(new Snail(main));
		abilities.add(new Stomper(main));
		abilities.add(new Switcher(main));
		abilities.add(new Turtle(main));
		abilities.add(new Viper(main));
		abilities.add(new Zeus(main));
		abilities.add(new CookieMonster(main));
		abilities.add(new Magma(main));
		abilities.add(new Monk(main));
		abilities.add(new Batman(main));
		abilities.add(new Spider(main));
		abilities.add(new Chemist());
		abilities.add(new Pacifist(main));
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
