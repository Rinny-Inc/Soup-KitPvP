package io.noks.kitpvp.listeners.abilities;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.google.common.collect.Lists;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.utils.ItemUtils;

public class Jumper implements Listener {
	private Main plugin;
	private List<UUID> mount;

	public Jumper(Main main) {
		this.mount = Lists.newArrayList();
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player player = event.getPlayer();

			if (!event.hasItem()) {
				return;
			}
			ItemStack item = event.getItem();
			if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
					&& item.getType() == Material.ENDER_PEARL && player.getGameMode() != GameMode.CREATIVE
					&& this.mount.contains(player.getUniqueId())) {
				event.setUseItemInHand(Event.Result.DENY);
				player.updateInventory();
				player.sendMessage(ChatColor.RED + "You still riding your pearl!");
			}
		}
	}

	@EventHandler
	public void onLaunchEnderPearl(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();

			if (PlayerManager.get(shooter.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.JUMPER)
					&& !this.mount.contains(shooter.getUniqueId())) {
				this.mount.add(shooter.getUniqueId());
				event.getEntity().setPassenger(shooter);
			}
		}
	}

	@EventHandler
	public void onEntityDismount(EntityDismountEvent event) {
		if (event.getEntity() instanceof Player && event.getDismounted() instanceof EnderPearl) {
			Player player = (Player) event.getEntity();

			if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.JUMPER)) {
				this.mount.remove(player.getUniqueId());
				event.getDismounted().remove();
			}
		}
	}

	/*@EventHandler
	public void onEnderpearlCollideWithJumper(ProjectileCollideEvent event) {
		if (event.getCollidedWith() instanceof Player && event.getEntity() instanceof EnderPearl) {
			Player shooter = (Player) event.getCollidedWith();
			EnderPearl ender = (EnderPearl) event.getEntity();

			if (PlayerManager.get(shooter.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.JUMPER)
					&& ender.getPassenger() != null) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onJumperFall(EnderpearlLandEvent event) {
		if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
			Player player = (Player) event.getEntity().getShooter();
			if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.JUMPER)) {
				event.setCancelled(true);
				if (event.getEntity().getPassenger() != null && event.getHitEntity() != player) {
					player.eject();
					player.teleport(player.getLocation().add(0.0D, 2.0D, 0.0D));
				}
			}
		}
	}*/

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = event.getEntity().getKiller();
			PlayerManager pm = PlayerManager.get(player.getUniqueId());

			if (pm.getAbility().hasAbility(AbilitiesEnum.JUMPER)) {
				if (player.getInventory().firstEmpty() == -1 && !player.getInventory().contains(Material.ENDER_PEARL)) {
					player.getWorld().dropItem(player.getLocation(),
							ItemUtils.getInstance().getItemStack(new ItemStack(Material.ENDER_PEARL, 2),
									ChatColor.RED + pm.getAbility().get().getSpecialItemName(), null));
				} else {
					player.getInventory()
							.addItem(new ItemStack[] {
									ItemUtils.getInstance().getItemStack(new ItemStack(Material.ENDER_PEARL, 2),
											ChatColor.RED + pm.getAbility().get().getSpecialItemName(), null) });
				}
			}
		}
		if (this.mount.contains(event.getEntity().getUniqueId()))
			this.mount.remove(event.getEntity().getUniqueId());

	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		if (this.mount.contains(event.getPlayer().getUniqueId()))
			this.mount.remove(event.getPlayer().getUniqueId());
	}
}
