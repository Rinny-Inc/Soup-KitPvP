package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.managers.PlayerManager;

public class SpawnCommand implements CommandExecutor {
	
	private final Main main;
	public SpawnCommand(Main main) {
		this.main = main;
		main.getCommand("spawn").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /spawn");
			return false;
		}
		final Player player = (Player) sender;
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		
		final String spawnMessage = ChatColor.GREEN + "Warping to spawn..";
		if (!pm.hasAbility() && !pm.hasCombatTag()) {
			player.teleport(player.getWorld().getSpawnLocation());
			player.sendMessage(spawnMessage);
			return true;
		}
		if (pm.hasAbility() && pm.hasCombatTag()) {
			return false;
		}
		if (pm.currentTask != null) {
			return false;
		}
		pm.currentTask = new BukkitRunnable() {
			final Location oldLocation = player.getLocation();
			int i = 5;
			int ticks = 19;
			
			@Override
			public void run() {
				if ((oldLocation.getBlockX() != player.getLocation().getBlockX() || oldLocation.getBlockY() != player.getLocation().getBlockY() || oldLocation.getBlockZ() != player.getLocation().getBlockZ()) || pm.hasCombatTag()) {
					this.cancel();
					player.sendMessage(ChatColor.RED + "Back to spawn cancelled!");
					pm.currentTask = null;
					return;
				}
				ticks += 1;
				if (ticks % 20 == 0) {
					if (i == 0) {
						player.sendMessage(spawnMessage);
						pm.kill(true);
						player.teleport(player.getLocation().getWorld().getSpawnLocation());
						player.getInventory().clear();
						if (player.getArrowsStuck() > 0) {
							player.setArrowsStuck(0);
						}
						player.getInventory().setArmorContents(null);
						player.getInventory().setContents(main.getSpawnItems(player.getName()));
						player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
						player.setHealth(20.0D);
						main.applySpawnProtection(player, true);
						this.cancel();
						pm.currentTask = null;
						return;
					}
					player.sendMessage(ChatColor.DARK_AQUA + "Warping in " + ChatColor.WHITE + i + ChatColor.DARK_AQUA + " seconds.");
					ticks = 0;
					i--;
				}
			}
		}.runTaskTimerAsynchronously(this.main, 0, 1);
		return true;
	}
	
	private void clearInventory(Player player, Abilities ability) {
		for (ItemStack item : player.getInventory().getContents()) {
			Material type = item.getType();
			if (type == ability.specialItem().getType() || type == Material.MUSHROOM_SOUP || type == ability.sword().getType()) {
				item.setType(null);
			}
		}
	}
}
