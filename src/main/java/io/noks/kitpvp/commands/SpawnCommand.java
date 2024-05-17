package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.Main;
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
		
		final String spawnMessage = ChatColor.GREEN + "Teleporting to spawn..";
		if (!pm.getAbility().hasAbility() && !pm.hasCombatTag()) {
			player.teleport(player.getWorld().getSpawnLocation());
			player.sendMessage(spawnMessage);
			return true;
		}
		if (!pm.isInSpawn() && pm.hasCombatTag()) {
			return false;
		}
		new BukkitRunnable() {
			final Location oldLocation = player.getLocation();
			int i = 5;
			int ticks = 10;
			
			@Override
			public void run() {
				if ((oldLocation.getBlockX() != player.getLocation().getBlockX() || oldLocation.getBlockY() != player.getLocation().getBlockY() || oldLocation.getBlockZ() != player.getLocation().getBlockZ()) || pm.hasCombatTag()) {
					this.cancel();
					player.sendMessage(ChatColor.RED + "Back to spawn cancelled!");
					return;
				}
				ticks += 10;
				if (ticks % 20 == 0) {
					if (i == 0) {
						player.sendMessage(spawnMessage);
						pm.kill(true);
						player.teleport(player.getLocation().getWorld().getSpawnLocation());
						player.getInventory().clear();
						player.getInventory().setContents(main.getItemUtils().getSpawnItems(player.getName()));
						main.applySpawnProtection(player, true);
						this.cancel();
						return;
					}
					player.sendMessage("Teleporting in " + i + " seconds.");
					ticks = 0;
					i--;
				}
			}
		}.runTaskTimerAsynchronously(this.main, 0, 10);
		return true;
	}
}
