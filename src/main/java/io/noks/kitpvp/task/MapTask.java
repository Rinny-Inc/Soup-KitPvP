package io.noks.kitpvp.task;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.managers.PlayerManager;

public class MapTask extends BukkitRunnable {
	
	@Override
	public void run() {
		final List<PlayerManager> playersInMap = PlayerManager.players.values().stream().filter(not(PlayerManager::isInSpawn)).collect(Collectors.toList());
		if (playersInMap.isEmpty()) {
			this.cancel();
			return;
		}
		for (PlayerManager players : playersInMap) {
			// Ability Cooldown in xp bar 
			// TODO: check if they're on cooldown
			// TODO: execute cooldown bar
			
			// TODO: Back To Spawn
			
			// TODO: CombatTag (scoreboard update too)
		}
	}
	
	private void updateXpBar(PlayerManager pm) {
	    final float xpPercentage = Math.max(0.0f, Math.min(99.9f, ((float) pm.getCurrentCombatTag().getRemainingTime() / (14 * 1000)) * 100));
	    pm.getPlayer().setExp(xpPercentage / 100);
	}
	
	private String format(int sec) {
		return String.format("%02d:%02d", new Object[] { Integer.valueOf(sec / 60), Integer.valueOf(sec % 60) });
	}
	
	private static <T> Predicate<T> not(Predicate<T> p) { 
		return p.negate();
	}
}
