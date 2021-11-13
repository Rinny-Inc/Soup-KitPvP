package us.noks.kitpvp.listeners.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import us.noks.kitpvp.Main;
import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.managers.PlayerManager;

public class Fisherman implements Listener {
	private Main plugin;

	public Fisherman(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onFish(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			event.setCancelled(true);
			return;
		}
		if (event.getCaught() instanceof Player && PlayerManager.get(event.getPlayer().getUniqueId()).getAbility().hasAbility(AbilitiesEnum.FISHERMAN)) {
			Player target = (Player) event.getCaught();
			Player player = event.getPlayer();
			if (target == player) return;
			target.teleport(player.getLocation());
			target.setFallDistance(0.0F);
		}
	}
}
