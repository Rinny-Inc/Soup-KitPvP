package io.noks.kitpvp.utils;

import java.util.Random;

public interface MathUtils {
	default int getRandom(int from, int to) {
		return (from < to) ? (from + (new Random()).nextInt(Math.abs(to - from))) : (from - (new Random()).nextInt(Math.abs(to - from)));
	}
}
