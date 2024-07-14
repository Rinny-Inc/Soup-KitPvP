package io.noks.kitpvp.abstracts;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.noks.kitpvp.enums.Rarity;

public abstract class Abilities implements Cloneable {
	private final String name;
	private final ItemStack icon;
	private final Rarity rarity;
	private final Long cooldown;
	private final String[] lore;
	protected static final DecimalFormat df = new DecimalFormat("#.#");
	
	public Abilities(String name, ItemStack icon, Rarity rarity, Long cooldown, String[] lore) {
		this.name = name;
		this.icon = icon;
		this.rarity = rarity;
		this.cooldown = cooldown;
		this.lore = lore;
	}
	
	public ItemStack specialItem() {
		return new ItemStack(Material.MUSHROOM_SOUP);
	}
	
	public String specialItemName() {
		return null;
	}

	public ItemStack sword() {
		final ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		final ItemMeta meta = item.getItemMeta();
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		return item;
	}
	
	public ItemStack[] armors() {
		return new ItemStack[] {new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_HELMET)};
	}
	
	public boolean hasCooldown() {
		return (this.cooldown.longValue() != 0L);
	}
	
	public String getName() {
		return this.name;
	}
	
	public ItemStack getIcon() {
		return this.icon;
	}
	
	public Rarity getRarity() {
		return this.rarity;
	}
	
	public Long getCooldown() {
		return this.cooldown;
	}
	
	public String[] getLore() {
		return this.lore;
	}
	
	public List<PotionEffect> potionEffect() {
		return Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
	}
	
	public boolean needCloning() {
		return false;
	}
	
	/*public void giveEquipment(Player player) {
		
	}*/
	
	public void onKill(Player killer) {}
	
	public void onInteract(PlayerInteractEvent event) {}
	public void onToggleSneak(PlayerToggleSneakEvent event) {}
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {}
	public void leaveAction(Player player) {}
	public void onDeath(PlayerDeathEvent event) {}
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {}
	
	@Override
    public Abilities clone() {
        try {
            return (Abilities) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
