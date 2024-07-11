package io.noks.kitpvp.abstracts;

import io.noks.kitpvp.enums.EventsType;
import io.noks.kitpvp.interfaces.BossTask;

public abstract class AbstractBossTask implements BossTask, Runnable {
	private boolean spawned;
	private EventsType type;

	public void start() {
		if (this.type.getPosition().getX() == 0.0D && this.type.getPosition().getY() == 0.0D && this.type.getPosition().getZ() == 0.0D) {
			return;
		}
		if (this.spawned) {
			return;
		}
		this.spawned = true;
	}

	public void stop() {
		if (!this.spawned) {
			return;
		}
		this.spawned = false;
		this.run();
	}
}
