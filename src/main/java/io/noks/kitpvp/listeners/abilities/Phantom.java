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
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Phantom implements Listener {
	private Main plugin;

	public Phantom(Main main) {
		this.equipments = Maps.newHashMap();

		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	private Map<UUID, ItemStack[]> equipments;

	@EventHandler
	public void onJellyFish(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action action = e.getAction();
		if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				&& p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.FEATHER
				&& PlayerManager.get(p.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.PHANTOM)) {
			PlayerManager pm = PlayerManager.get(p.getUniqueId());
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
				(new BukkitRunnable() {
					public void run() {
						if (Phantom.this.equipments.containsKey(p.getUniqueId())) {
							p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 1.0F, 1.0F);
							p.sendMessage(ChatColor.RED + "Fly unabled!");
							p.setFlying(false);
							p.setAllowFlight(false);
							p.getInventory()
									.setArmorContents((ItemStack[]) Phantom.this.equipments.get(p.getUniqueId()));
							p.updateInventory();
							Phantom.this.equipments.remove(p.getUniqueId());
						}
					}
				}).runTaskLater(this.plugin, 120L);
			} else {
				double cooldown = pm.getAbility().getActiveCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in "
						+ (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
			}
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

		ItemStack h = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta hm = (LeatherArmorMeta) h.getItemMeta();
		hm.setColor(Color.fromRGB(255, 255, 255));
		h.setItemMeta(hm);

		ItemStack c = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta cm = (LeatherArmorMeta) c.getItemMeta();
		cm.setColor(Color.fromRGB(255, 255, 255));
		c.setItemMeta(cm);

		ItemStack l = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta lm = (LeatherArmorMeta) l.getItemMeta();
		lm.setColor(Color.fromRGB(255, 255, 255));
		l.setItemMeta(lm);

		ItemStack b = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bm = (LeatherArmorMeta) b.getItemMeta();
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
