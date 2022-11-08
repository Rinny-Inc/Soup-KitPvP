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
import io.noks.kitpvp.land.Land;
import io.noks.kitpvp.managers.InventoryManager;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.RefillInventoryManager;
import io.noks.kitpvp.managers.caches.PlayerSettings;

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
		final Inventory inventory = event.getInventory();
		if (inventory == null) {
			return;
		}
		if (inventory.getType() != InventoryType.CHEST) {
			return;
		}
		final ItemStack item = event.getCurrentItem();
		
		if (item == null || item.getType() == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
			return;
		}
		if (event.getInventory().getTitle().toLowerCase().contains("your abilities")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
				Player player = (Player) event.getWhoClicked();
				PlayerManager pm = PlayerManager.get(player.getUniqueId());
				String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
				if (itemName.equals(" ") || itemName.length() < 3) {
					return;
				}
				String correctItemName = itemName.substring(2, itemName.length());
				if (Rarity.contains(correctItemName)) {
					Rarity rarity = Rarity.getRarityByName(correctItemName);
					if (!player.hasPermission(rarity.getPermission()) && !player.hasPermission("kit.*")) {
						return;
					}
					event.getInventory()
							.setContents(InventoryManager.getInstance().loadKitsInventory(player, rarity).getContents());
					return;
				}
				if (itemName.toLowerCase().equals(ChatColor.RED + "leave")) {
					player.sendMessage(ChatColor.RED + "You just left your kits inventory.");
					player.closeInventory();
					return;
				}
				if (itemName.toLowerCase().equals(ChatColor.DARK_AQUA + "settings")) {
					player.closeInventory();
					player.openInventory(InventoryManager.getInstance().loadSettingsInventory(player));
					return;
				}
				if (itemName.toLowerCase().equals(ChatColor.YELLOW + "your whole abilities")) {
					event.getInventory()
							.setContents(InventoryManager.getInstance().loadKitsInventory(player).getContents());
					return;
				}
				if (itemName.toLowerCase().equals(ChatColor.YELLOW + "next page")) {
					event.getInventory()
							.setContents(InventoryManager.getInstance()
									.loadKitsInventory(player, Rarity.getRarityByName(correctItemName),
											event.getCurrentItem().getAmount())
									.getContents());
					return;
				}
				if (itemName.toLowerCase().equals(ChatColor.YELLOW + "previous page")) {
					event.getInventory()
							.setContents(InventoryManager.getInstance()
									.loadKitsInventory(player, Rarity.getRarityByName(correctItemName),
											event.getCurrentItem().getAmount())
									.getContents());
					return;
				}
				if (itemName.toLowerCase().contains("last used ability:")) {
					String split = itemName.split(": ")[1];
					correctItemName = split.substring(2, split.length());
				}
				final Land map = new Land(pm);
				if (!map.hasValidLocation()) {
					player.sendMessage(ChatColor.RED + "Failed to teleport! (Invalid map locations)");
					return;
				}
				if (itemName.toLowerCase().equals(ChatColor.YELLOW + "random abilities")) {
					List<String> abilities = Lists.newArrayList();

					for (Abilities abilitiess : this.plugin.getAbilitiesManager().getAbilities()) {
						if (abilitiess.getRarity() != Rarity.USELESS && (player.hasPermission("kit." + abilitiess.getName().toLowerCase()) || player.hasPermission(abilitiess.getRarity().getPermission()) || player.hasPermission("kit.*")))
							abilities.add(abilitiess.getName());
					}
					if (abilities.isEmpty()) {
						return;
					}
					player.closeInventory();
					int random = (new Random()).nextInt(abilities.size());
					pm.getAbility().set(this.plugin.getAbilitiesManager().getAbilityFromName(abilities.get(random)));
					player.sendMessage(ChatColor.GRAY + "You've chosen " + pm.getAbility().get().getRarity().getColor() + pm.getAbility().get().getName() + ChatColor.GRAY + " ability.");
					map.giveEquipment(pm.getAbility().get());
					map.teleportToMap();
					abilities.clear();
					return;
				}
				if (!this.plugin.getAbilitiesManager().contains(correctItemName)) {
					return;
				}
				player.closeInventory();
				pm.getAbility().set(this.plugin.getAbilitiesManager().getAbilityFromName(correctItemName));
				player.sendMessage(ChatColor.GRAY + "You've chosen "
						+ pm.getAbility().get().getRarity().getColor()
						+ pm.getAbility().get().getName() + ChatColor.GRAY + " ability.");
				map.giveEquipment(pm.getAbility().get());
				map.teleportToMap();
			}
			return;
		}
		if (event.getInventory().getTitle().toLowerCase().contains("settings")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
				String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
				if (itemName.equals(" ") || itemName.length() < 3) {
					return;
				}
				Player player = (Player) event.getWhoClicked();
				PlayerManager pm = PlayerManager.get(player.getUniqueId());
				if (itemName.toLowerCase().contains(":")) {
					pm.getSettings().updateCompass();
					event.getInventory()
							.setContents(InventoryManager.getInstance().loadSettingsInventory(player).getContents());
					return;
				}
				player.closeInventory();
				if (itemName.toLowerCase().equals(ChatColor.YELLOW + "previous page")) {
					player.openInventory(InventoryManager.getInstance().loadKitsInventory(player));
					return;
				}
				if (itemName.toLowerCase().contains("slot")) {
					String name = itemName.split(" ")[0];
					name = name.substring(2, name.length());
					player.openInventory(InventoryManager.getInstance().loadSlotsInventory(player, name));
					return;
				}
			}
			return;
		}
		if (event.getInventory().getTitle().toLowerCase().contains("slot")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != null
					&& event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {

				Player player = (Player) event.getWhoClicked();
				PlayerSettings settings = PlayerManager.get(player.getUniqueId()).getSettings();
				String titleSplitted = event.getInventory().getTitle().split(" ")[1];
				if (!PlayerSettings.SlotType.contains(titleSplitted)) {
					return;
				}
				settings.setSlot(PlayerSettings.SlotType.getSlotTypeFromName(titleSplitted), event.getSlot());
				player.closeInventory();
				player.openInventory(InventoryManager.getInstance().loadSettingsInventory(player));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRefillInventoryLeave(InventoryCloseEvent event) {
		if (event.getInventory().getTitle().toLowerCase().contains("refill chest") && !event.getInventory().contains(Material.MUSHROOM_SOUP)) {
			RefillInventoryManager im = RefillInventoryManager.get(event.getInventory(), event.getPlayer().getLocation().getBlock().getBiome());
			im.setCooldown(Long.valueOf(60L));
			im.setFilled(false);
		}
	}
}
