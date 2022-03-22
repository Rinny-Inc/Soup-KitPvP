package us.noks.kitpvp.enums;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum AbilitiesEnum {
	NONE("None", null, null, null, Rarity.USELESS, Long.valueOf(0L), new String[] { ChatColor.AQUA + "None" }),
	PVP("PvP", getItemStack(Material.STONE_SWORD), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.COMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Default pvp kit" }),
	ARCHER("Archer", getItemStack(Material.BOW), getUnbreakableItemStack(Material.BOW), "Special Bow", Rarity.COMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Distance x 0.25" }),
	ANCHOR("Anchor", getItemStack(Material.IRON_BLOCK), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.RARE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Opponent and you won't take knockback" }),
	ANTIANCHOR("AntiAnchor", getItemStack(Material.GOLD_BLOCK), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.UNCOMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Cancel Anchor ability" }),
	BLINK("Blink", getItemStack(Material.NETHER_STAR), getItemStack(Material.NETHER_STAR), "Blink Star", Rarity.UNCOMMON, Long.valueOf(15L), new String[] { ChatColor.AQUA + "Use your star to get", ChatColor.AQUA + "away from dangerous situations" }),
	CAMEL("Camel", getItemStack(Material.SAND), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.COMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "In the desert biome you run like a camel" }),
	FIREMAN("Fireman", getItemStack(Material.LAVA_BUCKET), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.UNCOMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Fire resistant" }),
	FISHERMAN("Fisherman", getItemStack(Material.FISHING_ROD), getUnbreakableItemStack(Material.FISHING_ROD), "Fisherman Rod", Rarity.RARE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Catch your opponent with your rod" }),
	GLADIATOR("Gladiator", getItemStack(Material.IRON_FENCE), getItemStack(Material.IRON_FENCE), "Gladiator Fence", Rarity.LEGENDARY, Long.valueOf(20L), new String[] { ChatColor.AQUA + "Duel your opponent" }),
	ANTIGLADIATOR("AntiGladiator", getItemStack(Material.LADDER), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.UNIQUE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Cancel Gladiator ability" }),
	HULK("Hulk", getItemStack(Material.SLIME_BALL), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.UNCOMMON, Long.valueOf(3L), new String[] { ChatColor.AQUA + "Launch the player in your hand" }),
	GRANDPA("Grandpa", getEnchantedItemStack(Material.STICK, Enchantment.KNOCKBACK, 2), getEnchantedItemStack(Material.STICK, Enchantment.KNOCKBACK, 2), "Grandpa Stick", Rarity.COMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Protect the tower" }),
	JUMPER("Jumper", getItemStack(Material.ENDER_PEARL), getItemStack(Material.ENDER_PEARL, 5), "Evasion Pearl", Rarity.USELESS, Long.valueOf(14L), new String[] { ChatColor.AQUA + "Evade yourself from a bad situation" }),
	KANGAROO("Kangaroo", getItemStack(Material.FIREWORK), getItemStack(Material.FIREWORK), "Kangaroo Rocket", Rarity.UNIQUE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Jump like a kangaroo" }),
	MONK("Monk", getItemStack(Material.BLAZE_ROD), getItemStack(Material.BLAZE_ROD), "Monk Staff", Rarity.UNCOMMON, Long.valueOf(15L), new String[] { ChatColor.AQUA + "Switch your opponent sword", ChatColor.AQUA + "with another item in his", ChatColor.AQUA + "inventory" }),
	NINJA("Ninja", getItemStackData(Material.WOOL, (short) 15), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.LEGENDARY, Long.valueOf(20L), new String[] { ChatColor.AQUA + "Teleport yourself behind your opponent" }),
	PHANTOM("Phantom", getItemStack(Material.FEATHER), getItemStack(Material.FEATHER), "Phantom Feather", Rarity.RARE, Long.valueOf(30L), new String[] { ChatColor.AQUA + "Fly for 5 seconds" }),
	POSEIDON("Poseidon", getItemStack(Material.WATER_BUCKET), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.UNCOMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "You're a god when you are in water" }),
	REAPER("Reaper", getItemStack(Material.WOOD_HOE), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.RARE, Long.valueOf(0L),new String[] { ChatColor.AQUA + "33% chance to give wither II", ChatColor.AQUA + "to your opponents" }),
	SNAIL("Snail", getItemStack(Material.SOUL_SAND), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.RARE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "33% chance to give slowness II", ChatColor.AQUA + "to your opponents" }),
	SPECTRE("Spectre", getItemStack(Material.SUGAR), getItemStack(Material.SUGAR), "Invisible Power", Rarity.COMMON, Long.valueOf(40L), new String[] { ChatColor.AQUA + "Be invisible for 15 seconds" }),
	STOMPER("Stomper", getItemStack(Material.ANVIL), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.UNIQUE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "transfer fall damages to your opponents", ChatColor.AQUA + "while jumping on them" }),
	ANTISTOMPER("AntiStomper", getItemStack(Material.ENCHANTMENT_TABLE), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.RARE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Cancel Stomper ability" }),
	SWITCHER("Switcher", getItemStack(Material.SNOW_BALL), getItemStack(Material.SNOW_BALL, 6), "Switcher Ball", Rarity.UNIQUE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Switch your position with another player" }),
	TIMELORD("TimeLord", getItemStack(Material.WATCH), getItemStack(Material.WATCH), "Time Stopper", Rarity.LEGENDARY, Long.valueOf(45L), new String[] { ChatColor.AQUA + "Stop the time around you" }),
	TURTLE("Turtle", getItemStack(Material.OBSIDIAN), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.UNCOMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "When you're blocking with your sword,", ChatColor.AQUA + "you only take 0,5 heart" }),
	VIPER("Viper", getItemStack(Material.FLINT), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.RARE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "33% chance to give poison II", ChatColor.AQUA + "to your opponents" }),
	ZEUS("Zeus", getItemStack(Material.WOOD_AXE), getUnbreakableItemStack(Material.WOOD_AXE), "Zeus Axe", Rarity.RARE, Long.valueOf(15L), new String[] { ChatColor.AQUA + "Invoke the thunder" }),
	SPECIALIST("Specialist", getItemStack(Material.ENCHANTED_BOOK), getItemStack(Material.ENCHANTED_BOOK), "Enchant Book", Rarity.LEGENDARY, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Enchantment table in a book" }),
	COOKIEMONSTER("Cookiemonster", getItemStack(Material.COOKIE), getItemStack(Material.COOKIE, 8), "Cookie", Rarity.UNCOMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "COOKIE" }),
	MAGMA("Magma", getItemStack(Material.FIRE), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.COMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "10% chance to put in fire", ChatColor.AQUA + "your opponent." }),
	BATMAN("Batman", getItemStack(Material.WOOD_SPADE), getUnbreakableItemStack(Material.WOOD_SPADE), "Batman Hook", Rarity.RARE, Long.valueOf(15L), new String[] { ChatColor.AQUA + "Teleport you to the hooked", ChatColor.AQUA + "player" }),
	CONTRE("Contre", getItemStack(Material.MILK_BUCKET), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.UNIQUE, Long.valueOf(0L), new String[] { ChatColor.AQUA + "It counters  Magma, Snail,  Viper & Reaper effects" }),
	ENDERMAGE("Endermage", getItemStack(Material.ENDER_PORTAL_FRAME), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.COMMON, Long.valueOf(15L), new String[] { ChatColor.AQUA + "Teleport all players around to you" }),
	GLIDER("Glider", getItemStack(Material.FEATHER), getItemStack(Material.FEATHER), "Chicken Invocator", Rarity.BETA, Long.valueOf(0L), new String[] { ChatColor.RED + "In coding" }),
	BOXER("Boxer", getItemStack(Material.WOOD_SWORD), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.COMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Your hands are doing more damage" }),
	GANJAMAN("Ganjaman", getItemStack(Material.WHEAT), getItemStack(Material.WHEAT, 6), "Ganja", Rarity.LEGENDARY, Long.valueOf(25L), new String[] { ChatColor.AQUA + "Smoke ganja everyday" }),
	JELLYFISH("Jellyfish", getItemStack(Material.WATER_BUCKET), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.COMMON, Long.valueOf(0L), new String[] { ChatColor.AQUA + "Make a easy MLG when", ChatColor.AQUA + "you're falling" }),
	FLASH("Flash", getItemStack(Material.REDSTONE_TORCH_ON), getItemStack(Material.REDSTONE_TORCH_ON), "Flash", Rarity.RARE, Long.valueOf(30L), new String[] { ChatColor.AQUA + "Go far away" }),
	QUICKDROPPER("Quickdropper", getItemStack(Material.BOWL), getItemStack(Material.MUSHROOM_SOUP), null, Rarity.USELESS, Long.valueOf(0L), new String[] { ChatColor.AQUA + "You're directly dropping your bowls" }),
	DIGGER("Digger", getItemStack(Material.DIRT), getItemStack(Material.DIRT), "Direct Dig", Rarity.BETA, Long.valueOf(20L), new String[] { ChatColor.RED + "In coding" });

	private String name;
	private ItemStack icon;
	private ItemStack specialItem;
	private String spectialItemName;
	private Rarity rarity;
	private Long cooldown;
	private String[] lore;

	AbilitiesEnum(@Nullable String name, @Nullable ItemStack icon, ItemStack specialItem, String specialItemName, Rarity rarity, Long cooldown, String[] lore) {
		this.name = name;
		this.icon = icon;
		this.specialItem = specialItem;
		this.spectialItemName = specialItemName;
		this.rarity = rarity;
		this.cooldown = cooldown;
		this.lore = lore;
	}

	public String getName() {
		return this.name;
	}

	public ItemStack getIcon() {
		return this.icon;
	}

	public ItemStack getSpecialItem() {
		return this.specialItem;
	}

	public String getSpecialItemName() {
		return this.spectialItemName;
	}

	public Rarity getRarity() {
		return this.rarity;
	}

	public Long getCooldown() {
		return this.cooldown;
	}

	public String[] getLore() {
		return this.lore;
	}

	public boolean hasCooldown() {
		return (this.cooldown.longValue() != 0L);
	}

	private static ItemStack getItemStack(Material material) {
		return new ItemStack(material);
	}

	private static ItemStack getUnbreakableItemStack(Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		im.spigot().setUnbreakable(true);
		item.setItemMeta(im);
		return item;
	}

	private static ItemStack getItemStack(Material material, int amount) {
		return new ItemStack(material, amount);
	}

	private static ItemStack getItemStackData(Material material, short data) {
		return new ItemStack(material, 1, data);
	}

	private static ItemStack getEnchantedItemStack(Material material, Enchantment enchantment, int enchantmentLevel) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(enchantment, enchantmentLevel);
		return item;
	}

	public static AbilitiesEnum getAbilityFromName(String name) {
		for (AbilitiesEnum type : values()) {
			if (type.getName().toLowerCase().equals(name.toLowerCase())) {
				return type;
			}
		}
		return null;
	}

	public static boolean contains(String name) {
		return (getAbilityFromName(name) != null);
	}
}
