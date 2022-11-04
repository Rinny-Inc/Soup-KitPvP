package io.noks.kitpvp.commands;

import java.util.StringJoiner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;

public class AbilityListCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			return false;
		}
		final StringJoiner rarity = new StringJoiner(ChatColor.RESET + ", ");
		for (Rarity rarities : Rarity.values()) {
			if (rarities != Rarity.USELESS) rarity.add(rarities.getColor() + rarities.getName());
		}
		final StringJoiner ability = new StringJoiner(ChatColor.RESET + ", ");
		for (Abilities abilities : Main.getInstance().getAbilitiesManager().getAbilities()) {
			if (abilities.getRarity() != Rarity.USELESS) ability.add(abilities.getRarity().getColor() + abilities.getName());
		}
		sender.sendMessage(rarity.toString());
		sender.sendMessage("List of ability: " + ability.toString());
		return true;
	}
}
