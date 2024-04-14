package io.noks.kitpvp.task;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.RefreshType;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Ability;

public class MapTask extends BukkitRunnable {
	private int taskId = -1;
	private Main main;
	public MapTask(Main main) {
		this.main = main;
	}
	
	@Override
	public void run() {
		final List<PlayerManager> playersInMap = PlayerManager.players.values().stream().filter(not(PlayerManager::isInSpawn)).collect(Collectors.toList());
		if (playersInMap.isEmpty()) {
			this.clearTask();
			return;
		}
		for (PlayerManager players : playersInMap) {
			if (players.getAbility().get().hasCooldown() && players.getAbility().hasActiveCooldown()) {
				this.updateXpBar(players, players.getAbility());
			}
			if (players.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) {
				continue;
			}
			players.refreshScoreboardLine(RefreshType.COMBATTAG);
		}
	}

	
	private void updateXpBar(PlayerManager pm, Ability ability) {
	    final float xpPercentage = Math.max(0.0f, Math.min(99.9f, ((float) pm.getAbility().getActiveCooldown() / (ability.get().getCooldown() * 1000)) * 100));
	    pm.getPlayer().setExp(xpPercentage / 100);
	}
	
	private static <T> Predicate<T> not(Predicate<T> p) { 
		return p.negate();
	}
	
	public MapTask startTask() {
		if (this.taskId == -1) {
			this.taskId = this.runTaskTimerAsynchronously(this.main, 0, 1).getTaskId();
		}
		return this;
	}
	
	public void clearTask() {
		if (this.taskId != -1) {
			this.cancel();
			this.taskId = -1;
		}
	}
}
