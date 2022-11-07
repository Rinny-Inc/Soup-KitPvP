package io.noks.kitpvp.land;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.listeners.abilities.Archer;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Settings;
import io.noks.kitpvp.managers.caches.Settings.SlotType;

public class Land {
	private World world = Bukkit.getWorld("customjava8");
	private Location[] locations = new Location[] { new Location(this.world, 100.0D, 134.0D, 738.0D, 42.0F, 0.0F), new Location(this.world, 82.0D, 130.0D, 932.0D, 136.0F, 0.0F), new Location(this.world, -107.0D, 130.0D, 911.0D, -130.0F, 0.0F), new Location(this.world, -78.0D, 130.0D, 741.0D, -44.0F, 0.0F), new Location(this.world, -24.0D, 130.0D, 708.0D, -10.0F, 0.0F) };;
	private PlayerManager playerManager;
	
	public Land(PlayerManager pm) {
		this.playerManager = pm;
	}
	
	public boolean hasValidLocation() {
		return (this.locations.length > 0);
	}

	public void teleportToMap() {
		Player player = this.playerManager.getPlayer();
		player.setNoDamageTicks(100);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(10000.0F);
		player.teleport(this.locations[(new Random()).nextInt(this.locations.length)]);
		player.setItemOnCursor(null);
		this.playerManager.setAllowBuild(false);
	}

	public void giveEquipment(Abilities ability) {
		Player player = this.playerManager.getPlayer();
		player.setGameMode(GameMode.SURVIVAL);
		PlayerInventory inv = player.getInventory();
		inv.clear();
		inv.setArmorContents(null);
		inv.setItem(14, new ItemStack(Material.BOWL, 32));
		inv.setItem(13, new ItemStack(Material.RED_MUSHROOM, 32));
		inv.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 32));
		Settings settings = this.playerManager.getSettings();
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
