package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class Batman extends Abilities implements Listener {
	private Main plugin;

	public Batman(Main main) {
		super("Batman", new ItemStack(Material.WOOD_SPADE), Rarity.RARE, 15L, new String[] { ChatColor.AQUA + "Teleport you to the hooked", ChatColor.AQUA + "player" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	public ItemStack specialItem() {
		return this.plugin.getItemUtils().getItemUnbreakable(Material.WOOD_SPADE);
	}

	public String specialItemName() {
		return "Batman Hook";
	}

	@EventHandler
	public void onBatman(PlayerInteractEvent e) {
		if (!e.hasItem()) {
			return;
		}
		Player p = e.getPlayer();
		Action action = e.getAction();
		Ability ability = PlayerManager.get(p.getUniqueId()).getAbility();
		if (action == Action.RIGHT_CLICK_AIR && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.WOOD_SPADE && ability.hasAbility(this)) {
			if (!ability.hasActiveCooldown()) {
				ability.applyCooldown();
				Arrow arrow = (Arrow) p.launchProjectile(Arrow.class);
				arrow.setMetadata("batHook", new FixedMetadataValue(this.plugin, Boolean.valueOf(true)));
				arrow.spigot().setDamage(0.0D);
				Vector handle = p.getEyeLocation().getDirection().multiply(3.0D);
				arrow.setVelocity(handle);
				return;
			}
			double cooldown = ability.getActiveCooldown().longValue() / 1000.0D;
			p.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
		}
	}

	@EventHandler
	public void onBatmanHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player && event.getHitEntity() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();

			if (event.getEntity().hasMetadata("batHook")) {
				Player hit = (Player) event.getHitEntity();

				if (hit == shooter) return;
				Location hitLoc = hit.getLocation();
				shooter.teleport(hitLoc);
				shooter.setFallDistance(0.0F);
			}
		}
	}
}
