package io.noks.kitpvp.managers;

import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.avaje.ebean.validation.NotNull;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import net.minecraft.util.com.google.common.collect.Lists;

public class InventoryManager {
	private final @NotNull Inventory shopInventory;
	
	public InventoryManager() {
		this.shopInventory = Bukkit.createInventory(null, 9, ChatColor.DARK_AQUA + "Shop");
		this.initShopInventory();
	}

	@NotNull
	public Inventory loadKitsInventory(Player player) {
		return loadKitsInventory(player, null, 1);
	}

	@NotNull
	public Inventory loadKitsInventory(Player player, @Nullable Rarity rarity) {
		return loadKitsInventory(player, rarity, 1);
	}

	@NotNull
	public Inventory loadKitsInventory(Player player, @Nullable Rarity rarity, int page) {
		Inventory[] inventories = { Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA + "Ability Selector"), Bukkit.createInventory(player, 54) };
		for (Inventory inventory : inventories) {
			fill(inventory, player);
		}
		sortPlayersKitsByRarity(player, inventories, rarity);
		if (inventories[0].firstEmpty() == -1 && inventories[1] != null) {
			inventories[0].setItem(9, new ItemStack(Main.getInstance().getItemUtils().getItemStack(new ItemStack(Material.ARROW, page + 1), ChatColor.YELLOW + "Next page", null)));
			inventories[1].setItem(17, new ItemStack(Main.getInstance().getItemUtils().getItemStack(new ItemStack(Material.ARROW, page - 1), ChatColor.YELLOW + "Previous page", null)));
		}
		if (rarity != null)
			inventories[0].setItem(inventories[0].getSize() - 9, new ItemStack(Main.getInstance().getItemUtils().getItemMaterial(Material.PAPER, 0, ChatColor.YELLOW + "Your whole abilities")));
		return inventories[page - 1];
	}

	@NotNull
	public Inventory loadSettingsInventory(Player player) {
		Inventory inv = Bukkit.createInventory(player, 27, ChatColor.DARK_AQUA + "Your Settings");
		inv.clear();
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, 15, " "));
		}
		inv.setItem(0, Main.getInstance().getItemUtils().getItemMaterial(Material.ARROW, ChatColor.YELLOW + "Previous page"));
		final PlayerSettings settings = PlayerManager.get(player.getUniqueId()).getSettings();
		inv.setItem(10, Main.getInstance().getItemUtils().getItemStack(new ItemStack(Material.COMMAND), ChatColor.GRAY + "Scoreboard", new String[] { (settings.hasScoreboardEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") }));
		inv.setItem(12, Main.getInstance().getItemUtils().getItemMaterial(Material.STONE_SWORD, ChatColor.GRAY + "Sword Slot", settings.getSlot(PlayerSettings.SlotType.SWORD) + 1));
		inv.setItem(14, Main.getInstance().getItemUtils().getItemMaterial(Material.POTATO_ITEM, ChatColor.GRAY + "Item Slot", settings.getSlot(PlayerSettings.SlotType.ITEM) + 1));
		return inv;
	}

	@NotNull
	public Inventory loadSlotsInventory(Player player, String slots) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.DARK_AQUA + slots + " Slot");
		inv.clear();
		for (int i = 0; i < inv.getSize(); i++) {
			int correctedI = i + 1;
			inv.setItem(i, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, correctedI, formatIntToColor(correctedI).toString() + correctedI));
		}
		PlayerSettings settings = PlayerManager.get(player.getUniqueId()).getSettings();
		inv.setItem(settings.getSlot(PlayerSettings.SlotType.SWORD), Main.getInstance().getItemUtils().getItemMaterial(Material.STONE_SWORD, ChatColor.YELLOW + "Sword"));
		inv.setItem(settings.getSlot(PlayerSettings.SlotType.ITEM), Main.getInstance().getItemUtils().getItemMaterial(Material.POTATO_ITEM, ChatColor.YELLOW + "Item"));
		return inv;
	}
	
	public Inventory loadPerksInventory(PlayerManager pm) {
		// TODO
		return null;
	}
	
	private void initShopInventory() {
		this.shopInventory.setItem(0, Main.getInstance().getItemUtils().getItemStack(new ItemStack(Material.IRON_CHESTPLATE), ChatColor.GREEN + "Repair All", new String[] { "", ChatColor.GRAY + "Repairs everything in your inventory.", "", ChatColor.WHITE + "Status: " + ChatColor.GREEN + "In Stock", ChatColor.WHITE + "Cost: " + ChatColor.GOLD + "50 Credits", "", ChatColor.YELLOW + "Click to purchase the Repair All!" }));
		this.shopInventory.setItem(1, Main.getInstance().getItemUtils().getItemStack(new ItemStack(Material.GOLDEN_APPLE), ChatColor.GREEN + "Golden Apples", new String[] { "", ChatColor.GRAY + "3 premium golden apples.", "", ChatColor.WHITE + "Status: " + ChatColor.GREEN + "In Stock", ChatColor.WHITE + "Cost: " + ChatColor.GOLD + "150 Credits", "", ChatColor.YELLOW + "Click to purchase the Golden Apples!" }));
		ItemStack stick = new ItemStack(Material.STICK);
		stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
		this.shopInventory.setItem(2, Main.getInstance().getItemUtils().getItemStack(stick, ChatColor.GREEN + "GrandPa Stick", new String[] { "", ChatColor.GRAY + "GrandPa's knockback 2 stick", "", ChatColor.WHITE + "Status: " + ChatColor.GREEN + "In Stock", ChatColor.WHITE + "Cost: " + ChatColor.GOLD + "25 Credits", "", ChatColor.YELLOW + "Click to purchase the GrandPa Stick!" }));
		
		this.shopInventory.setItem(8, Main.getInstance().getItemUtils().getItemStack(new ItemStack(Material.CHEST), ChatColor.DARK_AQUA + "Perk Shop", new String[] { ChatColor.GRAY + "Click here to go to the perk shop", "", ChatColor.RED + "Coming Soon :)"}));
	}
	
	@NotNull
	public Inventory openShopInventory() {
		return this.shopInventory;
	}

	@NotNull
	private ChatColor formatIntToColor(int colorInt) {
		switch (colorInt) {
		case 0:
			return ChatColor.BLACK;
		case 1:
			return ChatColor.DARK_BLUE;
		case 2:
			return ChatColor.DARK_GREEN;
		case 3:
			return ChatColor.DARK_AQUA;
		case 4:
			return ChatColor.RED;
		case 5:
			return ChatColor.DARK_PURPLE;
		case 6:
			return ChatColor.GOLD;
		case 7:
			return ChatColor.GRAY;
		case 8:
			return ChatColor.DARK_GRAY;
		case 9:
			return ChatColor.BLUE;
		}
		return ChatColor.RESET;
	}

	public void openRecraftInventory(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.DARK_AQUA + "Recraft");
		inv.setItem(3, new ItemStack(Material.RED_MUSHROOM, 32));
		inv.setItem(4, new ItemStack(Material.BOWL, 32));
		inv.setItem(5, new ItemStack(Material.BROWN_MUSHROOM, 32));
		player.openInventory(inv);
	}

	private void sortPlayersKitsByRarity(Player player, Inventory[] inventory, @Nullable Rarity rarity) {
		final List<Abilities> list = Lists.newArrayList(Main.getInstance().getAbilitiesManager().getAbilities());
		list.sort(Comparator.comparing(Abilities::getRarity));
		for (Abilities abilities : list) {
			if (abilities.getRarity() == Rarity.USELESS || (!player.hasPermission("kit." + abilities.getName().toLowerCase()) && !player.hasPermission(abilities.getRarity().getPermission()) && !player.hasPermission("kit.*")) || (rarity != null && abilities.getRarity() != rarity))
				continue;
			if (inventory[0].firstEmpty() == -1) {
				if (inventory[1] == null) continue;
				inventory[1].addItem(Main.getInstance().getItemUtils().getItemStack(abilities.getIcon(), abilities.getRarity().getColor() + abilities.getName(), abilities.getLore()));
			}
			inventory[0].addItem(Main.getInstance().getItemUtils().getItemStack(abilities.getIcon(), abilities.getRarity().getColor() + abilities.getName(), abilities.getLore()));
		}
	}

	private void fill(Inventory inventory, Player player) {
		inventory.clear();
		for (int i = 0; i < 18; i++) {
			inventory.setItem(i, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, 15, " "));
		}
		inventory.setItem(2, Main.getInstance().getItemUtils().getItemStack(new ItemStack(Material.NOTE_BLOCK), ChatColor.YELLOW + "Ability Rotation", new String[] {null, ChatColor.GRAY + "Get free abilities!", "", ChatColor.RED + " Coming soon"}));
		inventory.setItem(4, Main.getInstance().getItemUtils().getItemMaterial(Material.BEACON, 0, ChatColor.DARK_GRAY + "(" + ChatColor.DARK_AQUA + "SoupWorld" + ChatColor.DARK_GRAY + ")"));
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm.getSelectedAbility() != null) {
			final Abilities lastAbility = pm.getSelectedAbility();
			inventory.setItem(6, Main.getInstance().getItemUtils().getItemStack(lastAbility.getIcon(), ChatColor.YELLOW + "Last used ability: " + lastAbility.getRarity().getColor() + lastAbility.getName(), lastAbility.getLore()));
		}
		inventory.setItem(7, Main.getInstance().getItemUtils().getItemMaterial(Material.WATCH, 0, ChatColor.YELLOW + "Random Abilities"));
		int rarityStartSlot = 10;
		for (Rarity rarity : Rarity.values()) {
			if (rarity != Rarity.USELESS) {
				boolean hasPermission = (player.hasPermission(rarity.getPermission()) || player.hasPermission("kit.*"));
				inventory.setItem(rarityStartSlot, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, hasPermission ? rarity.getColorId() : 14, (hasPermission ? rarity.getColor() : ChatColor.RED) + rarity.getName()));
				rarityStartSlot++;
			}
		}
	}
}
