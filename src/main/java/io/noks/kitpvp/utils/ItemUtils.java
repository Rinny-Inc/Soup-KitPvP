package io.noks.kitpvp.utils;

import java.util.Arrays;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.listeners.abilities.Archer;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.PlayerSettings.SlotType;

public class ItemUtils {
	public ItemStack getItemStack(ItemStack item, @Nullable String name, @Nullable String[] lore) {
		final ItemStack i = item;
		final ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		if (lore != null) im.setLore(Arrays.asList(lore));
		i.setItemMeta(im);
		return i;
	}

	public ItemStack getItemMaterial(Material m, int data, String name) {
		final ItemStack i = new ItemStack(m, 1, (short) data);
		final ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		i.setItemMeta(im);
		return i;
	}

	public ItemStack getItemMaterial(Material m, String name) {
		return getItemMaterial(m, name, 1);
	}

	public ItemStack getItemMaterial(Material m, String name, int amount) {
		final ItemStack i = new ItemStack(m, amount);
		final ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		i.setItemMeta(im);
		return i;
	}

	public ItemStack getItemMaterial(Material m, int amount) {
		return new ItemStack(m, amount);
	}

	public ItemStack getItemUnbreakable(Material material) {
		return getItemUnbreakable(material, null);
	}

	public ItemStack getItemUnbreakable(Material material, @Nullable String name) {
		final ItemStack i = new ItemStack(material);
		final ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		im.spigot().setUnbreakable(true);
		i.setItemMeta(im);
		return i;
	}

	public ItemStack getAbilitiesSelector() {
		return getItemStack(new ItemStack(Material.BOOK), ChatColor.GRAY + "Your abilities", new String[] {ChatColor.GRAY + "Choose your ability to fight other players"});
	}
	
	public void giveEquipment(Player player, Abilities ability) {
		player.setGameMode(GameMode.SURVIVAL);
		final PlayerInventory inv = player.getInventory();
		inv.clear();
		inv.setArmorContents(null);
		inv.setItem(14, new ItemStack(Material.BOWL, 32));
		inv.setItem(13, new ItemStack(Material.RED_MUSHROOM, 32));
		inv.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 32));
		final PlayerSettings settings = PlayerManager.get(player.getUniqueId()).getSettings();
		inv.setItem(settings.getSlot(SlotType.SWORD), new ItemStack(Main.getInstance().getItemUtils().getItemUnbreakable(ability.sword())));
		if (ability.specialItem().getType() != Material.MUSHROOM_SOUP) {
			inv.setItem(settings.getSlot(SlotType.ITEM), Main.getInstance().getItemUtils().getItemStack(ability.specialItem(), ChatColor.RED + ability.specialItemName(), null));
		}
		if (settings.hasCompass()) {
			inv.setItem(settings.getSlot(SlotType.COMPASS), new ItemStack(Main.getInstance().getItemUtils().getItemMaterial(Material.COMPASS, ChatColor.YELLOW + "Tracker")));
		}
		while (inv.firstEmpty() != -1) {
			inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
		}
		if (ability instanceof Archer) {
			inv.setItem(9, new ItemStack(Material.ARROW, 18));
		}
		player.updateInventory();
	}
}
