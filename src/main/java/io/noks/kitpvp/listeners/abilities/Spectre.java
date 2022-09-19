package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.AbilitiesEnum;
import io.noks.kitpvp.managers.PlayerManager;

public class Spectre implements Listener {
	private Main plugin;

	public Spectre(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onSpectre(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action action = e.getAction();
		if (action == Action.RIGHT_CLICK_AIR && p.getItemInHand().getType() != null
				&& p.getItemInHand().getType() == Material.SUGAR
				&& PlayerManager.get(p.getUniqueId()).getAbility().hasAbility(AbilitiesEnum.SPECTRE)) {
			PlayerManager pm = PlayerManager.get(p.getUniqueId());
			if (!pm.getAbility().hasActiveCooldown()) {
				pm.getAbility().applyCooldown();
				p.sendMessage(ChatColor.GREEN + "You are now invisible!");
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 1));
			} else {
				double cooldown = pm.getAbility().getActiveCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in "
						+ (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
			}
		}
	}
}
