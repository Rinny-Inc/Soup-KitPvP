package us.noks.kitpvp.enums;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Rarity {
	USELESS("Useless", DyeColor.WHITE, (short) 0, "rarity.useless", 0.0D),
	COMMON("Common", DyeColor.SILVER, (short) 7, "rarity.common", 10.5D),
	UNCOMMON("Uncommon", DyeColor.GREEN, (short) 13, "rarity.uncommon", 5.5D),
	RARE("Rare", DyeColor.MAGENTA, (short) 9, "rarity.rare", 2.5D),
	UNIQUE("Unique", DyeColor.PURPLE, (short) 10, "rarity.unique", 0.5D),
	LEGENDARY("Legendary", DyeColor.ORANGE, (short) 1, "rarity.legendary", 0.1D),
	BETA("Beta", DyeColor.LIME, (short) 5, "rarity.beta", 0.0D),
	ARTIFACT("Artifact", DyeColor.YELLOW, (short) 4, "rarity.artifact", 0.0D);

	private String name;
	private DyeColor dyeColor;
	private short colorId;
	private String permission;
	private double rate;

	Rarity(String name, DyeColor dyeColor, short colorId, String permission, double rate) {
		this.name = name;
		this.dyeColor = dyeColor;
		this.colorId = colorId;
		this.permission = permission;
		this.rate = rate;
	}

	public String getName() {
		return this.name;
	}

	public DyeColor getDyeColor() {
		return this.dyeColor;
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

	public String formatDyeColorToChatColor() {
      switch (this.dyeColor.ordinal()) {
        case 1:
          return ChatColor.BLACK.toString();
        case 2:
          return ChatColor.DARK_BLUE.toString();
        case 3:
          return ChatColor.DARK_GREEN.toString();
        case 4:
          return ChatColor.DARK_AQUA.toString();
        case 5:
          return ChatColor.RED.toString();
        case 6:
          return ChatColor.DARK_PURPLE.toString();
        case 7:
          return ChatColor.GOLD.toString();
        case 8:
          return ChatColor.GOLD.toString();
        case 9:
          return ChatColor.GRAY.toString();
        case 10:
          return ChatColor.DARK_GRAY.toString();
        case 11:
          return ChatColor.BLUE.toString();
        case 12:
          return ChatColor.GREEN.toString();
        case 13:
          return ChatColor.AQUA.toString();
        case 14:
          return ChatColor.LIGHT_PURPLE.toString();
        case 15:
          return ChatColor.YELLOW.toString();
        case 16:
          return ChatColor.WHITE.toString();
      } 
      return ChatColor.RESET.toString();
    }	
}
