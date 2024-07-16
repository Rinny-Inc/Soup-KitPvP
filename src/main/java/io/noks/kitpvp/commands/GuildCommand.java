package io.noks.kitpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.GuildRank;
import io.noks.kitpvp.exceptions.GuildExistenceException;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Guild;

public class GuildCommand implements CommandExecutor {
	private Main main;
	
	public GuildCommand(Main main) {
		this.main = main;
		main.getCommand("guild").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "Coming soon ^^");
			return false;
		}
		if (args.length == 0) {
			// TODO: send guild help
			return true;
		}
		final Player player = (Player) sender;
		final PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (args.length == 1) {
			// TODO
			// /guild disband
			// /guild open
			// /guild list
			return true;
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("create")) {
				if (pm.getGuild() != null) {
					player.sendMessage(ChatColor.RED + "Already in a guild!");
					return false;
				}
				final String name = args[1];
				if (name.length() > 24) {
					player.sendMessage(ChatColor.RED + "The guild name lenght need to be below or 24 character long!");
					return false;
				}
				try {
					this.main.getDataBase().createGuild(name, player.getUniqueId());
					player.sendMessage(ChatColor.GREEN + "Successfully created " + name + " guild!");
					return true;
				} catch (GuildExistenceException e) {
					player.sendMessage(ChatColor.RED + e.getMessage());
					return false;
				}
			}
			if (pm.getGuild() == null) {
				player.sendMessage(ChatColor.RED + "You need to be in a guild to do that!");
				return false;
			}
			final Guild guild = Guild.getGuildByPlayer(player.getUniqueId());
			
			
			// TODO: guild invite only for co_leader and leader
			
			
			if (guild.leaderUUID() != player.getUniqueId()) {
				player.sendMessage("Only leader is allowed to do these action!");
				return false;
			}
			final Player target = this.main.getServer().getPlayer(args[1]);
			if (target == null) {
				player.sendMessage(ChatColor.RED + "Player's not online! (This will be allowed soon enough)"); // TODO remove and allow offlineplayer promote
				return false;
			}
			if (!guild.getMembers().containsKey(target.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "Player's not in the guild!");
				return false;
			}
			if (args[0].equalsIgnoreCase("promote")) {
				final GuildRank rank = guild.getMemberRank(target.getUniqueId());
				final GuildRank newRank = GuildRank.getRankFromPower((byte) (rank.getPower() + 1));
				if (newRank == null) {
					player.sendMessage(ChatColor.RED + "Max promote rank reached!");
					return false;
				}
				guild.getMembers().remove(target.getUniqueId());
				guild.getMembers().put(target.getUniqueId(), newRank);
				// TODO: promote message
				return true;
			}
			if (args[0].equalsIgnoreCase("demote")) {
				final GuildRank rank = guild.getMemberRank(target.getUniqueId());
				final GuildRank newRank = GuildRank.getRankFromPower((byte) (rank.getPower() - 1));
				if (newRank == null) {
					player.sendMessage(ChatColor.RED + "Max demote rank reached!");
					return false;
				}
				guild.getMembers().remove(target.getUniqueId());
				guild.getMembers().put(target.getUniqueId(), newRank);
				// TODO: demote message
				return true;
			}
			
			// TODO
			// /guild kick <player>
			return true;
		}
		return false;
	}
}
