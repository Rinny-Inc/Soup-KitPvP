package io.noks.kitpvp.enums;

import org.bukkit.inventory.ItemStack;

public enum PerksEnum {
	BOUNTY_HUNTER("Bounty Hunter", Tiers.I, 1, new String[] {""}), // TIER I
	SORE_LOSER("Sore Loser", Tiers.I, 1, new String[] {""}),
	MARTYDOM("Martydom", Tiers.I, 1, new String[] {""}),
	RELINQUISH("Relinquish", Tiers.I, 1, new String[] {""}),
	TRICKSTER("Trickster", Tiers.I, 1, new String[] {""}),
	REFILLER("Refiller", Tiers.I, 1, new String[] {""}),
	STUNT_DEVIL("Stunt Devil", Tiers.I, 1, new String[] {""}),
	AQUAMAN("Aquaman", Tiers.I, 1, new String[] {""}),
	JAMMER("Jammer", Tiers.II, 1, new String[] {""}), // TIER II
	REVERSE_COPYCAT("Reverse Copycat", Tiers.II, 1, new String[] {""}),
	STEADY_HANDS("Steady Hands", Tiers.II, 1, new String[] {""}),
	ARMORER("Armorer", Tiers.II, 1, new String[] {""}),
	DEATH_DO_US_PART("Death Do Us Part", Tiers.II, 1, new String[] {""}),
	TAUNT("Taunt", Tiers.II, 1, new String[] {""}),
	REVENGE("Revenge", Tiers.II, 1, new String[] {""}),
	INFERNO("Inferno", Tiers.II, 1, new String[] {""}),
	HARDLINE("Hardline", Tiers.II, 1, new String[] {""}),
	SKULL_COLLECTOR("Skull Collector", Tiers.III, 1, new String[] {""}), // TIER III
	INCOGNITO("Incognito", Tiers.III, 1, new String[] {""}),
	CONARTIST("Conartist", Tiers.III, 1, new String[] {""}),
	CREDITOR("Creditor", Tiers.III, 1, new String[] {""}),
	CANA("Cana", Tiers.III, 1, new String[] {""}),
	BONUS_HEART("Bonus Heart", Tiers.III, 1, new String[] {""}),
	FIRE_FIGHTER("Fire Fighter", Tiers.III, 1, new String[] {""}),
	ANCHOR("Anchor", Tiers.III, 1, new String[] {""}),
	LIFE_SUPPORT("Life Support", Tiers.III, 1, new String[] {""}),
	CONTER("Counter", Tiers.III, 1, new String[] {""});
	
	private String name;
	private ItemStack icon;
	private Tiers tier;
	private int cost;
	private String[] description;
	
	PerksEnum(String name, /*ItemStack icon,*/ Tiers tier, int cost, String[] description) {
		this.name = name;
		//this.icon = icon;
		this.tier = tier;
		this.cost = cost;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getIcon() {
		return this.icon;
	}
	
	public Tiers getTier() {
		return tier;
	}
	
	public int getCost() {
		return cost;
	}
	
	public String[] getDescription() {
		return description;
	}
	
	public static PerksEnum getPerksFromName(String name) {
		for (PerksEnum perk : values()) {
			if (perk.getName().toLowerCase().equals(name.toLowerCase())) {
				return perk;
			}
		}
		return null;
	}
}
