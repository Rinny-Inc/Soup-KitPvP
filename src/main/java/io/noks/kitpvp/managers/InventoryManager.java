package io.noks.kitpvp.managers;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public class InventoryManager {

	public Inventory loadKitsInventory(Player player) {
		return loadKitsInventory(player, null, 1);
	}

	public Inventory loadKitsInventory(Player player, @Nullable Rarity rarity) {
		return loadKitsInventory(player, rarity, 1);
	}

	public Inventory loadKitsInventory(Player player, @Nullable Rarity rarity, int page) {
		Inventory[] inventories = { Bukkit.createInventory(player, 54, "-> Your abilities"), Bukkit.createInventory(player, 54) };
		for (Inventory inventory : inventories) {
			fill(inventory, player);
		}
		sortPlayersKitsByRarity(player, inventories, rarity);
		if (inventories[0].firstEmpty() == -1 && inventories[1] != null) {
			inventories[0].setItem(inventories[0].getSize() - 1, new ItemStack(Main.getInstance().getItemUtils().getItemStack(new ItemStack(Material.ARROW, page + 1), ChatColor.YELLOW + "Next page", null)));
			inventories[1].setItem(inventories[1].getSize() - 9, new ItemStack(Main.getInstance().getItemUtils().getItemStack(new ItemStack(Material.ARROW, page - 1), ChatColor.YELLOW + "Previous page", null)));
		}
		if (rarity != null)
			inventories[0].setItem(inventories[0].getSize() - 9, new ItemStack(Main.getInstance().getItemUtils().getItemMaterial(Material.PAPER, 0, ChatColor.YELLOW + "Your whole abilities")));
		return inventories[page - 1];
	}

	public Inventory loadRefillInventory(Player player) {
		RefillInventoryManager im = RefillInventoryManager.get(Bukkit.createInventory(null, 54, "-> "
				+ WordUtils.capitalizeFully(player.getLocation().getBlock().getBiome().toString()) + " Refill Chest"),
				player.getLocation().getBlock().getBiome());
		if (im.hasCooldown()) {
			double cooldown = im.getCooldown().longValue() / 1000.0D;
			player.sendMessage(ChatColor.RED + "Refill ends in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
			return null;
		}
		if (!im.isFilled()) {
			ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
			while (im.getInventory().firstEmpty() != -1) {
				im.getInventory().addItem(new ItemStack[] { soup });
			}
			im.setFilled(true);
		}
		player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.95F, 1.05F);
		return im.getInventory();
	}

	public Inventory loadSettingsInventory(Player player) {
		Inventory inv = Bukkit.createInventory(player, 27, "-> Settings");
		inv.clear();
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, 15, " "));
		}
		inv.setItem(0, Main.getInstance().getItemUtils().getItemMaterial(Material.ARROW, ChatColor.YELLOW + "Previous page"));
		PlayerSettings settings = PlayerManager.get(player.getUniqueId()).getSettings();
		inv.setItem(10, Main.getInstance().getItemUtils().getItemMaterial(Material.COMPASS, ChatColor.GRAY + "Compass: " + (settings.hasCompass() ? (ChatColor.GREEN + "Enabled") : (ChatColor.RED + "Disabled"))));
		inv.setItem(12, Main.getInstance().getItemUtils().getItemMaterial(Material.STONE_SWORD, ChatColor.GRAY + "Sword Slot", settings.getSlot(PlayerSettings.SlotType.SWORD) + 1));
		inv.setItem(14, Main.getInstance().getItemUtils().getItemMaterial(Material.POTATO_ITEM, ChatColor.GRAY + "Item Slot", settings.getSlot(PlayerSettings.SlotType.ITEM) + 1));
		if (settings.hasCompass()) {
			inv.setItem(16, Main.getInstance().getItemUtils().getItemMaterial(Material.COMPASS, ChatColor.GRAY + "Compass Slot", settings.getSlot(PlayerSettings.SlotType.COMPASS) + 1));
		}
		return inv;
	}

	public Inventory loadSlotsInventory(Player player, String slots) {
		Inventory inv = Bukkit.createInventory(player, 9, "-> " + slots + " Slot");
		inv.clear();
		for (int i = 0; i < inv.getSize(); i++) {
			int correctedI = i + 1;
			inv.setItem(i, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, correctedI, formatIntToColor(correctedI).toString() + correctedI));
		}
		PlayerSettings settings = PlayerManager.get(player.getUniqueId()).getSettings();
		inv.setItem(settings.getSlot(PlayerSettings.SlotType.SWORD), Main.getInstance().getItemUtils().getItemMaterial(Material.STONE_SWORD, ChatColor.YELLOW + "Sword"));
		inv.setItem(settings.getSlot(PlayerSettings.SlotType.ITEM), Main.getInstance().getItemUtils().getItemMaterial(Material.POTATO_ITEM, ChatColor.YELLOW + "Item"));
		if (settings.hasCompass())
			inv.setItem(settings.getSlot(PlayerSettings.SlotType.COMPASS), Main.getInstance().getItemUtils().getItemMaterial(Material.COMPASS, ChatColor.YELLOW + "Compass"));
		return inv;
	}

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
		Inventory inv = Bukkit.createInventory(player, 9, "-> Recraft");
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
		for (int i = 0; i < 9; i++) {
			inventory.setItem(i, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, 15, " "));
		}
		for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
			inventory.setItem(i, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, 15, " "));
		}
		inventory.setItem(0, Main.getInstance().getItemUtils().getItemMaterial(Material.WATCH, 0, ChatColor.YELLOW + "Random Abilities"));
		final Ability ability = PlayerManager.get(player.getUniqueId()).getAbility();
		if (ability.getLastUsed() != null) {
			Abilities lastAbility = ability.getLastUsed();
			inventory.setItem(1, Main.getInstance().getItemUtils().getItemStack(lastAbility.getIcon(), ChatColor.YELLOW + "Last used ability: " + lastAbility.getRarity().getColor() + lastAbility.getName(), lastAbility.getLore()));
		}
		inventory.setItem(4, Main.getInstance().getItemUtils().getItemMaterial(Material.BEACON, 0, ChatColor.DARK_GRAY + "(" + ChatColor.DARK_AQUA + "Rastacraft" + ChatColor.DARK_GRAY + ")"));
		inventory.setItem(7, Main.getInstance().getItemUtils().getItemMaterial(Material.NAME_TAG, ChatColor.DARK_AQUA + "Settings"));
		inventory.setItem(8, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, 14, ChatColor.RED + "Leave"));
		int rarityStartSlot = inventory.getSize() - 8;
		for (Rarity rarity : Rarity.values()) {
			if (rarity != Rarity.USELESS) {
				boolean hasPermission = (player.hasPermission(rarity.getPermission()) || player.hasPermission("kit.*"));
				inventory.setItem(rarityStartSlot, Main.getInstance().getItemUtils().getItemMaterial(Material.STAINED_GLASS_PANE, hasPermission ? rarity.getColorId() : 14, (hasPermission ? rarity.getColor() : ChatColor.RED) + rarity.getName()));
				rarityStartSlot++;
			}
		}
	}
}
