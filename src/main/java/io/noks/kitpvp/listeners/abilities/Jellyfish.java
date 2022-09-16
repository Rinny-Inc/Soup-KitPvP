package io.noks.kitpvp.listeners.abilities;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Jellyfish implements Listener {
	private Main plugin;

	public Jellyfish(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onJellyFish(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() != null
				&& player.getItemInHand().getType() == Material.AIR
				&& PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.JELLYFISH)
				&& player.getFallDistance() > 2.5F) {
			final Block block = player.getWorld()
					.getBlockAt(event.getClickedBlock().getLocation().add(0.0D, 1.0D, 0.0D));
			final Material material = event.getClickedBlock().getLocation().add(0.0D, 1.0D, 0.0D).getBlock().getType();
			block.setType(Material.STATIONARY_WATER);
			if (block.getType() == Material.STATIONARY_WATER)
				block.setData((byte) 3);
			player.getWorld().playSound(player.getLocation(), Sound.SLIME_WALK, 0.85F, 1.2F);
			(new BukkitRunnable() {
				public void run() {
					block.setType(material);
				}
			}).runTaskLater(this.plugin, 10L);
		}
	}
}
