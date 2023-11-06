package io.noks.kitpvp.task.event;

import javax.annotation.Nullable;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.abstracts.EventTask;
import io.noks.kitpvp.enums.EventsType;

public class EventsTask extends EventTask {
	private final Main main;
	public EventsTask(Main main) {
		this.main = main;
	}

	private boolean spawned;
	private EventsType type;
	private @Nullable BukkitTask currentTask;

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
		doTask();
	}

	public void doTask() {
		this.currentTask = new BukkitRunnable() {
			
			@Override
			public void run() {
				// TODO chat announcement
			}
		}.runTaskTimerAsynchronously(this.main, 20l, 10l);
	}
}
