package io.noks.kitpvp.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.RefillInventoryManager;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.PlayerSettings.SlotType;

public class InventoryListener implements Listener {
	private Main plugin;

	public InventoryListener(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void blockSpawnMoveItem(InventoryClickEvent event) {
		final Inventory inventory = event.getClickedInventory();
		if (inventory == null) {
			return;
		}
		if (inventory.getType().equals(InventoryType.CREATIVE) || inventory.getType().equals(InventoryType.CRAFTING) || inventory.getType().equals(InventoryType.PLAYER)) {
			final Player player = (Player) event.getWhoClicked();
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());

			if (!pm.getAbility().hasAbility() && player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
				player.updateInventory();
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent event) {
		final Inventory inventory = event.getClickedInventory();
		if (inventory == null) {
			return;
		}
		if (inventory.getType() != InventoryType.CHEST) {
			return;
		}
		event.setCancelled(true);
		final ItemStack item = event.getCurrentItem();
		
		if (item == null || item.getType() == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
			return;
		}
		if (inventory.getTitle().toLowerCase().contains("your abilities")) {
			final Player player = (Player) event.getWhoClicked();
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());
			final String itemName = item.getItemMeta().getDisplayName();
			if (itemName.equals(" ") || itemName.length() < 3) {
				return;
			}
			String correctItemName = itemName.substring(2, itemName.length());
			if (Rarity.contains(correctItemName)) {
				Rarity rarity = Rarity.getRarityByName(correctItemName);
				if (!player.hasPermission(rarity.getPermission()) && !player.hasPermission("kit.*")) {
					return;
				}
				inventory.setContents(this.plugin.getInventoryManager().loadKitsInventory(player, rarity).getContents());
				return;
			}
			if (itemName.toLowerCase().equals(ChatColor.RED + "leave")) {
				player.sendMessage(ChatColor.RED + "You just left your kits inventory.");
				player.closeInventory();
				return;
			}
			if (itemName.toLowerCase().equals(ChatColor.DARK_AQUA + "settings")) {
				player.closeInventory();
				player.openInventory(this.plugin.getInventoryManager().loadSettingsInventory(player));
				return;
			}
			if (itemName.toLowerCase().equals(ChatColor.YELLOW + "your whole abilities")) {
				inventory.setContents(this.plugin.getInventoryManager().loadKitsInventory(player).getContents());
				return;
			}
			if (itemName.toLowerCase().equals(ChatColor.YELLOW + "next page")) {
				inventory.setContents(this.plugin.getInventoryManager().loadKitsInventory(player, Rarity.getRarityByName(correctItemName), item.getAmount()).getContents());
				return;
			}
			if (itemName.toLowerCase().equals(ChatColor.YELLOW + "previous page")) {
				inventory.setContents(this.plugin.getInventoryManager().loadKitsInventory(player, Rarity.getRarityByName(correctItemName), item.getAmount()).getContents());
				return;
			}
			if (itemName.toLowerCase().contains("last used ability:")) {
				final String split = itemName.split(": ")[1];
				correctItemName = split.substring(2, split.length());
			}
			if (itemName.toLowerCase().equals(ChatColor.YELLOW + "random abilities")) {
				List<String> abilities = Lists.newArrayList();

				for (Abilities abilitiess : this.plugin.getAbilitiesManager().getAbilities()) {
					if (abilitiess.getRarity() != Rarity.USELESS && (player.hasPermission("kit." + abilitiess.getName().toLowerCase()) || player.hasPermission(abilitiess.getRarity().getPermission()) || player.hasPermission("kit.*"))) {
						abilities.add(abilitiess.getName());
					}
				}
				if (abilities.isEmpty()) {
					return;
				}
				player.closeInventory();
				int random = (new Random()).nextInt(abilities.size());
				pm.getAbility().setSelected(this.plugin.getAbilitiesManager().getAbilityFromName(abilities.get(random)));
				player.sendMessage(ChatColor.GRAY + "You've chosen " + pm.getAbility().get().getRarity().getColor() + pm.getAbility().get().getName() + ChatColor.GRAY + " ability.");
				abilities.clear();
				return;
			}
			if (!this.plugin.getAbilitiesManager().contains(correctItemName)) {
				return;
			}
			player.closeInventory();
			pm.getAbility().setSelected(this.plugin.getAbilitiesManager().getAbilityFromName(correctItemName));
			player.sendMessage(ChatColor.GRAY + "You've chosen " + pm.getAbility().get().getRarity().getColor() + pm.getAbility().get().getName() + ChatColor.GRAY + " ability.");
			return;
		}
		if (inventory.getTitle().toLowerCase().contains("settings")) {
			String itemName = item.getItemMeta().getDisplayName();
			if (itemName.equals(" ") || itemName.length() < 3) {
				return;
			}
			Player player = (Player) event.getWhoClicked();
			PlayerManager pm = PlayerManager.get(player.getUniqueId());
			if (itemName.toLowerCase().contains(":")) {
				pm.getSettings().updateCompass();
				inventory.setContents(this.plugin.getInventoryManager().loadSettingsInventory(player).getContents());
				return;
			}
			player.closeInventory();
			if (itemName.toLowerCase().equals(ChatColor.YELLOW + "previous page")) {
				player.openInventory(this.plugin.getInventoryManager().loadKitsInventory(player));
				return;
			}
			if (itemName.toLowerCase().contains("slot")) {
				String name = itemName.split(" ")[0];
				name = name.substring(2, name.length());
				player.openInventory(this.plugin.getInventoryManager().loadSlotsInventory(player, name));
				return;
			}
			return;
		}
		if (inventory.getTitle().toLowerCase().contains("slot")) {
			Player player = (Player) event.getWhoClicked();
			PlayerSettings settings = PlayerManager.get(player.getUniqueId()).getSettings();
			String titleSplitted = inventory.getTitle().split(" ")[1];
			if (!SlotType.contains(titleSplitted)) {
				return;
			}
			settings.setSlot(SlotType.getSlotTypeFromName(titleSplitted), event.getSlot());
			player.closeInventory();
			player.openInventory(this.plugin.getInventoryManager().loadSettingsInventory(player));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRefillInventoryLeave(InventoryCloseEvent event) {
		final Inventory inventory = event.getInventory();
		if (inventory.getTitle().toLowerCase().contains("refill chest") && !inventory.contains(Material.MUSHROOM_SOUP)) {
			final RefillInventoryManager im = RefillInventoryManager.get(inventory, event.getPlayer().getLocation().getBlock().getBiome());
			im.setCooldown(Long.valueOf(60L));
			im.setFilled(false);
		}
	}
}
