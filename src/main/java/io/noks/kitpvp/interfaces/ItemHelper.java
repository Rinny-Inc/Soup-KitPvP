package io.noks.kitpvp.interfaces;

import java.util.Arrays;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import com.avaje.ebean.validation.NotNull;

import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.PlayerSettings.SlotType;

public interface ItemHelper {
	default ItemStack getItemStack(ItemStack item, @Nullable String name, @Nullable String[] lore) {
		final ItemStack i = item;
		final ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		if (lore != null) im.setLore(Arrays.asList(lore));
		i.setItemMeta(im);
		return i;
	}

	default ItemStack getItemMaterial(Material m, int data, String name) {
		final ItemStack i = new ItemStack(m, 1, (short) data);
		final ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		i.setItemMeta(im);
		return i;
	}

	default ItemStack getItemMaterial(Material m, String name) {
		return getItemMaterial(m, name, 1);
	}

	default ItemStack getItemMaterial(Material m, String name, int amount) {
		final ItemStack i = new ItemStack(m, amount);
		final ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		i.setItemMeta(im);
		return i;
	}

	default ItemStack getItemMaterial(Material m, int amount) {
		return new ItemStack(m, amount);
	}

	default ItemStack getItemUnbreakable(Material material) {
		return getItemUnbreakable(material, null);
	}

	default ItemStack getItemUnbreakable(Material material, @Nullable String name) {
		final ItemStack i = new ItemStack(material);
		final ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		im.spigot().setUnbreakable(true);
		i.setItemMeta(im);
		return i;
	}
	
	default ItemStack getPlayerHead(String name, String displayName) {
		final ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
		final SkullMeta sm = (SkullMeta) i.getItemMeta();
		sm.setOwner(name);
		sm.setDisplayName(displayName);
		i.setItemMeta(sm);
		return i;
	}
	
	default ItemStack createGlassBlock(DyeColor color) {
		final ItemStack i = new ItemStack(Material.GLASS);
		final MaterialData data = i.getData();
        data.setData(color.getWoolData());
        i.setData(data);
        return i;
	}

	@NotNull
	default ItemStack[] getSpawnItems(String name) {
		return new ItemStack[] {getItemStack(new ItemStack(Material.ENCHANTED_BOOK), ChatColor.DARK_AQUA + "Ability Selector", new String[] {ChatColor.GRAY + "Choose your ability to fight other players"}),
								getItemStack(new ItemStack(Material.CHEST), ChatColor.DARK_AQUA + "Perk Selector", new String[] {ChatColor.RED + "Coming Soon"}), 
								null,
								null, 
								getPlayerHead(name, ChatColor.DARK_AQUA + "Stats"),
								null,
								null,
								getItemStack(new ItemStack(Material.WATCH), ChatColor.DARK_AQUA + "Settings", new String[] {ChatColor.GRAY + "Edit your settings"}),
								getItemStack(new ItemStack(Material.NETHER_STAR), ChatColor.DARK_AQUA + "Shop", new String[] {ChatColor.GRAY + "Enter the shop"})};
	}
	
	default void giveEquipment(Player player, Abilities ability) {
		player.setGameMode(GameMode.SURVIVAL);
		final PlayerInventory inv = player.getInventory();
		for (ItemStack items : inv.getContents()) {
			if (items == null) {
				continue;
			}
			for (ItemStack spawnItems : getSpawnItems(player.getName())) {
				if (spawnItems != null && items.getItemMeta().getDisplayName().equals(spawnItems.getItemMeta().getDisplayName())) {
					inv.remove(spawnItems.getType());
				}
			}
		}
		inv.setArmorContents(null);
		inv.setArmorContents(ability.armors());
		final PlayerSettings settings = PlayerManager.get(player.getUniqueId()).getSettings();
		inv.setItem(settings.getSlot(SlotType.SWORD), ability.sword());
		if (ability.specialItem().getType() != Material.MUSHROOM_SOUP) {
			inv.setItem(settings.getSlot(SlotType.ITEM), getItemStack(ability.specialItem(), ChatColor.RED + ability.specialItemName(), null));
		}
		switch(ability.getName().toLowerCase()) {
			case "jesus": {
				// TODO
				break;
			}
			case "sniper": {
				inv.setItem(9, new ItemStack(Material.ARROW, 18));
				break;
			}
			case "chemist": {
				inv.setItem(2, new ItemStack(Material.POTION, 1, (short) 16420));
				break;
			}
			default: break;
		}
		while (inv.firstEmpty() != -1) {
			inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
		}
		player.updateInventory();
		player.addPotionEffects(ability.potionEffect());
	}
}
