package io.noks.kitpvp.listeners.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Fisherman extends Abilities implements Listener {
	private Main plugin;

	public Fisherman(Main main) {
		super("Fisherman", new ItemStack(Material.FISHING_ROD), Rarity.RARE, 0L, new String[] { ChatColor.AQUA + "Catch your opponent with your rod" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@Override
	public ItemStack specialItem() {
		return this.getUnbreakableItemStack(this.getIcon().getType());
	}
	
	@Override
	public String specialItemName() {
		return "Fisherman Rod";
	}

	@EventHandler
	public void onFish(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			event.setCancelled(true);
			return;
		}
		if (event.getCaught() instanceof Player && PlayerManager.get(event.getPlayer().getUniqueId()).getAbility().hasAbility(this)) {
			Player target = (Player) event.getCaught();
			Player player = event.getPlayer();
			if (target == player) return;
			target.teleport(player.getLocation());
			target.setFallDistance(0.0F);
		}
	}
}
