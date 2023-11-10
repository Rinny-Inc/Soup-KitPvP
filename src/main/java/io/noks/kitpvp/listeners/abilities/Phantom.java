package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Phantom extends Abilities implements Listener {
	private Main plugin;
	private Map<UUID, ItemStack[]> equipments;
	
	public Phantom(Main main) {
		super("Phantom", new ItemStack(Material.FEATHER), Rarity.RARE, 30L, new String[] { ChatColor.AQUA + "Fly for 5 seconds" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
		this.equipments = Maps.newHashMap();
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getIcon();
	}
	
	@Override
	public String specialItemName() {
		return "Phantom Feather";
	}

	@EventHandler
	public void onPhantom(PlayerInteractEvent e) {
		if (!e.hasItem()) {
			return;
		}
		final Player p = e.getPlayer();
		final Action action = e.getAction();
		if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.FEATHER && PlayerManager.get(p.getUniqueId()).getAbility().hasAbility(this)) {
			final PlayerManager pm = PlayerManager.get(p.getUniqueId());
			if (!pm.getAbility().hasActiveCooldown()) {
				pm.getAbility().applyCooldown();
				for (Entity n : p.getNearbyEntities(20.0D, 20.0D, 20.0D)) {
					if (n instanceof Player) {
						Player nearby = (Player) n;
						nearby.playSound(p.getLocation(), Sound.WITHER_DEATH, 2.0F, 2.0F);
					}
				}
				p.setAllowFlight(true);
				p.setFlying(true);
				p.sendMessage(ChatColor.BLUE + "You can now flying for 5 seconds");
				p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1.0F, 1.0F);
				givePhantomEquipment(p);
				new BukkitRunnable() {
					public void run() {
						if (Phantom.this.equipments.containsKey(p.getUniqueId())) {
							p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 1.0F, 1.0F);
							p.sendMessage(ChatColor.RED + "Fly unabled!");
							p.setFlying(false);
							p.setAllowFlight(false);
							p.getInventory().setArmorContents((ItemStack[]) Phantom.this.equipments.get(p.getUniqueId()));
							p.updateInventory();
							Phantom.this.equipments.remove(p.getUniqueId());
						}
					}
				}.runTaskLater(this.plugin, 120L);
				return;
			}
			final double cooldown = pm.getAbility().getActiveCooldown().longValue() / 1000.0D;
			p.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player && this.equipments.containsKey(event.getEntity().getUniqueId()))
			this.equipments.remove(event.getEntity().getUniqueId());

	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if (this.equipments.containsKey(event.getPlayer().getUniqueId()))
			this.equipments.remove(event.getPlayer().getUniqueId());
	}

	private void givePhantomEquipment(Player player) {
		this.equipments.put(player.getUniqueId(), player.getInventory().getArmorContents());

		final ItemStack h = new ItemStack(Material.LEATHER_HELMET);
		final LeatherArmorMeta hm = (LeatherArmorMeta) h.getItemMeta();
		hm.setColor(Color.fromRGB(255, 255, 255));
		h.setItemMeta(hm);

		final ItemStack c = new ItemStack(Material.LEATHER_CHESTPLATE);
		final LeatherArmorMeta cm = (LeatherArmorMeta) c.getItemMeta();
		cm.setColor(Color.fromRGB(255, 255, 255));
		c.setItemMeta(cm);

		final ItemStack l = new ItemStack(Material.LEATHER_LEGGINGS);
		final LeatherArmorMeta lm = (LeatherArmorMeta) l.getItemMeta();
		lm.setColor(Color.fromRGB(255, 255, 255));
		l.setItemMeta(lm);

		final ItemStack b = new ItemStack(Material.LEATHER_BOOTS);
		final LeatherArmorMeta bm = (LeatherArmorMeta) b.getItemMeta();
		bm.setColor(Color.fromRGB(255, 255, 255));
		b.setItemMeta(bm);

		player.getInventory().setArmorContents(null);
		player.getInventory().setHelmet(h);
		player.getInventory().setChestplate(c);
		player.getInventory().setLeggings(l);
		player.getInventory().setBoots(b);
		player.updateInventory();
	}
}
