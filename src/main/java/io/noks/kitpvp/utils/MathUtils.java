package io.noks.kitpvp.utils;

import java.util.Random;

public class MathUtils {
	public static int getRandom(int from, int to) {
		return (from < to) ? (from + (new Random()).nextInt(Math.abs(to - from))) : (from - (new Random()).nextInt(Math.abs(to - from)));
	}
}
