package io.noks.kitpvp.enums;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;

public enum Rarity {
	USELESS("Useless", ChatColor.WHITE, (short) 0, "rarity.useless", Double.MIN_VALUE),
	COMMON("Common", ChatColor.GRAY, (short) 7, "rarity.common", 10.5D),
	UNCOMMON("Uncommon", ChatColor.DARK_GREEN, (short) 13, "rarity.uncommon", 5.5D),
	RARE("Rare", ChatColor.BLUE, (short) 9, "rarity.rare", 2.5D),
	UNIQUE("Unique", ChatColor.DARK_PURPLE, (short) 10, "rarity.unique", 0.05D),
	LEGENDARY("Legendary", ChatColor.GOLD, (short) 1, "rarity.legendary", 0.001D),
	BETA("Beta", ChatColor.GREEN, (short) 5, "rarity.beta", Double.MIN_VALUE),
	ARTIFACT("Artifact", ChatColor.YELLOW, (short) 4, "rarity.artifact", Double.MIN_VALUE);

	private String name;
	private ChatColor color;
	private short colorId;
	private String permission;
	private double rate;

	Rarity(String name, ChatColor color, short colorId, String permission, @Nullable double rate) {
		this.name = name;
		this.color = color;
		this.colorId = colorId;
		this.permission = permission;
		this.rate = rate;
	}

	public String getName() {
		return this.name;
	}

	public ChatColor getColor() {
		return this.color;
	}

	public short getColorId() {
		return this.colorId;
	}

	public String getPermission() {
		return this.permission;
	}

	public double getRate() {
		return this.rate;
	}

	public static boolean contains(String name) {
		if (getRarityByName(name) != null) {
			return true;
		}
		return false;
	}

	public static Rarity getRarityByName(String name) {
		for (Rarity rarity : values()) {
			if (rarity.getName().toLowerCase().equals(name.toLowerCase())) {
				return rarity;
			}
		}
		return null;
	}
}
