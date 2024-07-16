package io.noks.kitpvp.listeners.abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import io.noks.kitpvp.managers.PlayerManager;

public class Spider extends Abilities implements Listener {

	private Main main;
	public Spider(Main main) {
		super("Spider", new ItemStack(Material.WEB), Rarity.BETA, 30L, new String[] { ChatColor.AQUA + "Trap players in a 3x3 web" });
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@Override
	public ItemStack specialItem() {
		return new ItemStack(Material.WEB);
	}
	
	@Override
	public String specialItemName() {
		return ChatColor.RED + "Web Launcher";
	}
	
	@EventHandler
	public void onSpider(PlayerInteractEvent e) {
		if (!e.hasItem()) {
			return;
		}
		final Player p = e.getPlayer();
		final Action action = e.getAction();
		final PlayerManager pm = PlayerManager.get(p.getUniqueId());
		if (action == Action.RIGHT_CLICK_AIR && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.WEB && pm.hasAbility(this)) {
			if (pm.hasActiveAbilityCooldown()) {
				final double cooldown = pm.getActiveAbilityCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in " + df.format(cooldown) + " seconds.");
				return;
			}
			final Snowball web = p.launchProjectile(Snowball.class, p.getLocation().getDirection().multiply(3.0D));
			web.setShooter(p);
			web.setMetadata("web", new FixedMetadataValue(this.main, Boolean.valueOf(true)));
			pm.applyAbilityCooldown();
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() instanceof Player) {
			final Player player = (Player) event.getEntity().getShooter();
			final PlayerManager pm = PlayerManager.get(player.getUniqueId());
			
			if (!pm.hasAbility(this)) {
				return;
			}
			final Snowball ball = (Snowball) event.getEntity();
			
			if (!ball.hasMetadata("web")) {
				return;
			}
			Block block = ball.getLocation().getBlock();
			if (event.getHitEntity() != null) {
				block = event.getHitEntity().getLocation().getBlock();
				block.getLocation().setY(ball.getLocation().getY());
			}
			final List<Block> webs = new ArrayList<Block>();
			for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block relative = block.getRelative(x, 0, z);
                    if (relative.getType() == Material.AIR) {
                        relative.setType(Material.WEB);
                        webs.add(relative);
                    }
                }
            }
			new BukkitRunnable() {
				
				@Override
				public void run() {
					for (Block blocks : webs) {
						blocks.setType(Material.AIR);
					}
				}
			}.runTaskLater(this.main, 10 * 20L);
		}
	}
}
