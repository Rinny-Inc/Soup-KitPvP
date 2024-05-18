package io.noks.kitpvp.task;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.Abilities;
import io.noks.kitpvp.enums.RefreshType;
import io.noks.kitpvp.managers.PlayerManager;

public class MapTask extends BukkitRunnable {
	private int taskId = -1;
	public Set<PlayerManager> playersInMap;
	private final Main main;
	public MapTask(Main main) {
		this.main = main;
	}
	
	@Override
	public void run() {
		if (this.playersInMap.isEmpty()) {
			this.clearTask();
			return;
		}
		for (PlayerManager players : this.playersInMap) {
			if (players.ability() != null && players.ability().hasCooldown()) {
				Abilities ability = players.ability();
				if (players.hasActiveCooldown()) {
					this.updateXpBar(players, ability);
				} else if (!players.hasReceivedEndCooldownMessage()) {
					players.getPlayer().sendMessage(ChatColor.GRAY + "You may now use " + ChatColor.RED + (ability.specialItem() != null ? ability.specialItemName() : ability.getName()));
					players.updateHasReceivedEndCooldownMessage();
				}
			}
			if (players.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
				players.refreshScoreboardLine(RefreshType.COMBATTAG);
			}
		}
	}

	private void updateXpBar(PlayerManager pm, Abilities ability) {
		Long activeCooldown = pm.getActiveCooldown();
	    float xpPercentage = Math.min(99.9f, ((float) activeCooldown / (ability.getCooldown() * 1000)) * 100);
	    if (xpPercentage < 0.0) {
	    	xpPercentage = 0.0f;
	    }
	    int level = activeCooldown.intValue() / 1000;
	    final Player player = pm.getPlayer();
	    if (level != player.getLevel()) {
	    	player.setLevel(level);
	    }
	    player.setExp(xpPercentage / 100);
	}
	
	private <T> Predicate<T> not(Predicate<T> p) { 
		return p.negate();
	}
	
	public MapTask startTask() {
		if (this.taskId == -1) {
			this.playersInMap = new HashSet<PlayerManager>(PlayerManager.players.values().stream().filter(not(PlayerManager::isInSpawn)).collect(Collectors.toSet()));
			this.taskId = this.runTaskTimerAsynchronously(this.main, 0, 1).getTaskId();
		}
		return this;
	}
	
	public void clearTask() {
		if (this.taskId != -1) {
			this.cancel();
			this.taskId = -1;
			this.playersInMap = null;
		}
	}
}
