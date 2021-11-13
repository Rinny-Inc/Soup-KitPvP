package us.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import us.noks.kitpvp.Main;
import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.managers.PlayerManager;
import us.noks.kitpvp.managers.caches.Ability;
import us.noks.kitpvp.utils.ItemUtils;

public class Ganjaman implements Listener {
	private Main plugin;

	public Ganjaman(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onJointSmoking(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action action = event.getAction();
		Ability ability = PlayerManager.get(p.getUniqueId()).getAbility();
		if (action == Action.RIGHT_CLICK_AIR && p.getItemInHand().getType() != null
				&& p.getItemInHand().getType() == Material.WHEAT && ability.hasAbility(AbilitiesEnum.GANJAMAN)) {
			if (ability.hasAbilityCooldown()) {
				double cooldown = ability.getAbilityCooldown().longValue() / 1000.0D;
				p.sendMessage(ChatColor.RED + "You can use your ability in "
						+ (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
				return;
			}
			if (p.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
				p.sendMessage(ChatColor.RED + "You will badtrip if you take another puff!");
				return;
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 300, 1));
			p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
			p.setHealth(Math.min(p.getHealth() * 2.0D, p.getMaxHealth()));
			p.updateInventory();
			p.sendMessage(ChatColor.GREEN + "You are now high to the sky.");
			p.playSound(p.getLocation(), Sound.FIRE_IGNITE, 0.85F, 0.9F);
			p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.PARTICLE_SMOKE, 17, 0, 0.0F, 0.0F, 0.0F, 0.0F,
					32, 5);
		}
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			Ability damagedAbility = PlayerManager.get(damaged.getUniqueId()).getAbility();

			if (damagedAbility.hasAbility(AbilitiesEnum.GANJAMAN)
					&& damaged.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
				double rand = Math.random() * 100.0D;
				if (rand > 20.0D) {
					return;
				}
				Player damager = (Player) event.getDamager();
				if (!damager.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
					damager.sendMessage(ChatColor.GOLD + "The smoke emanating from the Ganjaman relaxes you...");
					damaged.getWorld().spigot().playEffect(damaged.getLocation(), Effect.LARGE_SMOKE, 17, 0, 0.0F, 0.0F,
							0.0F, 0.045F, 16, 7);
					damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, (new Random()).nextInt(1)));
					damager.addPotionEffect(
							new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, (new Random()).nextInt(1)));
				}
			}
		}
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = event.getEntity().getKiller();
			Ability ability = PlayerManager.get(player.getUniqueId()).getAbility();

			if (ability.hasAbility(AbilitiesEnum.GANJAMAN))
				if (player.getInventory().firstEmpty() == -1 && !player.getInventory().contains(Material.WHEAT)) {
					player.getWorld().dropItem(player.getLocation(),
							ItemUtils.getInstance().getItemStack(new ItemStack(Material.WHEAT, 2),
									ChatColor.RED + ability.getAbility().getSpecialItemName(), null));
				} else {
					player.getInventory().addItem(
							new ItemStack[] { ItemUtils.getInstance().getItemStack(new ItemStack(Material.WHEAT, 2),
									ChatColor.RED + ability.getAbility().getSpecialItemName(), null) });
				}
		}
	}
}
