package us.noks.kitpvp.listeners.abilities;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import us.noks.kitpvp.Main;
import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.managers.PlayerManager;

public class Poseidon implements Listener {
	private Main plugin;

	public Poseidon(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onPoseidon(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (PlayerManager.get(p.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.POSEIDON)) {
			if (!e.isCancelled()) {
				e.getFrom().getBlock().getLocation();
				e.getTo().getBlock().getLocation();
			}
			Biome biome = p.getLocation().getBlock().getBiome();
			if (biome == Biome.RIVER && !p.hasPotionEffect(PotionEffectType.SPEED)
					&& !p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
			}
		}
	}

	@EventHandler
	public void onPoseidonSuffocating(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
			Player player = (Player) event.getEntity();

			if (PlayerManager.get(player.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.POSEIDON))
				event.setCancelled(true);
		}
	}
}
