package io.noks.kitpvp.listeners.abilities;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Pacifist extends Abilities implements Listener {

	public Pacifist(Main main) {
		super("Pacifist", new ItemStack(Material.RED_ROSE, 1, (short) 8), Rarity.BETA, 0L, new String[] {"When any inventory is open, take half damage"});
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@Override
	public ItemStack[] armors() {
		final ItemStack h = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) h.getItemMeta();
		meta.setColor(Color.WHITE);
		h.setItemMeta(meta);
		h.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		h.addUnsafeEnchantment(Enchantment.DURABILITY, 20);
		final ItemStack c = new ItemStack(Material.LEATHER_CHESTPLATE);
		meta = (LeatherArmorMeta) c.getItemMeta();
		meta.setColor(Color.WHITE);
		c.setItemMeta(meta);
		c.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		c.addUnsafeEnchantment(Enchantment.DURABILITY, 20);
		final ItemStack l = new ItemStack(Material.LEATHER_LEGGINGS);
		meta = (LeatherArmorMeta) l.getItemMeta();
		meta.setColor(Color.WHITE);
		l.setItemMeta(meta);
		l.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		l.addUnsafeEnchantment(Enchantment.DURABILITY, 20);
		final ItemStack b = new ItemStack(Material.LEATHER_BOOTS);
		meta = (LeatherArmorMeta) b.getItemMeta();
		meta.setColor(Color.WHITE);
		b.setItemMeta(meta);
		b.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		b.addUnsafeEnchantment(Enchantment.DURABILITY, 20);
		return new ItemStack[] {b, l, c, h};
	}
	
	@Override
	public ItemStack sword() {
		final ItemStack sword = this.getIcon().clone();
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
		return sword;
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && PlayerManager.get(event.getEntity().getUniqueId()).hasAbility(this)) {
			final Player player = (Player) event.getEntity();
			
			if(player.getOpenInventory() != null) {
				final double newDamage = event.getDamage() / 2;
				event.setDamage(newDamage);
			}
		}
	}
}
