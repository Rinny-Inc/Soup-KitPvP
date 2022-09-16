package io.noks.kitpvp.tasked;

import org.bukkit.scheduler.BukkitRunnable;

import io.noks.kitpvp.abstracts.EventTask;
import io.noks.kitpvp.enums.EventsType;

public class EventsTask extends EventTask {
	private static EventsTask instance = new EventsTask();

	public static EventsTask getInstance() {
		return instance;
	}

	private boolean spawned;

	private EventsType type;

	public void start() {
		if (this.type.getPosition().getX() == 0.0D && this.type.getPosition().getY() == 0.0D
				&& this.type.getPosition().getZ() == 0.0D) {
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
		doTask();
	}

	public void doTask() {
		this.spawned = false;
		new BukkitRunnable() {
			public void run() {
			}
		};
	}
}
