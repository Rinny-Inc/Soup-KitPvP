package io.noks.kitpvp.enums;

import java.util.EnumSet;

import org.bukkit.inventory.ItemStack;

public enum PerksEnum {
	BOUNTY_HUNTER("Bounty Hunter", 1, new String[] {""}), // TIER I
	SORE_LOSER("Sore Loser", 1, new String[] {""}),
	MARTYDOM("Martydom", 1, new String[] {""}),
	RELINQUISH("Relinquish", 1, new String[] {""}),
	TRICKSTER("Trickster", 1, new String[] {""}),
	REFILLER("Refiller", 1, new String[] {""}),
	STUNT_DEVIL("Stunt Devil", 1, new String[] {""}),
	AQUAMAN("Aquaman", 1, new String[] {""}),
	JAMMER("Jammer", 1, new String[] {""}), // TIER II
	REVERSE_COPYCAT("Reverse Copycat", 1, new String[] {""}),
	STEADY_HANDS("Steady Hands", 1, new String[] {""}),
	ARMORER("Armorer", 1, new String[] {""}),
	DEATH_DO_US_PART("Death Do Us Part", 1, new String[] {""}),
	TAUNT("Taunt", 1, new String[] {""}),
	REVENGE("Revenge", 1, new String[] {""}),
	INFERNO("Inferno", 1, new String[] {""}),
	HARDLINE("Hardline", 1, new String[] {""}),
	SKULL_COLLECTOR("Skull Collector", 1, new String[] {""}), // TIER III
	INCOGNITO("Incognito", 1, new String[] {""}),
	CONARTIST("Conartist", 1, new String[] {""}),
	CREDITOR("Creditor", 1, new String[] {""}),
	CANA("Cana", 1, new String[] {""}),
	BONUS_HEART("Bonus Heart", 1, new String[] {""}),
	FIRE_FIGHTER("Fire Fighter", 1, new String[] {""}),
	ANCHOR("Anchor", 1, new String[] {""}),
	LIFE_SUPPORT("Life Support", 1, new String[] {""}),
	CONTER("Counter", 1, new String[] {""});
	
	private String name;
	private ItemStack icon;
	private int cost;
	private String[] description;
	
	PerksEnum(String name, /*ItemStack icon,*/ int cost, String[] description) {
		this.name = name;
		//this.icon = icon;
		this.cost = cost;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getIcon() {
		return this.icon;
	}
	
	public int getCost() {
		return cost;
	}
	
	public String[] getDescription() {
		return description;
	}
	
	public static PerksEnum getPerksFromName(String name) {
		for (PerksEnum perk : EnumSet.allOf(PerksEnum.class)) {
			if (perk.getName().toLowerCase().equals(name.toLowerCase())) {
				return perk;
			}
		}
		return null;
	}
}
