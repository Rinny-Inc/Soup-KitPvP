package us.noks.kitpvp.commands;

import java.util.StringJoiner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.enums.Rarity;

public class AbilityListCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			return false;
		}
		StringJoiner ability = new StringJoiner(ChatColor.RESET + ", ");
		for (AbilitiesEnum abilities : AbilitiesEnum.values()) {
			if (abilities.getRarity() != Rarity.USELESS)
				ability.add(abilities.getRarity().formatDyeColorToChatColor() + abilities.getName());
		}
		sender.sendMessage("List of ability: " + ability.toString());
		return true;
	}
}
