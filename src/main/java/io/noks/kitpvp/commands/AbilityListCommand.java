package io.noks.kitpvp.commands;

import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.Rarity;
import net.minecraft.util.com.google.common.collect.Lists;

public class AbilityListCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			return false;
		}
		final StringJoiner rarity = new StringJoiner(ChatColor.RESET + ", ");
		for (Rarity rarities : Rarity.values()) {
			if (rarities != Rarity.USELESS) rarity.add(rarities.getColor() + rarities.getName());
		}
		List<Abilities> list = Lists.newArrayList(Main.getInstance().getAbilitiesManager().getAbilities());
		list.sort(Comparator.comparing(Abilities::getRarity));
		final StringJoiner ability = new StringJoiner(ChatColor.RESET + ", ");
		for (Abilities abilities : list) {
			if (abilities.getRarity() != Rarity.USELESS) ability.add(abilities.getRarity().getColor() + abilities.getName());
		}
		sender.sendMessage(rarity.toString());
		sender.sendMessage("List of ability: " + ability.toString());
		return true;
	}
}
