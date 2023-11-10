package io.noks.kitpvp.commands;

import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.noks.kitpvp.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.util.com.google.common.collect.Maps;

public class ReportCommand implements CommandExecutor {
	private int cooldownTime = 30;
	private Map<UUID, Long> cooldowns = Maps.newConcurrentMap(); // TODO: do a cooldown class
	
	private Main main;
	public ReportCommand(Main main) {
		this.main = main;
		main.getCommand("report").setExecutor(main);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /report <player> <reason>");
			return false;
		}
		final Player target = this.main.getServer().getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(ChatColor.RED + "This player is not online.");
			return false;
		}
		final Player p = (Player) sender;
		if (target == p) {
			p.sendMessage(ChatColor.RED + "You can't report yourself!");
			return false;
		}
		if (this.cooldowns.containsKey(p.getUniqueId())) {
			long secondsLeft = ((Long) this.cooldowns.get(p.getUniqueId())).longValue() / 1000L + this.cooldownTime - System.currentTimeMillis() / 1000L;
			if (secondsLeft > 0L) {
				p.sendMessage(ChatColor.RED + "You cant report for another " + secondsLeft + " seconds!");
				return false;
			}
		}
		final StringJoiner reason = new StringJoiner(" ");
		for (int i = 1; i < args.length; i++) {
			reason.add(args[i]);
		}

		final TextComponent l1 = new TextComponent();
		l1.setText("(");
		l1.setColor(ChatColor.GRAY);

		final TextComponent l1a = new TextComponent();
		l1a.setText("REPORT");
		l1a.setColor(ChatColor.DARK_RED);
		l1a.setBold(Boolean.valueOf(true));

		final TextComponent l1b = new TextComponent();
		l1b.setText(") ");
		l1b.setColor(ChatColor.GRAY);

		final TextComponent l1c = new TextComponent();
		l1c.setText(p.getName());
		l1c.setColor(ChatColor.YELLOW);

		final TextComponent l1d = new TextComponent();
		l1d.setText(" has reported ");
		l1d.setColor(ChatColor.GRAY);

		final TextComponent l1e = new TextComponent();
		l1e.setText(target.getName());
		l1e.setColor(ChatColor.RED);

		final TextComponent l1f = new TextComponent();
		l1f.setText(" for ");
		l1f.setColor(ChatColor.GRAY);

		final TextComponent l1g = new TextComponent();
		l1g.setText("\"" + reason.toString() + "\" ");
		l1g.setColor(ChatColor.GREEN);

		final TextComponent l1h = new TextComponent();
		l1h.setText("[Teleport]");
		l1h.setColor(ChatColor.BLUE);
		l1h.setBold(true);
		l1h.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.GREEN + "Click to teleport you to " + target.getName())).create()));
		l1h.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + target.getName()));

		l1.addExtra(l1a);
		l1.addExtra(l1b);
		l1.addExtra(l1c);
		l1.addExtra(l1d);
		l1.addExtra(l1e);
		l1.addExtra(l1f);
		l1.addExtra(l1g);
		l1.addExtra(l1h);
		for (Player staff : this.main.getServer().getOnlinePlayers()) {
			if (staff.hasPermission("report.receive")) {
				staff.spigot().sendMessage(l1);
			}
		}
		this.cooldowns.put(p.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
		p.sendMessage(ChatColor.GREEN + "You have reported " + target.getName() + " for " + reason.toString() + ".");
		return true;
	}
}
