package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Monk extends Abilities implements Listener {
	private Main plugin;

	public Monk(Main main) {
		super("Monk", new ItemStack(Material.BLAZE_ROD), Rarity.UNCOMMON, 15L, new String[] { ChatColor.AQUA + "Switch your opponent sword", ChatColor.AQUA + "with another item in his", ChatColor.AQUA + "inventory" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getIcon();
	}
	
	@Override
	public String specialItemName() {
		return "Monk Staff";
	}
	
	@Override
	public ItemStack sword() {
		final ItemStack sword = new ItemStack(Material.WOOD_SWORD);
		final ItemMeta meta = sword.getItemMeta();
		meta.spigot().setUnbreakable(true);
		sword.setItemMeta(meta);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
		return sword;
	}
	
	@Override
	public ItemStack[] armors() {
		ItemStack[] armor = super.armors();
		final ItemStack h = new ItemStack(Material.LEATHER_HELMET);
		final LeatherArmorMeta meta = (LeatherArmorMeta) h.getItemMeta();
		meta.setColor(Color.ORANGE);
		h.setItemMeta(meta);
		h.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
		armor[3] = h;
		return armor;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEntityEvent event) {
		final ItemStack item = event.getPlayer().getItemInHand();
		final Player player = event.getPlayer();
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm.hasAbility(this) && event.getRightClicked() instanceof Player && item.getType() == Material.BLAZE_ROD) {
			if (pm.hasActiveAbilityCooldown()) {
				double cooldown = pm.getActiveAbilityCooldown().longValue() / 1000.0D;
				player.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
				return;
			}
			pm.applyAbilityCooldown();
			final Player rightClicked = (Player) event.getRightClicked();
			final PlayerInventory inv = rightClicked.getInventory();
			final int slot = (new Random()).nextInt(inv.getSize());
			ItemStack replaced = inv.getItemInHand();
			if (replaced == null)
				replaced = new ItemStack(Material.AIR);
			ItemStack replacer = inv.getItem(slot);
			if (replacer == null)
				replacer = new ItemStack(Material.AIR);
			inv.setItemInHand(replacer);
			inv.setItem(slot, replaced);
			rightClicked.updateInventory();
		}
	}
}
