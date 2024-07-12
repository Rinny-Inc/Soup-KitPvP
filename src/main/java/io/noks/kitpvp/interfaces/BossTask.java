package io.noks.kitpvp.interfaces;

public interface BossTask {
	void start();
	void stop();
	void doTask();
	void spawn();
	boolean hasSpawned();
}
