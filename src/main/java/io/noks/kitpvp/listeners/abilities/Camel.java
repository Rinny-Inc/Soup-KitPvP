package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class Camel extends Abilities implements Listener {
	private Main plugin;

	public Camel(Main main) {
		super("Camel", new ItemStack(Material.SAND), Rarity.COMMON, 0L, new String[] { ChatColor.AQUA + "In the desert biome you run like a camel" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onCamelMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Ability ability = PlayerManager.get(player.getUniqueId()).getAbility();
		if (ability.hasAbility(this)) {
			if (player.getLocation().getBlock().getBiome() == Biome.DESERT) {
				if (player.getWalkSpeed() != 0.275F) player.setWalkSpeed(0.275F);
				return;
			}
			if (player.getWalkSpeed() != 0.2F)
				player.setWalkSpeed(0.2F);
		}
	}
}
