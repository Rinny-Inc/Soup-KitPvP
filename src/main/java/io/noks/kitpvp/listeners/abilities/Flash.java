package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class Flash implements Listener {
	private Main plugin;

	public Flash(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onFlash(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action action = e.getAction();
		Ability ability = PlayerManager.get(p.getUniqueId()).getAbility();
		if (action == Action.RIGHT_CLICK_AIR && p.getItemInHand().getType() != null
				&& p.getItemInHand().getType() == Material.REDSTONE_TORCH_ON && ability.hasAbility(AbilitiesEnum.FLASH))
			if (!ability.hasActiveCooldown()) {
				Block block = p.getTargetBlock(null, 85);
				if (block.getType() != Material.AIR)
					block = p.getTargetBlock(null, 45);
				if (block.getType() != Material.AIR)
					return;
				ability.applyCooldown();
				Location loc = block.getLocation();
				p.teleport(loc);
				p.setFallDistance(0.0F);
				p.getWorld().strikeLightningEffect(loc);
			} else {
				double cooldown = ability.getActiveCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
			}
	}
}
