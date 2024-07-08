package io.noks.kitpvp.interfaces;

import java.util.Random;

public interface MathHelper {
	default int getRandom(int from, int to) {
		return (from < to) ? (from + (new Random()).nextInt(Math.abs(to - from))) : (from - (new Random()).nextInt(Math.abs(to - from)));
	}
}
