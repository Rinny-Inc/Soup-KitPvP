package io.noks.kitpvp.abstracts;

import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.enums.EventsType;
import io.noks.kitpvp.interfaces.BossTask;

public abstract class AbstractBossTask extends BukkitRunnable implements BossTask {
	protected boolean spawned;
	private EventsType type;
	protected boolean cancelled;
	protected int countdown;

	public void start() {
		if (this.spawned) {
			return;
		}
		if (this.type.getPosition().getX() == 0.0D && this.type.getPosition().getY() == 0.0D && this.type.getPosition().getZ() == 0.0D) {
			return;
		}
		this.spawn();
		this.spawned = true;
	}

	public void stop() {
		if (!this.spawned) {
			return;
		}
		this.spawned = false;
		this.countdown = 8400;
	}
	
	@Override
	public void run() {
		if (cancelled) {
			this.cancel();
			return;
		}
		this.doTask();
	}
	
	public boolean hasSpawned() {
		return this.spawned;
	}
	
	public abstract void spawn();
	
	public abstract void doTask();
}
