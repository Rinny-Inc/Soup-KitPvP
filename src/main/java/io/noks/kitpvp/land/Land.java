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

import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Settings;
import io.noks.kitpvp.utils.ItemUtils;

public class Land {
	protected World world = Bukkit.getWorld("customjava8");
	private Location[] locations = new Location[] { new Location(this.world, 100.0D, 134.0D, 738.0D, 42.0F, 0.0F), new Location(this.world, 82.0D, 130.0D, 932.0D, 136.0F, 0.0F), new Location(this.world, -107.0D, 130.0D, 911.0D, -130.0F, 0.0F), new Location(this.world, -78.0D, 130.0D, 741.0D, -44.0F, 0.0F), new Location(this.world, -24.0D, 130.0D, 708.0D, -10.0F, 0.0F) };;

	public boolean hasValidLocation() {
		return (this.locations.length > 0);
	}

	public void teleportToMap(Player player) {
		player.setNoDamageTicks(100);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(10000.0F);
		player.teleport(this.locations[(new Random()).nextInt(this.locations.length)]);
		player.setItemOnCursor(null);
		PlayerManager.get(player.getUniqueId()).setAllowBuild(false);
	}

	public void giveEquipment(Player player, AbilitiesEnum ability) {
		PlayerManager pm = PlayerManager.get(player.getUniqueId());
		player.setGameMode(GameMode.SURVIVAL);
		PlayerInventory inv = player.getInventory();
		inv.clear();
		inv.setArmorContents(null);
		inv.setItem(14, new ItemStack(Material.BOWL, 32));
		inv.setItem(13, new ItemStack(Material.RED_MUSHROOM, 32));
		inv.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 32));
		Settings settings = pm.getSettings();
		inv.setItem(settings.getSlot(Settings.SlotType.SWORD), new ItemStack(ItemUtils.getInstance()
				.getItemUnbreakable((ability == AbilitiesEnum.BOXER) ? Material.WOOD_SWORD : Material.STONE_SWORD)));
		if (ability.getSpecialItem().getType() != Material.MUSHROOM_SOUP)
			inv.setItem(settings.getSlot(Settings.SlotType.ITEM), ItemUtils.getInstance()
					.getItemStack(ability.getSpecialItem(), ChatColor.RED + ability.getSpecialItemName(), null));
		if (settings.hasCompass())
			inv.setItem(settings.getSlot(Settings.SlotType.COMPASS), new ItemStack(
					ItemUtils.getInstance().getItemMaterial(Material.COMPASS, ChatColor.YELLOW + "Tracker")));
		while (inv.firstEmpty() != -1) {
			inv.addItem(new ItemStack[] { new ItemStack(Material.MUSHROOM_SOUP) });
		}
		if (ability == AbilitiesEnum.ARCHER)
			inv.setItem(9, new ItemStack(Material.ARROW, 18));
		player.updateInventory();
	}

}
