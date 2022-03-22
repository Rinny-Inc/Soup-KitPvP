package us.noks.kitpvp.land;

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

import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.managers.PlayerManager;
import us.noks.kitpvp.managers.caches.Settings;
import us.noks.kitpvp.utils.ItemUtils;

public class Land {
	private final World world;

	public Land(PlayerManager manager) {
	    this.world = Bukkit.getWorld("world");
	    this.locations = new Location[] { new Location(this.world, 100.0D, 134.0D, 738.0D, 42.0F, 0.0F), new Location(this.world, 82.0D, 130.0D, 932.0D, 136.0F, 0.0F), new Location(this.world, -107.0D, 130.0D, 911.0D, -130.0F, 0.0F), new Location(this.world, -78.0D, 130.0D, 741.0D, -44.0F, 0.0F), new Location(this.world, -24.0D, 130.0D, 708.0D, -10.0F, 0.0F) };
	    this.manager = manager;
	  }

	private final Location[] locations;
	private PlayerManager manager;

	public PlayerManager getManager() {
		return this.manager;
	}

	public boolean hasValidLocation() {
		return (this.locations.length > 0);
	}

	public void teleportToMap() {
		Player player = this.manager.getPlayer();
		player.setNoDamageTicks(100);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(10000.0F);
		player.teleport(this.locations[(new Random()).nextInt(this.locations.length)]);
		player.setItemOnCursor(null);
		this.manager.setAllowBuild(false);
	}

	public void giveEquipment(AbilitiesEnum ability) {
		Player player = this.manager.getPlayer();
		player.setGameMode(GameMode.SURVIVAL);
		PlayerInventory inv = player.getInventory();
		inv.clear();
		inv.setArmorContents(null);
		inv.setItem(14, new ItemStack(Material.BOWL, 32));
		inv.setItem(13, new ItemStack(Material.RED_MUSHROOM, 32));
		inv.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 32));
		Settings settings = this.manager.getSettings();
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
