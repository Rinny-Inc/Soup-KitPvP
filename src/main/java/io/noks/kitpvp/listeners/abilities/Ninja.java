package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.avaje.ebean.validation.NotNull;

import io.noks.collections.TtlArrayList;
import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Ninja extends Abilities implements Listener {
	private final @NotNull Main plugin;
	private @Nullable TtlArrayList<UUID> target;

	public Ninja(Main main) {
		super("Ninja", new ItemStack(Material.WOOL, 1, (short) 15), Rarity.LEGENDARY, 20L, new String[] { ChatColor.AQUA + "Teleport yourself behind your opponent" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack[] armors() {
		final ItemStack h = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) h.getItemMeta();
		meta.setColor(Color.BLACK);
		h.setItemMeta(meta);
		h.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		h.addUnsafeEnchantment(Enchantment.DURABILITY, 20);
		final ItemStack c = new ItemStack(Material.LEATHER_CHESTPLATE);
		meta = (LeatherArmorMeta) c.getItemMeta();
		meta.setColor(Color.BLACK);
		c.setItemMeta(meta);
		c.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		c.addUnsafeEnchantment(Enchantment.DURABILITY, 20);
		final ItemStack l = new ItemStack(Material.LEATHER_LEGGINGS);
		meta = (LeatherArmorMeta) l.getItemMeta();
		meta.setColor(Color.BLACK);
		l.setItemMeta(meta);
		l.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		l.addUnsafeEnchantment(Enchantment.DURABILITY, 20);
		final ItemStack b = new ItemStack(Material.LEATHER_BOOTS);
		meta = (LeatherArmorMeta) b.getItemMeta();
		meta.setColor(Color.BLACK);
		b.setItemMeta(meta);
		b.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		b.addUnsafeEnchantment(Enchantment.DURABILITY, 20);
		return new ItemStack[] {b, l, c, h};
	}
	
	@Override
	public ItemStack sword() {
		final ItemStack sword = new ItemStack(Material.IRON_SWORD);
		final ItemMeta meta = sword.getItemMeta();
		meta.spigot().setUnbreakable(true);
		sword.setItemMeta(meta);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		return sword;
	}
	
	@EventHandler
	public void onToggleSneak(PlayerToggleSneakEvent event) {
		final Player player = event.getPlayer();
		if (target != null && !target.isEmpty()) {
			if (!event.isSneaking()) {
				return;
			}
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());
			if (!pm.hasAbility(this)) {
				return;
			}
			final Player target = Bukkit.getPlayer(this.target.get(0));
			if (target == null) {
				return;
			}
			if (!player.canSee(target) || !target.canSee(player)) {
				this.target = null;
				return;
			}
			if (PlayerManager.get(target.getUniqueId()).isInSpawn()) {
				this.target = null;
				return;
			}
			if (pm.hasActiveCooldown()) {
				double cooldown = pm.getActiveCooldown().longValue() / 1000.0D;
				player.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
				return;
			}
			pm.applyCooldown();
			float nang = target.getLocation().getYaw() + 90.0F;
			if (nang < 0.0F) {
				nang += 360.0F;
			}
			final double nX = Math.cos(Math.toRadians(nang));
			final double nZ = Math.sin(Math.toRadians(nang));
			Location behindTargetLocation = new Location(player.getWorld(), target.getLocation().getX() - nX, target.getLocation().getY(), target.getLocation().getZ() - nZ, target.getLocation().getYaw(), target.getLocation().getPitch());
			if (behindTargetLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
				behindTargetLocation = target.getLocation();
			}
			if (behindTargetLocation.getBlock().getType() != Material.AIR) {
				behindTargetLocation = target.getLocation();
			}
			player.teleport(behindTargetLocation);
			for (int i = 0; i < 6; i++) {
				player.getWorld().playEffect(player.getLocation(), Effect.LARGE_SMOKE, 10);
			}
			player.setFallDistance(0.0F);
			this.target.clear();
		}
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final Player damager = (Player) event.getDamager();
			final PlayerManager dm = PlayerManager.get(damager.getUniqueId());

			if (dm.hasAbility(this)) {
				Player damaged = (Player) event.getEntity();
				if (this.target == null) {
					this.target = new TtlArrayList<>(TimeUnit.SECONDS, 30, 1);
				}
				if (!this.target.isEmpty() && this.target.get(0) == damaged.getUniqueId()) {
					return;
				}
				this.target.removeFirst();
				this.target.add(damaged.getUniqueId());
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (this.target != null && !this.target.isEmpty() && this.target.get(0) == event.getPlayer().getUniqueId()) {
			this.target.clear();
			return;
		}
		final PlayerManager dm = PlayerManager.get(event.getPlayer().getUniqueId());
		if (dm.hasAbility(this)) {
			this.target = null;
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			if (this.target != null && !this.target.isEmpty() && this.target.get(0) == event.getEntity().getUniqueId()) {
				this.target.clear();
				return;
			}
			final PlayerManager dm = PlayerManager.get(event.getEntity().getUniqueId());
			if (dm.hasAbility(this)) {
				this.target = null;
			}
		}
	}
}
