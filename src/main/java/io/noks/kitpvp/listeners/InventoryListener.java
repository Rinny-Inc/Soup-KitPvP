package io.noks.kitpvp.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
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
import io.noks.kitpvp.interfaces.SignRotation;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.RefillInventoryManager;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.PlayerSettings.SlotType;

public class InventoryListener implements Listener, SignRotation {
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
			if (!pm.hasAbility() && player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
				player.updateInventory();
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		final Inventory inventory = event.getClickedInventory();
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
		final String title = ChatColor.stripColor(inventory.getTitle()).toLowerCase();
		if (title.contains("slot")) {
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			PlayerSettings settings = PlayerManager.get(player.getUniqueId()).getSettings();
			String titleSplitted = title.split(" ")[0];
			if (!SlotType.contains(titleSplitted)) {
				return;
			}
			settings.setSlot(SlotType.getSlotTypeFromName(titleSplitted), event.getSlot());
			player.closeInventory();
			player.openInventory(this.plugin.getInventoryManager().loadSettingsInventory(player));
			return;
		}
		switch (title) {
			case "ability selector" -> {
				event.setCancelled(true);
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
					final int random = (new Random()).nextInt(abilities.size());
					pm.setSelectedAbility(this.plugin.getAbilitiesManager().getAbilityFromName(abilities.get(random)));
					final Abilities ab = pm.getSelectedAbility();
					player.sendMessage(ChatColor.GRAY + "You've chosen " + ab.getRarity().getColor() + ab.getName() + ChatColor.GRAY + " ability.");
					abilities.clear();
					abilities = null;
					return;
				}
				if (!this.plugin.getAbilitiesManager().contains(correctItemName)) {
					return;
				}
				player.closeInventory();
				pm.setSelectedAbility(this.plugin.getAbilitiesManager().getAbilityFromName(correctItemName));
				player.sendMessage(ChatColor.GRAY + "You've chosen " + pm.getSelectedAbility().getRarity().getColor() + pm.getSelectedAbility().getName() + ChatColor.GRAY + " ability.");
			}
			case "shop" -> {
				event.setCancelled(true);
				Player player = (Player) event.getWhoClicked();
				final int clickedSlot = event.getSlot();
				switch (clickedSlot) {
					case 0 -> {
						player.closeInventory();
						player.performCommand("repair");
						return;
					}
					case 1 -> {
						Economy eco = PlayerManager.get(player.getUniqueId()).getEconomy();
						final int price = 150;
						if (eco.getMoney() < price) {
							player.sendMessage(ChatColor.RED + "Missing " + (price - eco.getMoney()) + " credits!");
							break;
						}
						player.closeInventory();
						eco.remove(price);
						player.sendMessage(ChatColor.GREEN + "Successfully bought Golden Apples for " + price + " credits!");
						player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 3));
						return;
					}
					case 2 -> {
						Economy eco = PlayerManager.get(player.getUniqueId()).getEconomy();
						final int price = 25;
						if (eco.getMoney() < price) {
							player.sendMessage(ChatColor.RED + "Missing " + (price - eco.getMoney()) + " credits!");
							break;
						}
						player.closeInventory();
						eco.remove(price);
						player.sendMessage(ChatColor.GREEN + "Successfully bought GrandPa Stick for " + price + " credits!");
						player.getInventory().addItem(event.getCurrentItem());
						return;
					}
					case 8 -> {
						final PlayerManager pm = PlayerManager.get(player.getUniqueId());
						player.openInventory(this.plugin.getInventoryManager().loadPerksInventory(pm));
					}
				}
			}
			case "your settings" -> {
				event.setCancelled(true);
				String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase());
				if (itemName.equals(" ") || itemName.length() < 3) {
					return;
				}
				Player player = (Player) event.getWhoClicked();
				player.closeInventory();
				if (itemName.equals("previous page")) {
					player.openInventory(this.plugin.getInventoryManager().loadKitsInventory(player));
					return;
				}
				if (itemName.equals("scoreboard")) {
					player.sendMessage(ChatColor.RED + "Scoreboard is coming right back real soon ^^");
					/*final PlayerManager pm = PlayerManager.get(player.getUniqueId());
					pm.getSettings().updateScoreboardState();
					if (pm.getSettings().hasScoreboardEnabled()) {
						pm.applyScoreboard();
					} else {
						player.setScoreboard(this.plugin.getServer().getScoreboardManager().getMainScoreboard());
					}
					player.openInventory(this.plugin.getInventoryManager().loadSettingsInventory(player));*/
					return;
				}
				if (itemName.contains("slot")) {
					String name = itemName.split(" ")[0];
					name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
					player.openInventory(this.plugin.getInventoryManager().loadSlotsInventory(player, name));
					return;
				}
			}
			case "your perks" -> {
				event.setCancelled(true);
				// TODO
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRefillInventoryLeave(InventoryCloseEvent event) {
		if (event.getInventory().getType() != InventoryType.CHEST) {
			return;
		}
		final Inventory inventory = event.getInventory();
		final String title = ChatColor.stripColor(inventory.getTitle()).toLowerCase();
		if (title.equals("refill chest") && !inventory.contains(Material.MUSHROOM_SOUP)) {
			final Player player = (Player) event.getPlayer();
			Location location = null;
			Block block = null;
			
			for (int i = 0; i < 8; i++) {
				block = player.getTargetBlock(null, i);
				if (block.getType() == Material.WALL_SIGN) {
					Sign sign = (Sign) block.getState();
					Block blockBehind = this.getBlockBehindSign(block, sign);
					if (blockBehind.getType() == Material.GLOWSTONE && blockBehind.getRelative(BlockFace.UP).getType() == Material.WOOL) {
						location = blockBehind.getLocation();
						break;
					}
				}
			}
	        if (location == null) {
	        	return;
	        }
			RefillInventoryManager.get(location).setCooldown(60L);
		}
	}
}
