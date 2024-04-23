package io.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.avaje.ebean.validation.NotNull;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Ninja extends Abilities implements Listener {
	private final @NotNull Main plugin;
	private @Nullable UUID target;

	public Ninja(Main main) {
		super("Ninja", new ItemStack(Material.WOOL, 1, (short) 15), Rarity.LEGENDARY, 20L, new String[] { ChatColor.AQUA + "Teleport yourself behind your opponent" });
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	@EventHandler
	public void onToggleSneak(PlayerToggleSneakEvent event) {
		final Player player = event.getPlayer();
		if (target != null) {
			if (!event.isSneaking())
				return;
			final Player target = Bukkit.getPlayer(this.target);
			if (target == null) {
				return;
			}
			if (!player.canSee(target) || !target.canSee(player)) {
				this.target = null;
				return;
			}
			if (PlayerManager.get(this.target).isInSpawn()) {
				this.target = null;
				return;
			}
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());
			if (pm.getAbility().hasAbility(this)) {
				if (pm.getAbility().hasActiveCooldown()) {
					double cooldown = pm.getAbility().getActiveCooldown().longValue() / 1000.0D;
					player.sendMessage(ChatColor.RED + "You can use your ability in " + (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
					return;
				}
				pm.getAbility().applyCooldown();
				float nang = target.getLocation().getYaw() + 90.0F;
				if (nang < 0.0F)
					nang += 360.0F;
				final double nX = Math.cos(Math.toRadians(nang));
				final double nZ = Math.sin(Math.toRadians(nang));
				Location behindTargetLocation = new Location(player.getWorld(), target.getLocation().getX() - nX,
						target.getLocation().getY(), target.getLocation().getZ() - nZ, target.getLocation().getYaw(),
						target.getLocation().getPitch());
				if (behindTargetLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
					behindTargetLocation = target.getLocation();
				}
				if (behindTargetLocation.getBlock().getType() != Material.AIR) {
					behindTargetLocation = target.getLocation();
				}
				player.teleport(behindTargetLocation);
				for (int i = 0; i < 6; i++) {
					player.getWorld().playEffect(player.getLocation(), Effect.LARGE_SMOKE, 10);
				}
				player.setFallDistance(0.0F);
				this.target = null;
			}
		}
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final Player damager = (Player) event.getDamager();
			final PlayerManager dm = PlayerManager.get(damager.getUniqueId());

			if (dm.getAbility().hasAbility(this)) {
				Player damaged = (Player) event.getEntity();
				if (this.target == damaged.getUniqueId()) {
					return;
				}
				this.target = damaged.getUniqueId();
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (this.target != null && this.target == event.getPlayer().getUniqueId()) {
			this.target = null;
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			if (this.target != null && this.target == event.getEntity().getUniqueId()) {
				this.target = null;
			}
		}
	}
}
