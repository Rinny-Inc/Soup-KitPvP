package io.noks.kitpvp.listeners.abilities;

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

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Batman extends Abilities implements Listener {
	private Main plugin;

	public Batman(Main main) {
		super("Batman", new ItemStack(Material.WOOD_SPADE), Rarity.RARE, 15L, new String[] { ChatColor.AQUA + "Teleport you to the hooked", ChatColor.AQUA + "player" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	public ItemStack specialItem() {
		return this.plugin.getItemUnbreakable(Material.WOOD_SPADE);
	}

	public String specialItemName() {
		return "Batman Hook";
	}

	@EventHandler
	public void onBatman(PlayerInteractEvent e) {
		if (!e.hasItem()) {
			return;
		}
		final Player p = e.getPlayer();
		final Action action = e.getAction();
		final PlayerManager pm = PlayerManager.get(p.getUniqueId());
		if (action == Action.RIGHT_CLICK_AIR && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.WOOD_SPADE && pm.hasAbility(this)) {
			if (pm.hasActiveAbilityCooldown()) {
				final double cooldown = pm.getActiveAbilityCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in " + df.format(cooldown) + " seconds.");
				return;
			}
			pm.applyAbilityCooldown();
			final Arrow arrow = p.launchProjectile(Arrow.class, p.getLocation().getDirection().multiply(3.0D));
			arrow.setMetadata("batHook", new FixedMetadataValue(this.plugin, Boolean.valueOf(true)));
			arrow.spigot().setDamage(0.0D);
		}
	}

	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
			final Arrow arrow = (Arrow) event.getEntity();

			if (!arrow.hasMetadata("batHook")) {
				return;
			}
			final Player shooter = (Player) arrow.getShooter();
			final Location hitLoc = event.getEntity().getLocation();
			if (hitLoc == null) {
				return;
			}
			hitLoc.setPitch(shooter.getLocation().getPitch());
			hitLoc.setYaw(shooter.getLocation().getYaw());
			shooter.teleport(hitLoc);
			shooter.setFallDistance(0.0F);
			arrow.remove();
		}
	}
}
